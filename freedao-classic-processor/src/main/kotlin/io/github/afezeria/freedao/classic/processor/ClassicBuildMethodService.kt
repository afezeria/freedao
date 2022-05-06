package io.github.afezeria.freedao.classic.processor

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import io.github.afezeria.freedao.NoRowReturnedException
import io.github.afezeria.freedao.TooManyResultException
import io.github.afezeria.freedao.classic.runtime.AutoFill
import io.github.afezeria.freedao.classic.runtime.LogHelper
import io.github.afezeria.freedao.classic.runtime.SqlSignature
import io.github.afezeria.freedao.processor.core.*
import io.github.afezeria.freedao.processor.core.method.MethodHandler
import io.github.afezeria.freedao.processor.core.method.RealParameter
import io.github.afezeria.freedao.processor.core.method.ResultHelper
import io.github.afezeria.freedao.processor.core.method.XmlTemplateMethod
import io.github.afezeria.freedao.processor.core.spi.BuildMethodService
import io.github.afezeria.freedao.processor.core.template.TemplateHandler
import io.github.afezeria.freedao.processor.core.template.TemplateHandler.Companion.toVarName
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.PrimitiveType

/**
 *
 * @author afezeria
 */
class ClassicBuildMethodService : BuildMethodService {
    /**
     * 给同名方法添加计数，避免出现同名类变量
     */
    private val methodCounter: MutableMap<String, Int> = mutableMapOf()

    override fun build(methodHandler: MethodHandler): CodeBlock {

        val counter = methodCounter.compute(
            "${methodHandler.daoHandler.element.qualifiedName}.${methodHandler.name}"
        ) { _, v ->
            v?.inc() ?: 0
        }!!
        val signVar = addMethodSignatureField(methodHandler, counter)
        return CodeBlock.builder()
            .addStatement(
                "return $contextVar.proxy($signVar${
                    methodHandler.parameters.filterIsInstance<RealParameter>()
                        .joinToString { it.name }
                        .let { if (it.isEmpty()) "" else ", $it" }
                })"
            ).build()
    }


    /**
     * 给dao添加表示sql签名的静态属性
     * @param methodHandler MethodHandler
     */
    private fun addMethodSignatureField(methodHandler: MethodHandler, counter: Int): String {
        val signFieldName = "${methodHandler.name}_${counter}_sign"
        val field = FieldSpec.builder(
            SqlSignature::class.type(
                methodHandler.resultHelper.returnType,
                methodHandler.resultHelper.originalItemType
            ).typeName,
            signFieldName,
            Modifier.PRIVATE,
            Modifier.FINAL,
        ).initializer(
            CodeBlock.builder().apply {
                methodHandler.apply {
                    add("new \$T<>(\n", SqlSignature::class.java)
                    indent()
                    //statementType
                    add("\$T.\$L,\n", io.github.afezeria.freedao.StatementType::class.java, statementType)
                    add("\$L,\n", methodHandler is XmlTemplateMethod)
                    add("this.getClass(),\n")
                    add("\$S,\n", this.name)
                    add("\$T.class,\n", resultHelper.returnType.erasure())
                    resultHelper.containerType?.apply {
                        add("\$T.class,\n", erasure())
                    } ?: add("null,\n")
                    add("\$T.class,\n", resultHelper.itemType.erasure())

                    add("new Class[]{\n")
                    indent()
                    parameters.filterIsInstance<RealParameter>().let { params ->
                        params.forEachIndexed { index, it ->
                            add("\$T.class${if (index < (params.size - 1)) "," else ""}\n", it.type.erasure().typeName)
                        }
                    }
                    unindent()
                    add("},\n")
                    add("\$L,\n", addSqlBuilderFieldTemp(this))
                    add("\$L,\n", addExecutorFieldTemp(this))


                    when (methodHandler.statementType) {
                        io.github.afezeria.freedao.StatementType.INSERT, io.github.afezeria.freedao.StatementType.UPDATE, io.github.afezeria.freedao.StatementType.DELETE -> {
                            add("null\n")
                        }
                        else -> {
                            add(buildSelectResultHandler(this))
                        }
                    }
                    unindent()
                    add(")")
                }
            }.build()
        ).build()
        methodHandler.daoHandler.classBuilder.addField(field)
        return signFieldName
    }

    private fun addSqlBuilderFieldTemp(methodHandler: MethodHandler): CodeBlock {
        return CodeBlock.builder().apply {
            add("_params -> {\n")
            indent()
            methodHandler.parameters.forEachIndexed { index, parameter ->
                var type = parameter.type
                if (type is PrimitiveType) {
                    type = typeUtils.boxedClass(type).asType()
                }
                addStatement("\$T ${parameter.name.toVarName()} = (\$T) _params[\$L]", type, type, index)
            }
            if (EnableAutoFill(methodHandler)) {
                AutoFillStruct(methodHandler)?.apply AutoFill@{
                    val props = autoFillProperties.mapNotNull {
                        val ann = it.element.getAnnotation(AutoFill::class.java)!!
                        if (ann.before
                            && ((ann.insert && methodHandler.statementType == io.github.afezeria.freedao.StatementType.INSERT)
                                    || ann.update && methodHandler.statementType == io.github.afezeria.freedao.StatementType.UPDATE)
                        ) {
                            ann to it
                        } else {
                            null
                        }
                    }
                    if (props.isEmpty()) {
                        return@AutoFill
                    }
                    if (isCollection) {
                        beginControlFlow(
                            "for (\$T $itemVar : ${methodHandler.parameters[index].name.toVarName()})",
                            type,
                        )
                    } else {
                        addStatement(
                            "\$T $itemVar = (\$T) _params[\$L]",
                            methodHandler.parameters[index].type,
                            methodHandler.parameters[index].type,
                            index
                        )
                    }
                    props.forEach { (ann, prop) ->
                        addStatement(
                            "$itemVar.${prop.setterName}((\$T) \$T.gen($itemVar, \$S, \$T.class))",
                            prop.type,
                            ann.mirroredType { generator },
                            prop.name,
                            prop.type
                        )
                    }
                    if (isCollection) {
                        endControlFlow()
                    }
                }
            }
            add("\n")
            add(methodHandler.sqlBuildCodeBlock)
            add("\n")
            addStatement("\$T l_sql_0 = l_builder_0.toString()", String::class.type)
            addStatement("return new Object[]{l_sql_0, ${TemplateHandler.sqlArgsVarName}}")
            unindent()
            add("}")
        }.build()
    }

    private fun addExecutorFieldTemp(methodHandler: MethodHandler): CodeBlock {
        val resultHelper = methodHandler.resultHelper
        return CodeBlock.builder().apply {
            add(
                "($connVar, $methodArgsVar, $sqlVar, $argsVar, $resultHandlerVar) -> {\n",
                String::class.type,
                List::class.type(Any::class.type)
            )
            indent()
            beginControlFlow("if ($logVar.isDebugEnabled())")
            addStatement("\$T.logSql($logVar, $sqlVar)", LogHelper::class.type)
            addStatement("\$T.logArgs($logVar, $argsVar)", LogHelper::class.type)
            endControlFlow()


            if (EnableAutoFill(methodHandler)) {
                beginControlFlow(
                    "try (\$T $stmtVar = $connVar.prepareStatement($sqlVar, \$T.RETURN_GENERATED_KEYS))",
                    PreparedStatement::class.type,
                    Statement::class.type
                )
            } else {
                beginControlFlow(
                    "try (\$T $stmtVar = $connVar.prepareStatement($sqlVar))",
                    PreparedStatement::class.type
                )
            }

            beginControlFlow("for (int $idxVar = 0; $idxVar < $argsVar.size(); $idxVar++)")
            addStatement("$stmtVar.setObject($idxVar + 1, $argsVar.get($idxVar))")
            endControlFlow()

            addStatement("$stmtVar.execute()")

            when (methodHandler.statementType) {
                io.github.afezeria.freedao.StatementType.INSERT, io.github.afezeria.freedao.StatementType.UPDATE, io.github.afezeria.freedao.StatementType.DELETE -> {
                    handeUpdateMethodResultMapping(methodHandler, resultHelper)
                }
                else -> {
                    handeSelectMethodResultMapping(resultHelper)
                }
            }

            endControlFlow()
            unindent()
            add("}")
        }.build()
    }

    private fun CodeBlock.Builder.handeUpdateMethodResultMapping(
        methodHandler: MethodHandler,
        resultHelper: ResultHelper
    ) {
        if (EnableAutoFill(methodHandler)) {
            AutoFillStruct(methodHandler)?.apply {
                addStatement("\$T $resultSetVar = $stmtVar.getGeneratedKeys()", ResultSet::class.type)

                if (isCollection) {
                    addStatement("\$T $containerVar = (\$T) $methodArgsVar[${index}]", collectionType, collectionType)
                    addStatement("\$T $itemVar = null", type)
                    addStatement("int $idxVar = 0")
                    beginControlFlow("while ($resultSetVar.next())")
                    addStatement("$itemVar = $containerVar.get($idxVar)")
                } else {
                    addStatement("\$T $itemVar = (\$T) $methodArgsVar[${index}]", type, type)
                    beginControlFlow("while ($resultSetVar.next())")
                }
                autoFillProperties
                    .forEach {
                        val ann = it.element.getAnnotation(AutoFill::class.java)!!
                        if (ann.before.not()
                            && ((ann.insert && methodHandler.statementType == io.github.afezeria.freedao.StatementType.INSERT)
                                    || ann.update && methodHandler.statementType == io.github.afezeria.freedao.StatementType.UPDATE)
                        ) {
                            val targetType = it.type
                            //将generatedKeys的值填充回实体类
                            if (it.column.resultTypeHandle != null) {
                                //有类型转换
                                addStatement(
                                    "$itemVar.${it.setterName}((\$T) \$T.handleResult(\$T.gen($resultSetVar, \$S, Object.class), \$T.class))",
                                    targetType,
                                    it.column.resultTypeHandle,
                                    ann.mirroredType { generator },
                                    it.column.name,
                                    targetType,
                                )
                            } else {
                                addStatement(
                                    "$itemVar.${it.setterName}((\$T) \$T.gen($resultSetVar, \$S, \$T.class))",
                                    targetType,
                                    ann.mirroredType { generator },
                                    it.column.name,
                                    targetType,
                                )
                            }
                        }
                    }
                if (isCollection) {
                    addStatement("$idxVar++")
                    endControlFlow()
                } else {
                    addStatement("break")
                    endControlFlow()
                }
            }
        }
        if (resultHelper.itemType.isSameType(Int::class)) {
            addStatement("return $stmtVar.getUpdateCount()")
        } else {
            addStatement("return $stmtVar.getLargeUpdateCount()")
        }
    }

    private fun CodeBlock.Builder.handeSelectMethodResultMapping(
        resultHelper: ResultHelper
    ) {

        addStatement("\$T $resultSetVar = $stmtVar.getResultSet()", ResultSet::class.type)
        val returnMultipleRow = resultHelper.containerType != null
        if (returnMultipleRow) {
            //List<Map<String, Object>> list = new ArrayList<>();
            val diamondStr = if ((resultHelper.containerType!!.asElement() as TypeElement).typeParameters.isEmpty()) {
                ""
            } else {
                "<>"
            }
            addStatement(
                "\$T $containerVar = new \$T$diamondStr()",
                resultHelper.returnType,
                resultHelper.containerType
            )
            addStatement(
                "\$T $itemVar = null",
                (resultHelper.returnType as DeclaredType).findTypeArgument(Collection::class.type, "E"),
            )
        } else {
            addStatement(
                "\$T $itemVar = null",
                resultHelper.returnType.boxed(),
            )
        }

        beginControlFlow("while ($resultSetVar.next())")
        //__item = __handler.handle(__rs, __item)
        addStatement("$itemVar = $resultHandlerVar.handle($resultSetVar, $itemVar)")
        if (returnMultipleRow) {
            addStatement("$containerVar.add($itemVar)")
        } else {
            beginControlFlow("if ($resultSetVar.next())")
            addStatement("throw new \$T()", TooManyResultException::class.java)
            endControlFlow()
        }
        endControlFlow()
        if (resultHelper.returnType is PrimitiveType) {
            beginControlFlow("if ($itemVar == null)")
            addStatement("throw new \$T()", NoRowReturnedException::class.java)
            endControlFlow()
        }
        if (returnMultipleRow) {
            addStatement("return $containerVar")
        } else {
            addStatement("return $itemVar")
        }
    }

    private fun buildSelectResultHandler(methodHandler: MethodHandler): CodeBlock {
        return CodeBlock.builder().apply {
            add("($resultSetVar,$itemVar) -> {\n")
            indent()
            methodHandler.apply {
                if (methodHandler.resultHelper.isStructuredItem) {            //返回结构化数据
                    val diamondStr = if ((resultHelper.itemType.asElement() as TypeElement).typeParameters.isEmpty()) {
                        ""
                    } else {
                        "<>"
                    }
                    if (resultHelper.itemType.isAssignable(Map::class)) {
                        //集合内容为map
                        addStatement("$itemVar = new \$T$diamondStr()", resultHelper.itemType)
                        if (methodHandler.mappings.isNotEmpty()) {
                            methodHandler.mappings.forEach {
                                when {
                                    //有类型转换器
                                    it.typeHandler != null -> {
                                        val targetType = it.targetType ?: Any::class.type
                                        addStatement(
                                            "$itemVar.put(\$S, (\$T) \$T.handleResult($resultSetVar.getObject(\$S), \$T.class))",
                                            it.source,
                                            targetType,
                                            it.typeHandler,
                                            it.source,
                                            targetType,
                                        )
                                    }
                                    //map的值的类型参数不为Object时调用jdbc的显示转换类型函数
                                    !resultHelper.mapValueType!!.isSameType(Any::class) -> {
                                        addStatement(
                                            "$itemVar.put(\$S, $resultSetVar.getObject(\$S, \$T.class))",
                                            it.source,
                                            it.source,
                                            resultHelper.mapValueType
                                        )
                                    }
                                    else -> {
                                        addStatement(
                                            "$itemVar.put(\$S, $resultSetVar.getObject(\$S))",
                                            it.source,
                                            it.source
                                        )
                                    }
                                }
                            }
                        } else {
                            //未声明结果映射规则时将返回的所有结果都添加到map中
                            addStatement(
                                "\$T $metaDataVar = $resultSetVar.getMetaData()",
                                ResultSetMetaData::class.type
                            )
                            addStatement("int $columnCountVar = $metaDataVar.getColumnCount()")
                            beginControlFlow("for (int $idxVar = 1; $idxVar <= $columnCountVar; $idxVar++)")
                            addStatement("String $labelVar = $metaDataVar.getColumnLabel($idxVar)")
                            if (resultHelper.mapValueType!!.isSameType(Any::class)) {
                                addStatement("$itemVar.put($labelVar, $resultSetVar.getObject($labelVar))")
                            } else {
                                addStatement(
                                    "$itemVar.put($labelVar, $resultSetVar.getObject($labelVar, \$T.class))",
                                    resultHelper.mapValueType
                                )
                            }
                            endControlFlow()
                        }
                    } else {
                        add("$itemVar = new \$T$diamondStr(\n", resultHelper.itemType)
                        indent()
                        methodHandler.mappings.filter { it.constructorParameterIndex > -1 }
                            .sortedBy { it.constructorParameterIndex }
                            .forEachIndexed { index, it ->
                                if (index > 0) {
                                    add(",")
                                }
                                if (it.typeHandler != null) {
                                    //有类型转换
                                    val targetType = it.targetType ?: Any::class.type
                                    add(
                                        "(\$T) \$T.handleResult($resultSetVar.getObject(\$S), \$T.class)",
                                        targetType,
                                        it.typeHandler,
                                        it.source,
                                        targetType,
                                    )
                                } else {
                                    //无类型转换器
                                    //类型为javabean时targetType必定不为null
                                    requireNotNull(it.targetType).let { type ->
                                        if (type.isSameType(Any::class)) {
                                            add("$resultSetVar.getObject(\$S)", it.source)
                                        } else {
                                            add(
                                                "$resultSetVar.getObject(\$S,\$T.class)",
                                                it.source,
                                                it.targetType
                                            )
                                        }
                                    }
                                }
                                add("\n")
                            }
                        unindent()
                        add(");\n")
                        //集合项为javabean时mapping必定不为空集合
                        methodHandler.mappings
                            .filter { it.constructorParameterIndex == -1 }
                            .forEach {
                                val setter = "set${it.target.replaceFirstChar { it.uppercaseChar() }}"
                                if (it.typeHandler != null) {
                                    //有类型转换
                                    val targetType = it.targetType ?: Any::class.type
                                    addStatement(
                                        "$itemVar.$setter((\$T) \$T.handleResult($resultSetVar.getObject(\$S), \$T.class))",
                                        targetType,
                                        it.typeHandler,
                                        it.source,
                                        targetType,
                                    )
                                } else {
                                    //无类型转换器
                                    //类型为javabean时targetType必定不为null
                                    requireNotNull(it.targetType).let { type ->
                                        if (type.isSameType(Any::class)) {
                                            addStatement("$itemVar.$setter($resultSetVar.getObject(\$S))", it.source)
                                        } else {
                                            addStatement(
                                                "$itemVar.$setter($resultSetVar.getObject(\$S,\$T.class))",
                                                it.source,
                                                it.targetType
                                            )
                                        }
                                    }
                                }
                            }
                    }
                } else {
                    //返回单列
                    if (methodHandler.mappings.isNotEmpty() && methodHandler.mappings[0].typeHandler != null) {
                        val targetType = methodHandler.mappings[0].targetType ?: Any::class.type
                        //有类型转换
                        addStatement(
                            "$itemVar = (\$T) \$T.handleResult($resultSetVar.getObject(1), \$T.class)",
                            targetType,
                            methodHandler.mappings[0].typeHandler,
                            targetType,
                        )
                    } else if (resultHelper.itemType.isSameType(Any::class)) {
                        //未指定单列类型或类型为Object
                        addStatement("$itemVar = $resultSetVar.getObject(1)", resultHelper.itemType)
                    } else {
                        //无类型转换器
                        addStatement("$itemVar = $resultSetVar.getObject(1, \$T.class)", resultHelper.itemType)
                    }
                }
            }
            addStatement("return $itemVar")
            unindent()
            add("}\n")
        }.build()
    }

}