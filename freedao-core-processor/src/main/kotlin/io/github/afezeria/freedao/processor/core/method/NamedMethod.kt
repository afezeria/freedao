package io.github.afezeria.freedao.processor.core.method

import io.github.afezeria.freedao.Long2IntegerResultHandler
import io.github.afezeria.freedao.processor.core.*
import io.github.afezeria.freedao.processor.core.processor.*
import java.util.*
import kotlin.reflect.full.primaryConstructor

abstract class NamedMethod private constructor(
    daoHandler: DaoHandler, method: LazyMethod,
) : AbstractMethodDefinition(daoHandler, method) {


    val crudEntity: EntityType

    //    var crudEntity: EntityObjectModel
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
        crudEntity = EntityType(
            daoHandler.crudEntityType
                ?: throw HandlerException("Method $qualifiedName requires Dao.crudEntity to be specified")
        )
        dbProperties = crudEntity.allFields.filter { it.exist }
        propertyMap = crudEntity.allFields.filter { it.exist }.associateBy { it.simpleName }
        parseName()
        checkParameters()
    }

    /**
     * 解析方法名，分离出查询条件和排序使用的属性
     */
    fun parseName() {
        val (_, _, condAndOrder) = methodNameSplitRegex.matchEntire(method.simpleName)!!.groupValues
        var list = groupingRegex.findAll(condAndOrder).mapTo(LinkedList()) { it.groupValues[0] }
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
        //允许在没有过滤条件的情况下单独存在排序字段
        // e.g. selectByOrderById
        if (conditions.isEmpty() && condAndOrder.startsWith("OrderBy") && condAndOrder.length > "OrderBy".length) {
            list = groupingRegex.findAll(condAndOrder.substring(7)).mapTo(LinkedList()) { it.groupValues[0] }
            hasOrderByClause = true
            propertyName = ""
        } else {
            if (propertyName.isNotEmpty()) {
                throw HandlerException("missing condition property ${crudEntity.qualifiedName}.${propertyName.replaceFirstChar { it.lowercase() }}")
            }
        }

        if (hasOrderByClause) {
            while (list.isNotEmpty()) {
                propertyName += list.pop()
                val property = propertyMap[propertyName.replaceFirstChar { it.lowercase() }]
                if (property != null) {
                    val next = list.peek() ?: throw HandlerException("missing sort keyword asc or desc")
                    if (next == "Asc" || next == "Desc") {
                        orderColumns += property to OrderEnum.valueOf(next)
                        propertyName = ""
                        list.pop()
                    }
                }
            }
        }
        if (propertyName.isNotEmpty()) {
            throw HandlerException("missing order property ${crudEntity.qualifiedName}.${propertyName.replaceFirstChar { it.lowercase() }}")
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
        val parameters = method.parameters.mapTo(LinkedList()) { it }
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
                if (param.type.isAssignable(type)) {
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
                    throw HandlerException("Missing ${it.qualifiedName} parameter")
                }
                val parameter = parameters.pop()
                parameterLastMatchIndex = parameterIdx
                parameterIdx++
                if (!parameter.type.isAssignable(it)) {
                    throw HandlerException("Parameter mismatch, the ${parameterIdx}th parameter type should be $it")
                }
                cond.params += parameter
            }
        }
    }

    protected open fun buildSelectList(): String {
        return crudEntity.allFields.filter { it.exist }.joinToString { it.columnName }
    }

    protected fun buildWhereClause(): String {
        return conditions.takeIf { it.isNotEmpty() }
            ?.joinToString(separator = " ", prefix = "where ") { cond ->
                cond.render()
            } ?: ""
    }

    protected fun buildOrderClause(): String {
        return orderColumns.takeIf { it.isNotEmpty() }
            ?.joinToString(prefix = " order by ") { (prop, enum) ->
                "${prop.columnName.sqlQuote()} ${enum.name.lowercase()}"
            } ?: ""
    }

    open class Query(daoHandler: DaoHandler, method: LazyMethod) : NamedMethod(daoHandler, method) {
        init {
            init()
        }

        open fun init() {
            if (returnTypeContainerType == null) {
                throw HandlerException("The return type of method must be a collection")
            }
            if (!crudEntity.isSameType(returnTypeItemType)) {
                throw HandlerException("The element type of the return type must be a ${crudEntity.qualifiedName}")
            }
            //todo 上面两个判断可以用下面这个替换
//            if (returnTypeContainerType == null || !returnTypeItemType.isSameType(crudEntity)) {
//                throw HandlerException("The return type must be assignable to Collection<${crudEntity}>")
//            }
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

    open class QueryOne(daoHandler: DaoHandler, method: LazyMethod) : Query(daoHandler, method) {
        override fun init() {
            if (returnTypeContainerType != null || !crudEntity.isSameType(returnTypeItemType)) {
                throw HandlerException("The return type of method must be ${crudEntity.qualifiedName}")
            }
        }
    }


    open class DtoQuery(daoHandler: DaoHandler, method: LazyMethod) : Query(daoHandler, method) {
        override fun init() {
            if (returnTypeContainerType == null) {
                throw HandlerException("The return type of method must be a collection")
            }
            if (returnTypeItemType !is BeanType) {
                throw HandlerException("The element type of the return type must be a java bean")
            }
            checkMapping()
        }

        open fun checkMapping() {
            //从映射中移除crudEntity中不存在的字段
            mappings.removeIf { m ->
                dbProperties.find { m.source == it.columnName || (m.target == it.simpleName && it.exist) }
                    ?.takeIf { it.type.isAssignable(m.targetTypeLA!!) }
                    ?.let {
                        m.source = it.columnName
                        //dto的映射中没有指定结果集处理器且使用entity的中对应字段的处理器
                        if (m.typeHandlerLA == null) {
                            m.typeHandlerLA = it.resultTypeHandle
                        }
                        false
                    } ?: true
            }
            if (mappings.isEmpty()) {
                throw HandlerException("There are no fields in common between entity(${crudEntity.qualifiedName}) and dto(${returnTypeItemType})")
            }
        }

        override fun buildSelectList(): String {
            return mappings.joinToString { "${it.source.sqlQuote()} as ${it.source.sqlQuote()}" }
        }
    }

    class DtoQueryOne(daoHandler: DaoHandler, method: LazyMethod) : DtoQuery(daoHandler, method) {
        override fun init() {
            if (returnTypeItemType !is BeanType) {
                throw HandlerException("The element type of the return type must be a java bean")
            }
            checkMapping()
        }
    }


    class Delete(daoHandler: DaoHandler, method: LazyMethod) : NamedMethod(daoHandler, method) {
        init {
            returnUpdateCount = true
            if (orderColumns.isNotEmpty()) {
                throw HandlerException("delete method cannot contain a sort")
            }
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

    class Count(daoHandler: DaoHandler, method: LazyMethod) : NamedMethod(daoHandler, method) {
        init {
            if (orderColumns.isNotEmpty()) {
                throw HandlerException("count method cannot contain a sort")
            }
            if (!returnTypeItemType.isSameType(Int::class) && !returnTypeItemType.isSameType(Long::class)) {
                throw HandlerException("The return type of count method must be Integer/int or Long/long")
            }

            mappings += MappingData(
                source = "_cot",
                target = "",
                typeHandlerLA = Long2IntegerResultHandler::class.typeLA.takeIf {
                    returnTypeItemType.isSameType(Int::class)
                },
                targetTypeLA = Int::class.typeLA.takeIf {
                    returnTypeItemType.isSameType(Int::class)
                },
                constructorParameterIndex = -1
            )
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

    companion object {

        private val methodNameSplitRegex = "^(.*?By)(.*)$".toRegex()
        private val length2ConditionKeys = listOf(
            3 to listOf("LessThanEqual", "GreaterThanEqual"),
            2 to listOf("IsNull", "LessThan", "GreaterThan", "NotNull", "NotIn", "NotLike", "StartsWith", "EndsWith"),
            1 to listOf("Between", "Like", "Not", "In", "True", "False"),
        )
        private val length2ConnectKeys = listOf(2 to listOf("OrderBy"), 1 to listOf("And", "Or"))
        private val queryPrefix = "(select|query|find)By[A-Za-z0-9]+".toRegex()
        private val queryOnePrefix = "(select|query|find)OneBy[A-Za-z0-9]+".toRegex()
        private val dtoQueryPrefix = "dto(Select|Query|Find)By[A-Za-z0-9]+".toRegex()
        private val dtoQueryOnePrefix = "dto(Select|Query|Find)OneBy[A-Za-z0-9]+".toRegex()
        private val countPrefix = "countBy[A-Za-z0-9]+".toRegex()
        private val deletePrefix = "(delete|remove)By[A-Za-z0-9]+".toRegex()

        /**
         * 查找条件关键字并将其从list中移除
         *
         * e.g. in: {"Is","Null","And","Name"} out: "IsNull" list:{"And","Name"}
         * @param list LinkedList<String>
         * @return String?
         */
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

        /**
         * 查找And/Or和OrderBy并将其从list中移除
         *
         * e.g. in: {"Order","By","Id","Asc"} out: "OrderBy" list:{"Id","Asc"}
         * @param list LinkedList<String>
         * @return String
         */
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

        operator fun invoke(daoHandler: DaoHandler, method: LazyMethod): NamedMethod? {
            val name = method.simpleName
            return name.run {
                when {
                    matches(queryPrefix) -> Query(daoHandler, method)
                    matches(queryOnePrefix) -> QueryOne(daoHandler, method)
                    matches(dtoQueryPrefix) -> DtoQuery(daoHandler, method)
                    matches(dtoQueryOnePrefix) -> DtoQueryOne(daoHandler, method)
                    matches(countPrefix) -> Count(daoHandler, method)
                    matches(deletePrefix) -> Delete(daoHandler, method)
                    else -> null
                }
            }
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
            private val requiredParameterTypesFn: Condition.() -> List<LazyType> = { listOf(property!!.type) },
            private val renderFn: Condition.() -> String,
        ) {


            var property: BeanProperty? = null
            val params = mutableListOf<LazyVariable>()
            val column: String
                get() = property!!.columnName.sqlQuote()

            val requiredParameterTypes by lazy { requiredParameterTypesFn(this) }

            fun render() = renderFn(this)

            /**
             * 根据方法参数索引创建sql参数字符串
             * @param methodParameterIndex Int
             * @return String
             */
            fun createSqlParameter(methodParameterIndex: Int) =
                "#{${params[methodParameterIndex].simpleName}${typeHandlerStr()}}"

            fun typeHandlerStr() = property?.parameterTypeHandle?.let { ",typeHandler=${it.qualifiedName}" } ?: ""

            class LessThanEqual : Condition(renderFn = { "$column &lt;= ${createSqlParameter(0)}" })
            class GreaterThanEqual : Condition(renderFn = { "$column &gt;= ${createSqlParameter(0)}" })
            class NotNull :
                Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "$column is not null" })

            class IsNull : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "$column is null" })
            class LessThan : Condition(renderFn = { "$column &lt; ${createSqlParameter(0)}" })
            class GreaterThan : Condition(renderFn = { "$column &gt; ${createSqlParameter(0)}" })
            class NotIn : Condition(
                requiredParameterTypesFn = { listOf(Collection::class.typeLA(property!!.type)) },
                renderFn = {
                    "$column not in (<foreach collection='${params[0].simpleName}' item='item' separator=','>#{item${typeHandlerStr()}}</foreach>)"
                }
            )

            class NotLike : Condition(renderFn = { "$column not like ${createSqlParameter(0)}" })
            class Between : Condition(
                requiredParameterTypesFn = { listOf(property!!.type, property!!.type) },
                renderFn = { "$column between ${createSqlParameter(0)} and ${createSqlParameter(1)}" },
            )

            class Like : Condition(renderFn = { "$column like <java>\"%\"+\${${params[0].simpleName}}+\"%\"</java>" })

            class StartsWith : Condition(renderFn = { "$column like <java>\${${params[0].simpleName}}+\"%\"</java>" })

            class EndsWith : Condition(renderFn = { "$column like <java>\"%\"+\${${params[0].simpleName}}</java>" })

            class Not : Condition(renderFn = { "$column &lt;&gt; ${createSqlParameter(0)}" })
            class In : Condition(
                requiredParameterTypesFn = { listOf(Collection::class.typeLA(property!!.type)) },
                renderFn = {
                    "$column in (<foreach collection='${params[0].simpleName}' item='item' separator=','>#{item${typeHandlerStr()}}</foreach>)"
                }
            )

            class True : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "$column = true" })
            class False : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "$column = false" })

            /**
             * Is不能作为关键字出现在方法名中
             * 当字段名后没有接其他条件关键字或Order时当作Is处理
             */
            class Is : Condition(renderFn = { "$column = ${createSqlParameter(0)}" })
            class And : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "and" })
            class Or : Condition(requiredParameterTypesFn = { emptyList() }, renderFn = { "or" })
            companion object {
                private val name2Constructor =
                    Condition::class.sealedSubclasses.associate { it.simpleName to it.primaryConstructor }

                operator fun invoke(name: String): Condition {
                    return name2Constructor[name]?.call() ?: throw IllegalStateException()
                }
            }
        }

        enum class OrderEnum {
            Asc, Desc
        }

    }
}