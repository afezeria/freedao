package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.Long2IntegerResultHandler
import com.github.afezeria.freedao.processor.core.*
import java.util.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

abstract class NamedMethod private constructor(
    element: ExecutableElement, daoModel: DaoModel,
) : MethodModel(element, daoModel) {

    lateinit var crudEntity: EntityObjectModel
    lateinit var dbProperties: List<BeanProperty>
    lateinit var propertyMap: Map<String, BeanProperty>

    /**
     * 当关键字为And/Or时pair.first为null
     */
    var conditions: MutableList<Pair<BeanProperty?, ConditionalKeywordEnum>> = mutableListOf()
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
        daoModel.crudEntity?.let {
            crudEntity = it
            dbProperties = crudEntity.getProperties().filter { it.column.exist }
            propertyMap = crudEntity.getProperties().filter { it.column.exist }.associateBy { it.name }
        } ?: run {
            throw HandlerException(
                "Method $name requires that the crudEntity attribute to be specified in the Dao annotation of the interface",
            )
        }
        parseName()
        checkParameters()
    }

//    /**
//     * 解析方法名，分离出查询条件和排序使用的属性
//     */
//    private fun parseName() {
//        val (_, _, condAndOrder) = methodNameSplitRegex.matchEntire(name)!!.groupValues
//        var orderByClause: String? = null
//        //property name start index
//        var start = 0
//        //property name length
//        var length = 1
//
//        var left: String? = null
//        var right: String?
//        while (start + length <= condAndOrder.length && start < condAndOrder.length) {
//            left = condAndOrder.substring(start, start + length).replaceFirstChar { it.lowercase() }
//            right = condAndOrder.substring(start + length)
//            val property = dbProperties.find { it.name == left }
//            val keyword = keywordRegex.find(right)?.groupValues?.run {
//                get(1).ifEmpty { get(2) }
//            }
//            if (property != null && keyword != null) {
//                if (keyword == "OrderBy") {
//                    conditions += property to ConditionalKeywordEnum.Is
//                    orderByClause = right.substring(7)
//                    left = null
//                    break
//                } else {
//                    when (keyword) {
//                        "And", "Or" -> {
//                            conditions += property to ConditionalKeywordEnum.Is
//                            conditions += null to ConditionalKeywordEnum.valueOf(keyword)
//                        }
//                        // 当关键字为空字符串时当作Is处理
//                        "" -> conditions += property to ConditionalKeywordEnum.Is
//                        else -> conditions += property to ConditionalKeywordEnum.valueOf(keyword)
//                    }
//                    left = null
//                    start += length + keyword.length
//                    length = 0
//                    continue
//                }
//            }
//            length++
//        }
//        if (left != null) {
//            throw HandlerException("missing property ${crudEntity.className}.${left}")
//        }
//        if (orderByClause != null) {
//            orderByItemRegex.findAll(orderByClause).forEach { m ->
//                left = m.groupValues[1].replaceFirstChar { it.lowercase() }
//                dbProperties.find { it.name == left }?.let {
//                    orderColumns += it to OrderEnum.valueOf(m.groupValues[2])
//                } ?: throw HandlerException("missing property ${crudEntity.className}.${left}")
//            }
//        }
//    }

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
                        conditions += property to ConditionalKeywordEnum.valueOf(conditionKey ?: "Is")
                        propertyName = ""
                        hasOrderByClause = true
                        break
                    }
                    "And", "Or" -> {
                        conditions += property to ConditionalKeywordEnum.valueOf(conditionKey ?: "Is")
                        propertyName = ""
                        conditions += null to ConditionalKeywordEnum.valueOf(connectKey)
                    }
                    "" -> {
                        if (list.isEmpty()) {
                            conditions += property to ConditionalKeywordEnum.valueOf(conditionKey ?: "Is")
                            propertyName = ""
                            break
                        }
                    }
                }
            }
        }
        if (propertyName.isNotEmpty()) {
            throw HandlerException("missing property ${crudEntity.className}.${propertyName}")
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
            throw HandlerException("missing order property ${crudEntity.className}.${propertyName}")
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
        val conditionsThatRequireParameter =
            conditions
                .filter { (prop, cond) ->
                    //过滤And/Or(当关键字为and/or时pair的first为null)和不需要参数的条件
                    prop != null && cond.requiredParameterType(prop.type).isNotEmpty()
                }
        if (conditionsThatRequireParameter.isEmpty()) {
            return
        }
        //找到方法参数列表中和表达式需要的第一个参数匹配的参数的缩影，并将这之前的参数从parameters中移除
        conditionsThatRequireParameter
            .first { (p, kw) -> kw.requiredParameterType(p!!.type).isNotEmpty() }
            .let { (p, kw) ->
                val type = kw.requiredParameterType(p!!.type).first()
                while (parameters.isNotEmpty()) {
                    val param = parameters.first
                    if (param.simpleName.toString() == p.name && param.asType().isSameType(type)) {
                        break
                    } else {
                        parameters.pop()
                        parameterIdx++
                    }
                }
            }
        parameterFirstMatchIndex = parameterIdx
        conditionsThatRequireParameter.forEach { (p, kw) ->
            requireNotNull(p)
            kw.requiredParameterType(p.type).forEach {
                if (parameters.isEmpty()) {
                    throw HandlerException("Missing parameter ${p.name}:${it.typeName}")
                }
                val parameter = parameters.pop()
                parameterLastMatchIndex = parameterIdx
                parameterIdx++
                if (!parameter.asType().isSameType(it) || parameter.simpleName.toString() != p.name) {
                    throw HandlerException("Parameter mismatch, the ${parameterIdx}th parameter should be ${p.name}:${it}")
                }
            }
        }
    }

    protected fun buildSelectList(): String {
        return crudEntity.getProperties().filter { it.column.exist }.joinToString { it.toSelectItem() }
    }

    protected fun buildWhereClause(): String {
        return conditions.joinToString(separator = " ", prefix = "where ") { (property, keyword) ->
            keyword.render(listOf(
                property?.column?.name,
                property?.column?.name?.let { "#{$it}" },
            ))
        }
    }

    protected fun buildOrderClause(): String {
        return orderColumns.joinToString() { (prop, enum) ->
            "${prop.column.name.sqlQuote()} ${enum.name.lowercase()}"
        }.takeIf { it.isNotBlank() }?.let { "order by $it" } ?: ""
    }


    companion object {
        private val methodNameSplitRegex = "^(.*?By)(.*)$".toRegex()
//        val keywordRegex =
//            "^(LessThanEqual|GreaterThanEqual|IsNotNull|IsNull|LessThan|GreaterThan|NotIn|NotLike|Between|After|Before|Like|Not|In|True|False|)(And|OrderBy|Or|$)".toRegex()

        private val length2ConditionKeys = listOf(
            3 to listOf("LessThanEqual", "GreaterThanEqual", "IsNotNull"),
            2 to listOf("IsNull", "LessThan", "GreaterThan", "NotIn", "NotLike"),
            1 to listOf("Between", "After", "Before", "Like", "Not", "In", "True", "False"),
        )
        private val length2ConnectKeys = listOf(2 to listOf("OrderBy"), 1 to listOf("And", "Or"))

        fun findConditionKey(list: LinkedList<String>): String? {
            var s = ""
            for ((length, keys) in length2ConditionKeys) {
                if (list.size > length) {
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
         * 支持的关键字
         */
        enum class ConditionalKeywordEnum(
            val numberOfParameter: Int = 1,
            /**
             * 输入实体类属性的类型，返回的操作符要求的参数类型
             */
            val requiredParameterType: (TypeMirror) -> List<TypeMirror> = { listOf(it) },
            val render: (List<String?>) -> String,
        ) {
            LessThanEqual(render = { "${it[0]} <= ${it[1]}" }),
            GreaterThanEqual(render = { "${it[0]} >= ${it[1]}" }),
            IsNotNull(
                numberOfParameter = 0,
                requiredParameterType = { emptyList() },
                render = { "${it[0]} is not null" }
            ),
            IsNull(
                numberOfParameter = 0,
                requiredParameterType = { emptyList() },
                render = { "${it[0]} is null" }
            ),
            LessThan(render = { "${it[0]} < ${it[1]}" }),
            GreaterThan(render = { "${it[0]} > ${it[1]}" }),
            NotIn(
                requiredParameterType = { listOf(Collection::class.type(it)) },
                render = { "${it[0]} not in ${it[1]}" }
            ),
            NotLike(render = { "${it[0]} not like ${it[1]}" }),
            Between(
                numberOfParameter = 2,
                requiredParameterType = { listOf(it, it) },
                render = { "${it[0]} between ${it[1]} and ${it[2]}" },
            ),
            After(render = { "${it[0]} > ${it[1]}" }),
            Before(render = { "${it[0]} < ${it[1]}" }),
            Like(render = { "${it[0]} like ${it[1]}" }),
            Not(render = { "${it[0]} <> ${it[1]}" }),
            In(
                requiredParameterType = { listOf(Collection::class.type(it)) },
                render = { "${it[0]} in ${it[1]}" }
            ),
            True(
                numberOfParameter = 0,
                requiredParameterType = { emptyList() },
                render = { "${it[0]} = true" }
            ),
            False(
                numberOfParameter = 0,
                requiredParameterType = { emptyList() },
                render = { "${it[0]} = false" }
            ),

            /**
             * 当关键字为空字符串时当作Is处理
             */
            Is(render = { "${it[0]} = ${it[1]}" }),
            And(
                numberOfParameter = 0,
                requiredParameterType = { emptyList() },
                render = { "and" }
            ),
            Or(
                numberOfParameter = 0,
                requiredParameterType = { emptyList() },
                render = { "or" }
            ),
            ;
        }

        enum class OrderEnum {
            Asc, Desc
        }

//        private val orderByItemRegex = "(.*?)(Asc|Desc)((?=[A-Z])|$)".toRegex()

        private val prefixList = listOf("queryBy", "queryOneBy", "findBy", "findOneBy", "countBy", "deleteBy")

        fun match(element: ExecutableElement): Boolean {
            val name = element.simpleName.toString()
            return prefixList.any { name.startsWith(it) && name.length > it.length && name[it.length].isUpperCase() }
        }

        operator fun invoke(element: ExecutableElement, daoModel: DaoModel): NamedMethod {
            val name = element.simpleName.toString()
            return name.run {
                when {
                    startsWith("queryBy") || startsWith("findBy") -> Query(element, daoModel)
                    startsWith("queryOneBy") || startsWith("findOneBy") -> QueryOne(element, daoModel)
                    startsWith("countBy") -> Count(element, daoModel)
                    startsWith("deleteBy") -> Delete(element, daoModel)
                    else -> throw IllegalStateException()
                }
            }
        }

    }

    open class Query(element: ExecutableElement, daoModel: DaoModel) : NamedMethod(element, daoModel) {
        init {
            val returnType = resultHelper.returnType
            if (!returnType.erasure().isAssignable(Collection::class)) {
                throw HandlerException("The container type of the return type must be a subclass of collection")
            }
            returnType as DeclaredType
            if (!crudEntity.type.isSameType(returnType.findTypeArgument(Collection::class.type, "E")!!)) {
                throw HandlerException("The item type of the return type must be ${crudEntity.type.typeName}")
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

    open class QueryOne(element: ExecutableElement, daoModel: DaoModel) : NamedMethod(element, daoModel) {
        init {
            if (!crudEntity.type.isSameType(resultHelper.returnType)) {
                throw HandlerException("return type must be ${crudEntity.type}")
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

    class Delete(element: ExecutableElement, daoModel: DaoModel) : NamedMethod(element, daoModel) {
        init {
            resultHelper.returnType.apply {
                if (!isSameType(Long::class.type) && !isSameType(Int::class.type)) {
                    throw HandlerException("return type must be Integer or Long")
                }
            }
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

    class Count(element: ExecutableElement, daoModel: DaoModel) : NamedMethod(element, daoModel) {
        init {
            resultHelper.returnType.apply {
                if (!isSameType(Long::class.type) && !isSameType(Int::class.type)) {
                    throw HandlerException("return type must be Integer or Long")
                }
            }
            resultHelper.mappings += MappingData(source = "_cot",
                target = "",
                typeHandler = Long2IntegerResultHandler::class.type,
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