package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.Long2IntegerResultHandler
import com.github.afezeria.freedao.ResultTypeHandler
import com.github.afezeria.freedao.processor.core.*
import java.util.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import kotlin.reflect.full.primaryConstructor

abstract class NamedMethod private constructor(
    element: ExecutableElement, daoHandler: DaoHandler,
) : MethodHandler(element, daoHandler) {

    var crudEntity: EntityObjectModel
    var dbProperties: List<BeanProperty>
    var propertyMap: Map<String, BeanProperty>

    /**
     * 当关键字为And/Or时pair.first为null
     */
    var conditions: MutableList<Condition> = mutableListOf()
    var orderColumns: MutableList<Pair<BeanProperty, OrderEnum>> = mutableListOf()

    /**
     * 参数列表中第一个和表达式参数匹配的参数的索引
     */
    var parameterFirstMatchIndex = 0

    /**
     * 参数列表中最后一个和表达式参数匹配的参数的索引
     */
    var parameterLastMatchIndex = 0

    init {
        crudEntity = daoHandler.crudEntity
            ?: throw HandlerException("Method $name requires Dao.crudEntity to be specified")
        dbProperties = crudEntity.properties.filter { it.column.exist }
        propertyMap = crudEntity.properties.filter { it.column.exist }.associateBy { it.name }
        parseName()
        checkParameters()
    }

    /**
     * 解析方法名，分离出查询条件和排序使用的属性
     */
    fun parseName() {
        val (_, _, condAndOrder) = methodNameSplitRegex.matchEntire(name)!!.groupValues
        val list = groupingRegex.findAll(condAndOrder).mapTo(LinkedList()) { it.groupValues[0] }
        var propertyName = ""

        var hasOrderByClause = false

        while (list.isNotEmpty()) {
            propertyName += list.pop()
            val property = propertyMap[propertyName.replaceFirstChar { it.lowercase() }]
            if (property != null) {
                val conditionKey = findConditionKey(list)
                val connectKey = findConnectKey(list)
                when (connectKey) {
                    "OrderBy" -> {
                        conditions += Condition(conditionKey ?: "Is").also { it.property = property }
                        propertyName = ""
                        hasOrderByClause = true
                        break
                    }
                    "And", "Or" -> {
                        conditions += Condition(conditionKey ?: "Is").also { it.property = property }
                        propertyName = ""
                        conditions += Condition(connectKey)
                    }
                    "" -> {
                        if (list.isEmpty()) {
                            conditions += Condition(conditionKey ?: "Is").also { it.property = property }
                            propertyName = ""
                            break
                        }
                    }
                }
            }
        }
        if (propertyName.isNotEmpty()) {
            throw HandlerException("missing condition property ${crudEntity.className}.${propertyName.replaceFirstChar { it.lowercase() }}")
        }

        if (hasOrderByClause) {
            while (list.isNotEmpty()) {
                propertyName += list.pop()
                val property = propertyMap[propertyName.replaceFirstChar { it.lowercase() }]
                if (property != null) {
                    val next = list.peek()
                    if (next == "Asc" || next == "Desc") {
                        orderColumns += property to OrderEnum.valueOf(next)
                        propertyName = ""
                        list.pop()
                    }
                }
            }
        }
        if (propertyName.isNotEmpty()) {
            throw HandlerException("missing order property ${crudEntity.className}.${propertyName.replaceFirstChar { it.lowercase() }}")
        }
    }

    /**
     * 检查参数类型与数量
     * 匹配规则：根据方法名对应的表达式从参数列表中第一个匹配项开始向后检查
     *
     * 例：
     *
     * queryByNameAndAge 根据名称解析需要的参数依次为 {name:String,age:int}
     *
     * 先从参数列表中找name参数的索引，然后从该索引开始向后按顺序依次检查参数列表和需要的参数是否匹配
     *
     * queryByNameAndAge(String name,Integer age) 正确
     *
     * queryByNameAndAge(Context ctx,String name,Integer age) 正确
     *
     * queryByNameAndAge(String name,Integer age,Context ctx) 正确
     *
     * queryByNameAndAge(Long timestamp,String name,Integer age,Context ctx) 正确
     *
     * queryByNameAndAge(String name,Context ctx,Integer age) 错误
     *
     * queryByNameAndAge(String name) 错误
     *
     */
    private fun checkParameters() {
        var parameterIdx = 0
        val parameters = element.parameters.mapTo(LinkedList()) { it }
        val requireParameterConditions = conditions.filter { cond ->
            //过滤And/Or(当关键字为and/or时pair的first为null)和不需要参数的条件
            cond.property != null && cond.requiredParameterTypes.isNotEmpty()
        }
        if (requireParameterConditions.isEmpty()) {
            return
        }
        //找到方法参数列表中和表达式需要的第一个参数匹配的参数的缩影，并将这之前的参数从parameters中移除
        requireParameterConditions.first().let { cond ->
            val type = cond.requiredParameterTypes.first()
            while (parameters.isNotEmpty()) {
                val param = parameters.first
                if (param.asType().isAssignable(type)) {
                    break
                } else {
                    parameters.pop()
                    parameterIdx++
                }
            }
        }
        parameterFirstMatchIndex = parameterIdx
        requireParameterConditions.forEach { cond ->
            cond.requiredParameterTypes.forEach {
                if (parameters.isEmpty()) {
                    throw HandlerException("Missing ${it.typeName} parameter")
                }
                val parameter = parameters.pop()
                parameterLastMatchIndex = parameterIdx
                parameterIdx++
                if (!parameter.asType().isAssignable(it)) {
                    throw HandlerException("Parameter mismatch, the ${parameterIdx}th parameter type should be $it")
                }
                cond.params += parameter
            }
        }
    }

    protected fun buildSelectList(): String {
        return crudEntity.properties.filter { it.column.exist }.joinToString { it.toSelectItem() }
    }

    protected fun buildWhereClause(): String {
        return conditions.joinToString(separator = " ", prefix = "where ") { cond ->
            cond.render()
        }
    }

    protected fun buildOrderClause(): String {
        return orderColumns.joinToString { (prop, enum) ->
            "${prop.column.name.sqlQuote()} ${enum.name.lowercase()}"
        }.takeIf { it.isNotBlank() }?.let { "order by $it" } ?: ""
    }


    companion object {
        private val methodNameSplitRegex = "^(.*?By)(.*)$".toRegex()

        private val length2ConditionKeys = listOf(
            3 to listOf("LessThanEqual", "GreaterThanEqual"),
            2 to listOf("IsNull", "LessThan", "GreaterThan", "NotNull", "NotIn", "NotLike"),
            1 to listOf("Between", "Like", "Not", "In", "True", "False"),
        )
        private val length2ConnectKeys = listOf(2 to listOf("OrderBy"), 1 to listOf("And", "Or"))

        fun findConditionKey(list: LinkedList<String>): String? {
            var s = ""
            for ((length, keys) in length2ConditionKeys) {
                if (list.size >= length) {
                    if (list.subList(0, length).joinToString("") in keys) {
                        repeat(length) { s += list.pop() }
                        return s
                    }
                }
            }
            return null
        }

        fun findConnectKey(list: LinkedList<String>): String {
            var s = ""
            for ((length, keys) in length2ConnectKeys) {
                if (list.size > length) {
                    if (list.subList(0, length).joinToString("") in keys) {
                        repeat(length) { s += list.pop() }
                        break
                    }
                }
            }
            return s
        }

        /**
         * 支持的where子句条件关键字
         * @property property BeanProperty? 匹配的bean属性
         * @property params List<VariableElement> 匹配的方法参数
         * @property requiredParameterTypesFn Function0<List<TypeMirror>> 返回需要的参数的类型列表
         * @property renderFn Function0<String> 返回sql的条件字符串
         * @constructor
         */
        sealed class Condition(
            private val requiredParameterTypesFn: Condition.() -> List<TypeMirror> = { listOf(property!!.type) },
            private val renderFn: Condition.() -> String,
        ) {
            var property: BeanProperty? = null
            val params = mutableListOf<VariableElement>()
            val column: String
                get() = property!!.column.name.sqlQuote()

            val requiredParameterTypes by lazy { requiredParameterTypesFn(this) }
            fun render() = renderFn(this)


            companion object {
                private val name2Constructor =
                    Condition::class.sealedSubclasses.associate { it.simpleName to it.primaryConstructor }

                operator fun invoke(name: String): Condition {
                    return name2Constructor[name]?.call() ?: throw IllegalStateException()
                }
            }

            class LessThanEqual : Condition(renderFn = { "$column &lt;= #{${params[0].simpleName}}" })
            class GreaterThanEqual : Condition(renderFn = { "$column &gt;= #{${params[0].simpleName}}" })
            class NotNull :
                Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "$column is not null" })

            class IsNull : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "$column is null" })

            class LessThan : Condition(renderFn = { "$column &lt; #{${params[0].simpleName}}" })

            class GreaterThan : Condition(renderFn = { "$column &gt; #{${params[0].simpleName}}" })

            class NotIn : Condition(
                requiredParameterTypesFn = { listOf(Collection::class.type(property!!.type)) },
                renderFn = {
                    "$column not in (<foreach collection='${params[0].simpleName}' item='item' separator=','>#{item}</foreach>)"
                }
            )

            class NotLike : Condition(renderFn = { "$column not like #{${params[0].simpleName}}" })

            class Between : Condition(
                requiredParameterTypesFn = { listOf(property!!.type, property!!.type) },
                renderFn = { "$column between #{${params[0].simpleName}} and #{${params[1].simpleName}}" },
            )

            class Like : Condition(renderFn = { "$column like #{${params[0].simpleName}}" })

            class Not : Condition(renderFn = { "$column &lt;&gt; #{${params[0].simpleName}}" })

            class In : Condition(
                requiredParameterTypesFn = { listOf(Collection::class.type(property!!.type)) },
                renderFn = {
                    "$column in (<foreach collection='${params[0].simpleName}' item='item' separator=','>#{item}</foreach>)"
                }
            )

            class True : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "$column = true" })

            class False : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "$column = false" })

            /**
             * Is不能作为关键字出现在方法名中
             * 当字段名后没有接其他条件关键字或Order时当作Is处理
             */
            class Is : Condition(renderFn = { "$column = #{${params[0].simpleName}}" })

            class And : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "and" })

            class Or : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "or" })
        }

        enum class OrderEnum {
            Asc, Desc
        }


        operator fun invoke(element: ExecutableElement, daoHandler: DaoHandler): NamedMethod? {
            val name = element.simpleName.toString()
            return name.run {
                when {
                    startsWith("queryBy") || startsWith("findBy") -> Query(element, daoHandler)
                    startsWith("queryOneBy") || startsWith("findOneBy") -> QueryOne(element, daoHandler)
                    startsWith("countBy") -> Count(element, daoHandler)
                    startsWith("deleteBy") -> Delete(element, daoHandler)
                    else -> null
                }
            }
        }

    }

    open class Query(element: ExecutableElement, daoHandler: DaoHandler) : NamedMethod(element, daoHandler) {
        init {
            val returnType = resultHelper.returnType
            if (!returnType.erasure().isAssignable(Collection::class)) {
                throw HandlerException("The return type of method must be a collection")
            }
            returnType as DeclaredType
            if (!crudEntity.type.isSameType(returnType.findTypeArgument(Collection::class.type, "E")!!)) {
                throw HandlerException("The element type of the return type must be a ${crudEntity.type.typeName}")
            }
        }

        override fun getTemplate(): String {
            //language=xml
            return """
                    <select>
                    select ${buildSelectList()} from ${crudEntity.dbFullyQualifiedName} ${buildWhereClause()} ${buildOrderClause()}
                    </select>
                """.trimIndent()
        }
    }

    open class QueryOne(element: ExecutableElement, daoHandler: DaoHandler) : NamedMethod(element, daoHandler) {
        init {
            if (!crudEntity.type.isSameType(resultHelper.returnType)) {
                throw HandlerException("The return type of method must be ${crudEntity.type}")
            }
        }

        override fun getTemplate(): String {
            //language=xml
            return """
                    <select>
                    select ${buildSelectList()} from ${crudEntity.dbFullyQualifiedName} ${buildWhereClause()} ${buildOrderClause()}
                    </select>
                """.trimIndent()
        }
    }

    class Delete(element: ExecutableElement, daoHandler: DaoHandler) : NamedMethod(element, daoHandler) {
        init {
            returnUpdateCount = true
        }

        override fun getTemplate(): String {
            //language=xml
            return """
                    <delete>
                    delete from ${crudEntity.dbFullyQualifiedName} ${buildWhereClause()}
                    </delete>
                """.trimIndent()

        }
    }

    class Count(element: ExecutableElement, daoHandler: DaoHandler) : NamedMethod(element, daoHandler) {
        init {
            resultHelper.returnType.apply {
                if (!isSameType(Long::class.type) && !isSameType(Int::class.type)) {
                    throw HandlerException("The return type of count method must be Integer or Long")
                }
            }
            resultHelper.mappings += MappingData(source = "_cot",
                target = "",
                typeHandler = if (resultHelper.returnType.isSameType(Int::class)) {
                    Long2IntegerResultHandler::class.type
                } else {
                    ResultTypeHandler::class.type
                },
                targetType = null,
                constructorParameterIndex = -1)
        }

        override fun getTemplate(): String {
            //language=xml
            return """
                    <select>
                    select count(*) as _cot from ${crudEntity.dbFullyQualifiedName} ${buildWhereClause()}
                    </select>
                """.trimIndent()
        }
    }
}