package io.github.afezeria.freedao.classic.processor

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import io.github.afezeria.freedao.NoRowReturnedException
import io.github.afezeria.freedao.StatementType
import io.github.afezeria.freedao.TooManyResultException
import io.github.afezeria.freedao.classic.runtime.AutoFill
import io.github.afezeria.freedao.classic.runtime.SqlSignature
import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition
import io.github.afezeria.freedao.processor.core.method.RealParameter
import io.github.afezeria.freedao.processor.core.method.XmlTemplateMethod
import io.github.afezeria.freedao.processor.core.processor.*
import io.github.afezeria.freedao.processor.core.spi.BuildMethodService
import io.github.afezeria.freedao.processor.core.template.TemplateHandler
import io.github.afezeria.freedao.processor.core.template.TemplateHandler.Companion.toVarName
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import javax.lang.model.element.Modifier

/**
 *
 * @author afezeria
 */
class ClassicBuildMethodService : BuildMethodService {
    /**
     * 给同名方法添加计数，避免出现同名类变量
     */
    private val methodCounter: MutableMap<String, Int> = mutableMapOf()

    override fun build(methodHandler: AbstractMethodDefinition): CodeBlock {

        val counter = methodCounter.compute(
            methodHandler.qualifiedName
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
    private fun addMethodSignatureField(methodHandler: AbstractMethodDefinition, counter: Int): String {
        val signFieldName = "${methodHandler.qualifiedName}_${counter}_sign"
        val field = FieldSpec.builder(
            SqlSignature::class.typeLA(
                methodHandler.returnType,
                methodHandler.returnTypeOriginalItemType,
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
                    add("\$T.\$L,\n", StatementType::class.java, statementType)
                    add("\$L,\n", methodHandler is XmlTemplateMethod)
                    add("this.getClass(),\n")
                    add("\$S,\n", this.qualifiedName)
                    add("\$T.class,\n", methodHandler.returnType.erasure())
                    methodHandler.returnTypeContainerType?.apply {
                        add("\$T.class,\n", erasure())
                    } ?: add("null,\n")
                    add("\$T.class,\n", methodHandler.returnTypeItemType.erasure())

                    add("new Class[]{\n")
                    indent()
                    parameters.filter { it !is VirtualLazyParameterImpl }.let { params ->
                        params.forEachIndexed { index, it ->
                            add("\$T.class${if (index < (params.size - 1)) "," else ""}\n", it.type.erasure().className)
                        }
                    }
                    unindent()
                    add("},\n")
                    add("\$L,\n", addSqlBuilderFieldTemp(this))
                    add("\$L,\n", addExecutorFieldTemp(this))


                    when (methodHandler.statementType) {
                        StatementType.INSERT, StatementType.UPDATE, StatementType.DELETE -> {
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

    /**
     * 生成sql拼接lambda
     * @param methodHandler BaseMethod
     * @return CodeBlock
     */
    private fun addSqlBuilderFieldTemp(methodHandler: AbstractMethodDefinition): CodeBlock {
        return CodeBlock.builder().apply {
            add("_params -> {\n")
            indent()
            methodHandler.parameters.forEachIndexed { index, parameter ->
                var type = parameter.type
                if (type is PrimitiveType) {
                    type = type.boxed()
                }
                if (type.typeParameters.isNotEmpty()) {
                    add("@SuppressWarnings(\"unchecked\")\n")
                }
                addStatement(
                    "\$T ${parameter.simpleName.toVarName()} = (\$T) _params[\$L]",
                    type.className,
                    type.className,
                    index
                )
            }
            if (EnableAutoFill(methodHandler)) {
                AutoFillStruct(methodHandler)?.apply AutoFill@{
                    val props = autoFillProperties.mapNotNull {
                        val ann = it.getAnnotation(AutoFill::class)!!
                        if (ann.before
                            && ((ann.insert && methodHandler.statementType == StatementType.INSERT)
                                    || ann.update && methodHandler.statementType == StatementType.UPDATE)
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
                            "for (\$T $itemVar : ${methodHandler.parameters[index].simpleName.toVarName()})",
                            type.className,
                        )
                    } else {
                        addStatement(
                            "\$T $itemVar = (\$T) _params[\$L]",
                            methodHandler.parameters[index].type.className,
                            methodHandler.parameters[index].type.className,
                            index
                        )
                    }
                    props.forEach { (ann, prop) ->
                        addStatement(
                            "$itemVar.${prop.setterName}((\$T) \$T.gen($itemVar, \$S, \$T.class))",
                            prop.type.className,
                            ann.mirroredTypeLA { generator }.className,
                            prop.simpleName,
                            prop.type.className
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
            addStatement("\$T l_sql_0 = l_builder_0.toString()", String::class.typeLA.className)
            addStatement("return new Object[]{l_sql_0, ${TemplateHandler.sqlArgsVarName}}")
            unindent()
            add("}")
        }.build()
    }

    /**
     * 生成sql执行lambda
     * @param methodHandler BaseMethod
     * @return CodeBlock
     */
    private fun addExecutorFieldTemp(methodHandler: AbstractMethodDefinition): CodeBlock {
        return CodeBlock.builder().apply {
            add("($connVar, $methodArgsVar, $sqlVar, $argsVar, $resultHandlerVar) -> {\n")
            indent()

            if (EnableAutoFill(methodHandler)) {
                beginControlFlow(
                    "try (\$T $stmtVar = $connVar.prepareStatement($sqlVar, \$T.RETURN_GENERATED_KEYS))",
                    PreparedStatement::class.typeLA.className,
                    Statement::class.typeLA.className
                )
            } else {
                beginControlFlow(
                    "try (\$T $stmtVar = $connVar.prepareStatement($sqlVar))",
                    PreparedStatement::class.typeLA.className
                )
            }

            beginControlFlow("for (int $idxVar = 0; $idxVar < $argsVar.size(); $idxVar++)")
            addStatement("$stmtVar.setObject($idxVar + 1, $argsVar.get($idxVar))")
            endControlFlow()

            addStatement("$stmtVar.execute()")

            when (methodHandler.statementType) {
                StatementType.INSERT, StatementType.UPDATE, StatementType.DELETE -> {
                    handeUpdateMethodResultMapping(methodHandler)
                }

                else -> {
                    handeSelectMethodResultMapping(methodHandler)
                }
            }

            endControlFlow()
            unindent()
            add("}")
        }.build()
    }

    private fun CodeBlock.Builder.handeUpdateMethodResultMapping(methodHandler: AbstractMethodDefinition) {
        if (EnableAutoFill(methodHandler)) {
            AutoFillStruct(methodHandler)?.apply {
                addStatement("\$T $resultSetVar = $stmtVar.getGeneratedKeys()", ResultSet::class.typeLA.className)

                if (isCollection) {
                    addStatement(
                        "\$T $containerVar = (\$T) $methodArgsVar[${index}]",
                        collectionType!!.className,
                        collectionType.className
                    )
                    addStatement("\$T $itemVar = null", type.className)
                    addStatement("int $idxVar = 0")
                    beginControlFlow("while ($resultSetVar.next())")
                    addStatement("$itemVar = $containerVar.get($idxVar)")
                } else {
                    addStatement("\$T $itemVar = (\$T) $methodArgsVar[${index}]", type.className, type.className)
                    beginControlFlow("while ($resultSetVar.next())")
                }
                autoFillProperties
                    .forEach {
                        val ann = it.getAnnotation(AutoFill::class)!!
                        if (ann.before.not()
                            && ((ann.insert && methodHandler.statementType == StatementType.INSERT)
                                    || ann.update && methodHandler.statementType == StatementType.UPDATE)
                        ) {
                            val targetTypeClassName = it.type.className
                            //将generatedKeys的值填充回实体类
                            if (it.resultTypeHandle != null) {
                                //有类型转换
                                addStatement(
                                    "$itemVar.${it.setterName}((\$T) \$T.handleResult(\$T.gen($resultSetVar, \$S, Object.class), \$T.class))",
                                    targetTypeClassName,
                                    it.resultTypeHandle!!.className,
                                    ann.mirroredTypeLA { generator }.className,
                                    it.columnName,
                                    targetTypeClassName,
                                )
                            } else {
                                addStatement(
                                    "$itemVar.${it.setterName}((\$T) \$T.gen($resultSetVar, \$S, \$T.class))",
                                    targetTypeClassName,
                                    ann.mirroredTypeLA { generator }.className,
                                    it.columnName,
                                    targetTypeClassName,
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
        if (methodHandler.returnTypeItemType.isSameType(Int::class)) {
            addStatement("return $stmtVar.getUpdateCount()")
        } else {
            addStatement("return $stmtVar.getLargeUpdateCount()")
        }
    }

    private fun CodeBlock.Builder.handeSelectMethodResultMapping(methodHandler: AbstractMethodDefinition) {
        addStatement("\$T $resultSetVar = $stmtVar.getResultSet()", ResultSet::class.java)

        val returnMultipleRow = methodHandler.returnTypeContainerType != null
        if (returnMultipleRow) {
            //List<Map<String, Object>> list = new ArrayList<>();
            val diamondStr = if (methodHandler.returnTypeContainerType!!.typeParameters.isEmpty()) {
                ""
            } else {
                "<>"
            }
            addStatement(
                "\$T $containerVar = new \$T$diamondStr()",
                methodHandler.returnType.className,
                methodHandler.returnTypeContainerType!!.className,
            )
            addStatement(
                "\$T $itemVar = null",
                methodHandler.returnType.findTypeArgument(Collection::class.typeLA, "E").className,
            )
        } else {
            addStatement(
                "\$T $itemVar = null",
                methodHandler.returnType.boxed(),
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
        if (methodHandler.returnType is PrimitiveType) {
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

    private fun buildSelectResultHandler(methodHandler: AbstractMethodDefinition): CodeBlock {
        return CodeBlock.builder().apply {
            add("($resultSetVar,$itemVar) -> {\n")
            indent()
            methodHandler.apply {

                if (methodHandler.returnTypeItemType.isAssignable(Map::class) || methodHandler.returnTypeItemType.asBeanType() != null) {
                    //返回结构化数据
                    val diamondStr = if (methodHandler.returnTypeItemType.typeParameters.isEmpty()) {
                        ""
                    } else {
                        "<>"
                    }
                    if (returnTypeItemType.isAssignable(Map::class)) {
                        //集合内容为map
                        addStatement("$itemVar = new \$T$diamondStr()", methodHandler.returnTypeItemType.className)
                        if (methodHandler.mappings.isNotEmpty()) {
                            methodHandler.mappings.forEach {
                                when {
                                    //有类型转换器
                                    it.typeHandlerLA != null -> {
                                        addStatement(
                                            "$itemVar.put(\$S, (\$T) \$T.handleResult($resultSetVar.getObject(\$S), \$T.class))",
                                            it.source,
                                            (it.targetTypeLA ?: Any::class.typeLA).className,
                                            it.typeHandlerLA!!.className,
                                            it.source,
                                            (it.targetTypeLA ?: Any::class.typeLA).className,
                                        )
                                    }
                                    //map的值的类型参数不为Object时调用jdbc的显示转换类型函数
                                    !methodHandler.returnTypeMapValueType!!.isSameType(Any::class) -> {
                                        addStatement(
                                            "$itemVar.put(\$S, $resultSetVar.getObject(\$S, \$T.class))",
                                            it.source,
                                            it.source,
                                            methodHandler.returnTypeMapValueType!!.className,
                                        )
                                    }

                                    else -> {
                                        addStatement(
                                            "$itemVar.put(\$S, $resultSetVar.getObject(\$S))",
                                            it.source,
                                            it.source,
                                        )
                                    }
                                }
                            }
                        } else {
                            //未声明结果映射规则时将返回的所有结果都添加到map中
                            addStatement(
                                "\$T $metaDataVar = $resultSetVar.getMetaData()",
                                ResultSetMetaData::class.typeLA.className,
                            )
                            addStatement("int $columnCountVar = $metaDataVar.getColumnCount()")
                            beginControlFlow("for (int $idxVar = 1; $idxVar <= $columnCountVar; $idxVar++)")
                            addStatement("String $labelVar = $metaDataVar.getColumnLabel($idxVar)")
                            if (methodHandler.returnTypeMapValueType!!.isSameType(Any::class)) {
                                addStatement("$itemVar.put($labelVar, $resultSetVar.getObject($labelVar))")
                            } else {
                                addStatement(
                                    "$itemVar.put($labelVar, $resultSetVar.getObject($labelVar, \$T.class))",
                                    methodHandler.returnTypeMapValueType!!.className
                                )
                            }
                            endControlFlow()
                        }
                    } else {
                        add("$itemVar = new \$T$diamondStr(\n", methodHandler.returnTypeItemType.className)
                        indent()
                        methodHandler.mappings.filter { it.constructorParameterIndex > -1 }
                            .sortedBy { it.constructorParameterIndex }
                            .forEachIndexed { index, it ->
                                if (index > 0) {
                                    add(",")
                                }
                                if (it.typeHandlerLA != null) {
                                    //有类型转换
                                    add(
                                        "(\$T) \$T.handleResult($resultSetVar.getObject(\$S), \$T.class)",
                                        (it.targetTypeLA ?: Any::class.typeLA).className,
                                        it.typeHandlerLA!!.className,
                                        it.source,
                                        (it.targetTypeLA ?: Any::class.typeLA).className,
                                    )
                                } else {
                                    //无类型转换器
                                    //类型为javabean时targetType必定不为null
                                    it.targetTypeLA!!.let { type ->
                                        if (type.isSameType(Any::class)) {
                                            add("$resultSetVar.getObject(\$S)", it.source)
                                        } else {
                                            add(
                                                "$resultSetVar.getObject(\$S,\$T.class)",
                                                it.source,
                                                type.className
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
                                if (it.typeHandlerLA != null) {
                                    //有类型转换
                                    addStatement(
                                        "$itemVar.$setter((\$T) \$T.handleResult($resultSetVar.getObject(\$S), \$T.class))",
                                        (it.targetTypeLA ?: Any::class.typeLA).className,
                                        it.typeHandlerLA!!.className,
                                        it.source,
                                        (it.targetTypeLA ?: Any::class.typeLA).className,
                                    )
                                } else {
                                    //无类型转换器
                                    //类型为javabean时targetType必定不为null
                                    it.targetTypeLA!!.let { type ->
                                        if (type.isSameType(Any::class)) {
                                            addStatement("$itemVar.$setter($resultSetVar.getObject(\$S))", it.source)
                                        } else {
                                            addStatement(
                                                "$itemVar.$setter($resultSetVar.getObject(\$S,\$T.class))",
                                                it.source,
                                                type.className
                                            )
                                        }
                                    }
                                }
                            }
                    }
                } else {
                    //返回单列
                    if (methodHandler.mappings.isNotEmpty() && methodHandler.mappings[0].typeHandlerLA != null) {
                        val targetTypeClassName =
                            (methodHandler.mappings[0].targetTypeLA ?: Any::class.typeLA).className
                        //有类型转换
                        addStatement(
                            "$itemVar = (\$T) \$T.handleResult($resultSetVar.getObject(1), \$T.class)",
                            targetTypeClassName,
                            methodHandler.mappings[0].typeHandlerLA!!.className,
                            targetTypeClassName,
                        )
                    } else if (methodHandler.returnTypeItemType.isSameType(Any::class)) {
                        //未指定单列类型或类型为Object
                        addStatement("$itemVar = $resultSetVar.getObject(1)", methodHandler.returnTypeItemType)
                    } else {
                        //无类型转换器
                        addStatement(
                            "$itemVar = $resultSetVar.getObject(1, \$T.class)",
                            methodHandler.returnTypeItemType
                        )
                    }
                }
            }
            addStatement("return $itemVar")
            unindent()
            add("}\n")
        }.build()
    }

}