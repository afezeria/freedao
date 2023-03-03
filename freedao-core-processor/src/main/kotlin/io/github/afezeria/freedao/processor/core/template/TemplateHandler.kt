package io.github.afezeria.freedao.processor.core.template

import com.squareup.javapoet.CodeBlock
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.PlaceholderGen
import io.github.afezeria.freedao.processor.core.processor.*
import java.util.*

/**
 *
 */


class TemplateHandler(map: Map<String, LazyType>) {
    private val codeBlock: CodeBlock.Builder = CodeBlock.builder()
    val placeholderGen = PlaceholderGen()

    private var builderCounter = 0
    private var builderPrefix = "l_builder_"
    private val builderName: String
        get() = "$builderPrefix$builderCounter"

    private var tmpVarCounter = 0
    private var tmpVarPrefix = "l_tmpVar_"
    private fun newTmpVar(): String = "$tmpVarPrefix${tmpVarCounter++}"

    /**
     * key为变量名，value为类型
     */
    private val parameterStack: Deque<MutableMap<String, LazyType>> = LinkedList()


    init {
        parameterStack.addFirst(map.map { (k, v) ->
            k.toVarName() to v
        }.toMap(mutableMapOf()))
        codeBlock.addStatement(
            "\$T<\$T> $sqlArgsVarName = new \$T<>()",
            List::class.java,
            Object::class.java,
            ArrayList::class.java
        )
        codeBlock.addStatement("\$T $builderName = new \$T()", StringBuilder::class.java, StringBuilder::class.java)
    }

    fun handle(): CodeBlock {
        return codeBlock.build()
    }

    fun previousBuilderName(): String {
        val counter = builderCounter - 1
        return if (counter < 0) {
            throw IllegalStateException()
        } else {
            "$builderPrefix$counter"
        }
    }

    /**
     * 根据属性名查找变量，返回变量名和类型
     * @param name String
     * @return Pair<String,TypeMirror>
     */
    private fun getContextVar(name: String): Pair<String, LazyType>? {
        for (map in parameterStack) {
            map[name.toVarName()]?.apply {
                return name.toVarName() to this
            }
        }
        return null
    }


    /**
     * 根据表达式生成调用链
     *
     * [expectType]为null时直接返回path的类型，不为null时检查path的结果的类型，如果类型为Object或可以转换为[expectType]
     * 则进行强转，否则抛出异常提示类型不匹配
     *
     * 返回结果中first的格式：
     *
     * [expectType]为null时：a.getB()
     *
     * [expectType]为a.c.Other时：((a.c.Other) a.getB()
     * @param path String 调用链表达式
     * @param expectType TypeMirror? 期望的类型
     * @return Pair<String, TypeMirror> first为path转换后的java代码文本，second为first的结果的类型，
     * [expectType]不为空时second等于[expectType]
     */
    fun createInvokeChain(path: String, expectType: LazyType? = null): Pair<String, LazyType> {
        val arr = path.split(".").toMutableList()
        val first = arr.removeFirst()
        var (text, type) = getContextVar(first) ?: throw RuntimeException("property '$first' not found")
        if (type is PrimitiveType) {
            type = type.boxed()
            text = "(${type.qualifiedName}) $text"
        }
        var originalText = first

        for (s in arr) {
            when {
                s.isMapKey() -> {
                    when {
                        type.isSameType(Any::class) -> {
                            text = "((java.util.Map) $text).get($s)"
                            type = Any::class.typeLA
                        }

                        type.isAssignable(Map::class) -> {
                            text = "$text.get($s)"
                            type = type.findTypeArgument(Map::class.typeLA, "V")
                        }

                        else -> {
                            throw HandlerException("$originalText is not a map")
                        }
                    }
                }

                s.isListIndex() -> {
                    when {
                        type.isSameType(Any::class) -> {
                            text = "((java.util.List)$text).get($s)"
                            type = Any::class.typeLA
                        }

                        type.isAssignable(List::class) -> {
                            text = "$text.get($s)"
                            type = type.typeParameters.takeIf { it.isNotEmpty() }?.get(0) ?: Any::class.typeLA
                        }

                        else -> {
                            throw HandlerException("$originalText is not a list")
                        }
                    }
                }

                else -> {
                    if (type.isSameType(Any::class)) {
                        //如果不知道字段类型就反射处理
                        text = "${io.github.afezeria.freedao.ReflectHelper::class.qualifiedName}.call($text,\"$s\")"
                        type = Any::class.typeLA
                    } else if (type.isAssignable(Collection::class) || type.isAssignable(Map::class) && s == "size") {
                        //唯一允许的方法调用是map/list的size方法
                        type = Int::class.typeLA
                        text = "(${type}) $text.size()"
                    } else {
                        //找到字段类型
                        type = type.allFields.find { it.simpleName == s }
                            ?.type
                            ?: throw HandlerException("error expr:$originalText.$s, missing property:${type}.$s")
                        text = "$text.get${s.replaceFirstChar { it.uppercaseChar() }}()"
                    }
                }
            }
            if (type is PrimitiveType) {
                type = type.boxed()
                text = "($type) $text"
            }
            originalText += ".$s"
        }

        return if (expectType != null) {
            when {
                type.isSameType(Any::class) -> "((${expectType.qualifiedName}) $text)" to expectType
                type.isAssignable(expectType) -> text to type
                else -> throw HandlerException("$originalText is of type $type cannot assignable to $expectType")
            }
        } else {
            text to type
        }
    }

    /**
     * 根据path创建java变量
     * @param path String
     * @param expectType TypeMirror?
     * @return ScopeVariable
     */
    fun createInternalVariableByContextValue(path: String, expectType: LazyType? = null): Pair<String, LazyType> {
        val (text, type) = createInvokeChain(path, expectType)
        val tmpVar = newTmpVar()
        currentScope {
            addStatement("\$T $tmpVar = $text", type.className)
        }
        return tmpVar to type
    }

    /**
     * 创建xml中可访问的属性的变量
     * @param name String 变量名
     * @param type TypeMirror 变量类型
     * @param defaultValue Any? 默认值
     * @return ScopeVariable
     */
    fun createTemplateVariable(name: String, type: LazyType, defaultValue: Any?): String {
        if (getContextVar(name) != null) {
            throw HandlerException("Property '$name' already exists")
        }
        val varName = name.toVarName()
        parameterStack.first[varName] = type
        currentScope {
            addStatement("\$T $varName = \$L", type.className, defaultValue)
        }
        return varName
    }

    fun createInternalFlag(type: LazyType, initValue: Any?): String {
        val varName = newTmpVar()
        codeBlock.addStatement("\$T $varName = \$L", type.className, initValue)
        return varName
    }

    fun newScope(fn: CodeBlock.Builder.(String) -> Unit) {
        builderCounter++
        parameterStack.addFirst(mutableMapOf())
        codeBlock.apply {
            add("{\n")
            indent()
            addStatement("\$T $builderName = new \$T()", StringBuilder::class.java, StringBuilder::class.java)
            fn(this, builderName)
            addStatement("${previousBuilderName()}.append($builderName)")
            unindent()
            add("}\n")
        }
        parameterStack.removeFirst()
        builderCounter--
    }

    fun currentScope(fn: CodeBlock.Builder.(String) -> Unit) {
        fn(codeBlock, builderName)
    }

    fun handleTestExprAndReturnFlagName(test: String): String {
        return TestExprHandler(this, test).handle()
    }

    companion object {
        const val sqlArgsVarName = "l_sqlArgs_0"

        private val indexRegex = "\\d+".toRegex()

        private fun String.isMapKey(): Boolean = startsWith("\"")
        private fun String.isListIndex(): Boolean = matches(indexRegex)

        /**
         * 生成模板中可用的属性名对应的变量名
         * @receiver String
         * @return String
         */
        fun String.toVarName() = "p_$this"
    }
}

