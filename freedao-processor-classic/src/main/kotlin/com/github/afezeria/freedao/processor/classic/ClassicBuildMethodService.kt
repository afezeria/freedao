package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.ResultTypeHandler
import com.github.afezeria.freedao.StatementType
import com.github.afezeria.freedao.processor.core.*
import com.github.afezeria.freedao.processor.core.method.MethodModel
import com.github.afezeria.freedao.processor.core.method.ResultHelper
import com.github.afezeria.freedao.processor.core.spi.BuildMethodService
import com.github.afezeria.freedao.processor.core.template.TemplateHandler
import com.github.afezeria.freedao.processor.core.template.TemplateHandler.Companion.toVarName
import com.github.afezeria.freedao.runtime.classic.LogHelper
import com.github.afezeria.freedao.runtime.classic.SqlExecutor
import com.github.afezeria.freedao.runtime.classic.SqlSignature
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
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

    override fun build(methodModel: MethodModel): CodeBlock {
        val counter =
            methodCounter.compute("${methodModel.daoModel.element.qualifiedName}.${methodModel.name}") { _, v ->
                v?.inc() ?: 0
            }!!
        val signVar = addMethodSignatureField(methodModel, counter)
        val sqlVar = addSqlBuilderField(methodModel, counter)
        val executorVar = addExecutorField(methodModel, counter)
        return CodeBlock.builder().apply {
            addStatement("Object[] $methodArgsVar = {${methodModel.parameters.joinToString { it.name }}}")
            addStatement("Object[] __sqlAndArgs__ = $contextVar.buildSql($signVar, $methodArgsVar, $sqlVar)")

            addStatement("return $contextVar.execute($signVar, $methodArgsVar, (\$T) $buildSqlResultVar[0], (\$T) $buildSqlResultVar[1], $executorVar)",
                String::class.type,
                List::class.type(Any::class.type))

        }.build()
    }


    /**
     * 给dao添加表示sql签名的静态属性
     * @param methodModel MethodHandler
     */
    private fun addMethodSignatureField(methodModel: MethodModel, counter: Int): String {
        val name = "${methodModel.name}_${counter}_sign"
        val field = FieldSpec.builder(
            SqlSignature::class.type.typeName,
            name,
            Modifier.PRIVATE,
            Modifier.FINAL,
        ).initializer(CodeBlock.builder()
            .add("new \$T(\$L,${methodModel.daoModel.implQualifiedName}.class,\$S,\$T.class${
                methodModel.parameters.joinToString { "${it.model.typeMirror.erasure().typeName}.class" }
                    .takeIf { it.isNotBlank() }
                    ?.let { ", $it" }
                    ?: ""
            })",
                SqlSignature::class.type,
                "${StatementType::class.qualifiedName}.${methodModel.statementType}",
                methodModel.name,
                methodModel.resultHelper.returnType.erasure()).build()).build()
        methodModel.daoModel.classBuilder.addField(field)
        return name
    }

    private fun addSqlBuilderField(methodModel: MethodModel, counter: Int): String {
        //Function<Object[], Object[]> insert_0_sqlBuilder
        val field = FieldSpec.builder(
            java.util.function.Function::class.type(
                typeUtils.getArrayType(Any::class.type),
                typeUtils.getArrayType(Any::class.type),
            ).typeName,
            "${methodModel.name}_${counter}_sql",
            Modifier.PRIVATE,
            Modifier.FINAL,
        ).initializer(
            CodeBlock.builder().apply {
                add("_params -> {\n")
                indent()
                methodModel.parameters.forEachIndexed { index, parameter ->
                    var type = parameter.model.typeMirror
                    if (type is PrimitiveType) {
                        type = typeUtils.boxedClass(type).asType()
                    }
                    addStatement("\$T ${parameter.name.toVarName()} = (\$T) _params[\$L]", type, type, index)
                }
                add("\n")
                add(methodModel.sqlBuildCodeBlock)
                add("\n")
                addStatement("\$T l_sql_0 = l_builder_0.toString()", String::class.type)
                addStatement("return new Object[]{l_sql_0, ${TemplateHandler.sqlArgsVarName}}")
                unindent()
                add("}")
            }.build()
        ).build()
        methodModel.daoModel.classBuilder.addField(field)

        return field.name
    }

    private fun addExecutorField(methodModel: MethodModel, counter: Int): String? {
        val resultHelper = methodModel.resultHelper
        val field = FieldSpec.builder(
            SqlExecutor::class.type(resultHelper.returnType).typeName,
            "${methodModel.name}_${counter}_executor",
            Modifier.PRIVATE,
            Modifier.FINAL,
        ).initializer(
            CodeBlock.builder().apply {
                add("($connVar, $methodArgsVar, $sqlVar, $argsVar) -> {\n",
                    String::class.type,
                    List::class.type(Any::class.type))
                indent()
                beginControlFlow("if ($logVar.isDebugEnabled())")
                addStatement("\$T.logSql($logVar, $sqlVar)", LogHelper::class.type)
                addStatement("\$T.logArgs($logVar, $argsVar)", LogHelper::class.type)
                endControlFlow()


                if (EnableAutoFill(methodModel)) {
                    beginControlFlow("try (\$T $stmtVar = $connVar.prepareStatement($sqlVar, \$T.RETURN_GENERATED_KEYS))",
                        PreparedStatement::class.type,
                        Statement::class.type)
                } else {
                    beginControlFlow("try (\$T $stmtVar = $connVar.prepareStatement($sqlVar))",
                        PreparedStatement::class.type)
                }

                beginControlFlow("for (int $idxVar = 0; $idxVar < $argsVar.size(); $idxVar++)")
                addStatement("$stmtVar.setObject($idxVar + 1, $argsVar.get($idxVar))")
                endControlFlow()

                addStatement("$stmtVar.execute()")

                when(methodModel.statementType) {
                    StatementType.INSERT,StatementType.UPDATE,StatementType.DELETE -> {
                        handeUpdateMethodResultMapping(methodModel, resultHelper)
                    }
                    else -> {
                        handeSelectMethodResultMapping(methodModel, resultHelper)
                    }
                }

                nextControlFlow("catch (\$T e)", Exception::class.type)
                addStatement("throw new \$T(e)", RuntimeException::class.type)
                endControlFlow()
                unindent()
                add("}")
            }.build()
        ).build()
        methodModel.daoModel.classBuilder.addField(field)

        return field.name
    }

    private fun CodeBlock.Builder.handeUpdateMethodResultMapping(methodModel: MethodModel, resultHelper: ResultHelper) {
        if (EnableAutoFill(methodModel)) {
            AutoFillStruct(methodModel)?.apply {
                addStatement("\$T $resultSetVar = $stmtVar.getGeneratedKeys()", ResultSet::class.type)

                if (isCollection) {
                    addStatement("\$T $containerVar = (\$T) $methodArgsVar[${index}]", collectionType, collectionType)
                    addStatement("\$T $itemVar = null", type)
                    addStatement("int $idxVar = 0")
                    beginControlFlow("while ($resultSetVar.next())")
                    addStatement("$itemVar = $containerVar.get(i)")
                } else {
                    addStatement("\$T $itemVar = (\$T) $methodArgsVar[${index}]", type, type)
                    beginControlFlow("while ($resultSetVar.next())")
                }

                dbAutoFillProperties.forEach {
                    if (!it.column.resultTypeHandle.isSameType(ResultTypeHandler::class)) {
                        //有类型转换
                        addStatement("$itemVar.${it.setterName}(\$T.handle($resultSetVar.getObject(\$S)))",
                            it.column.resultTypeHandle,
                            it.column.name)
                    } else {
                        //无类型转换器
                        if (it.type.isSameType(Any::class)) {
                            addStatement("$itemVar.${it.setterName}($resultSetVar.getObject(\$S))", it.column.name)
                        } else {
                            addStatement("$itemVar.${it.setterName}($resultSetVar.getObject(\$S,\$T.class))",
                                it.column.name,
                                it.type)
                        }
                    }
                }
                if (isCollection) {
                    addStatement("$containerVar.add($itemVar)")
                    addStatement("i++")
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

    private fun CodeBlock.Builder.handeSelectMethodResultMapping(methodModel: MethodModel, resultHelper: ResultHelper) {

        addStatement("\$T $resultSetVar = $stmtVar.getResultSet()", ResultSet::class.type)
        val returnMultipleRow = resultHelper.containerType != null
        if (returnMultipleRow) {
            //List<Map<String, Object>> list = new ArrayList<>();
            val diamondStr = if ((resultHelper.containerType!!.asElement() as TypeElement).typeParameters.isEmpty()) {
                ""
            } else {
                "<>"
            }
            addStatement("\$T $containerVar = new \$T$diamondStr()",
                resultHelper.returnType,
                resultHelper.containerType)
            addStatement(
                "\$T $itemVar = null",
                (resultHelper.returnType as DeclaredType).findTypeArgument(Collection::class.type, "E"),
            )
        } else {
            addStatement(
                "\$T $itemVar = null",
                resultHelper.returnType,
            )
        }
        if (methodModel.resultHelper.isStructuredItem) {
            //返回结构化数据
            val diamondStr = if ((resultHelper.itemType.asElement() as TypeElement).typeParameters.isEmpty()) {
                ""
            } else {
                "<>"
            }
            if (resultHelper.itemType.isAssignable(Map::class)) {
                val addInitItemStatement = {
                    addStatement("$itemVar = new \$T$diamondStr()", resultHelper.itemType)
                }
                //集合内容为map
                if (resultHelper.mappings.isNotEmpty()) {
                    beginControlFlow("while ($resultSetVar.next())")
                    addInitItemStatement()
                    resultHelper.mappings.forEach {
                        when {
                            //有类型转换器
                            !it.typeHandler.isSameType(ResultTypeHandler::class) -> {
                                addStatement("$itemVar.put(\$S, \$T.handle(rs.getObject(\$S)))",
                                    it.source,
                                    it.typeHandler,
                                    it.source)
                            }
                            //map的值的类型参数不为Object时调用jdbc的显示转换类型函数
                            !resultHelper.mapValueType!!.isSameType(Any::class) -> {
                                addStatement("$itemVar.put(\$S, $resultSetVar.getObject(\$S, \$T.class))",
                                    it.source,
                                    it.source,
                                    resultHelper.mapValueType)
                            }
                            else -> {
                                addStatement("$itemVar.put(\$S, $resultSetVar.getObject(\$S))", it.source, it.source)
                            }
                        }
                    }
                    if (returnMultipleRow) {
                        addStatement("$containerVar.add(item)")
                    } else {
                        addStatement("break")
                    }
                    endControlFlow()
                } else {
                    addStatement("\$T $metaDataVar = $resultSetVar.getMetaData()", ResultSetMetaData::class.type)
                    addStatement("int $columnCountVar = $metaDataVar.getColumnCount()")
                    beginControlFlow("while ($resultSetVar.next())")
                    addInitItemStatement()
                    beginControlFlow("for (int $idxVar = 1; $idxVar <= $columnCountVar; $idxVar++)")
                    addStatement("String $labelVar = $metaDataVar.getColumnLabel($idxVar)")
                    if (resultHelper.mapValueType!!.isSameType(Any::class)) {
                        addStatement("$itemVar.put($labelVar, $resultSetVar.getObject($labelVar))")
                    } else {
                        addStatement("$itemVar.put($labelVar, $resultSetVar.getObject($labelVar, \$T.class))",
                            resultHelper.mapValueType)
                    }
                    endControlFlow()
                    if (returnMultipleRow) {
                        addStatement("$containerVar.add($itemVar)")
                    } else {
                        addStatement("break")
                    }
                    endControlFlow()
                }
            } else {
                beginControlFlow("while ($resultSetVar.next())")
                add("$itemVar = new \$T$diamondStr(\n", resultHelper.itemType)
                indent()
                resultHelper.mappings.filter { it.constructorParameterIndex > -1 }
                    .sortedBy { it.constructorParameterIndex }
                    .forEachIndexed { index, it ->
                        if (index > 0) {
                            add(",")
                        }
                        if (!it.typeHandler.isSameType(ResultTypeHandler::class)) {
                            //有类型转换
                            add("\$T.handle($resultSetVar.getObject(\$S))",
                                it.typeHandler,
                                it.source)
                        } else {
                            //无类型转换器
                            //类型为javabean时targetType必定不为null
                            requireNotNull(it.targetType).let { type ->
                                if (type.isSameType(Any::class)) {
                                    add("$resultSetVar.getObject(\$S)", it.source)
                                } else {
                                    add("$resultSetVar.getObject(\$S,\$T.class)",
                                        it.source,
                                        it.targetType)
                                }
                            }
                        }
                        add("\n")
                    }
                unindent()
                add(");\n")
                //集合项为javabean时mapping必定不为空集合
                resultHelper.mappings
                    .filter { it.constructorParameterIndex == -1 }
                    .forEach {
                        val setter = "set${it.target.replaceFirstChar { it.uppercaseChar() }}"
                        if (!it.typeHandler.isSameType(ResultTypeHandler::class)) {
                            //有类型转换
                            addStatement("$itemVar.$setter(\$T.handle($resultSetVar.getObject(\$S)))",
                                it.typeHandler,
                                it.source)
                        } else {
                            //无类型转换器
                            //类型为javabean时targetType必定不为null
                            requireNotNull(it.targetType).let { type ->
                                if (type.isSameType(Any::class)) {
                                    addStatement("$itemVar.$setter($resultSetVar.getObject(\$S))", it.source)
                                } else {
                                    addStatement("$itemVar.$setter($resultSetVar.getObject(\$S,\$T.class))",
                                        it.source,
                                        it.targetType)
                                }
                            }
                        }
                    }
                if (returnMultipleRow) {
                    addStatement("$containerVar.add($itemVar)")
                } else {
                    addStatement("break")
                }

                endControlFlow()
            }
        } else {
            beginControlFlow("while ($resultSetVar.next())")
            //返回单列
            if (resultHelper.mappings.isNotEmpty() && !resultHelper.mappings[0].typeHandler.isSameType(ResultTypeHandler::class)) {
                //有类型转换
                addStatement("$itemVar = \$T.handle($resultSetVar.getObject(1))", resultHelper.mappings[0].typeHandler)
            } else {
                //无类型转换器
                addStatement("$itemVar = $resultSetVar.getObject(1, \$T.class)", resultHelper.itemType)
            }
            if (returnMultipleRow) {
                addStatement("$containerVar.add($itemVar)")
            } else {
                addStatement("break")
            }

            endControlFlow()
        }

        if (returnMultipleRow) {
            addStatement("return $containerVar")
        } else {
            addStatement("return $itemVar")
        }
    }

}