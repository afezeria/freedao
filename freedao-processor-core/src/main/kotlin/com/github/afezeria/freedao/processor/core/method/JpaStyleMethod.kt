package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.processor.core.*
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

class JpaStyleMethod(
    element: ExecutableElement, daoModel: DaoModel,
) : MethodModel(element, daoModel) {

    lateinit var crudEntity: EntityObjectModel
    lateinit var existProperties: List<BeanProperty>

    lateinit var prefix: PrefixEnum
    var conditions: MutableList<Pair<BeanProperty?, ConditionalKeywordEnum>> = mutableListOf()
    var orderColumns: MutableList<Pair<BeanProperty, OrderEnum>> = mutableListOf()

    init {
        daoModel.crudEntity?.let {
            crudEntity = it
            existProperties = crudEntity.getProperties().filter { it.column.exist }
        } ?: run {
            throw HandlerException(
                "Method $name requires that the crudEntity attribute to be specified in the Dao annotation of the interface",
            )
        }
        parseName()
        checkParameters()
        checkReturnType()
    }

    private fun checkReturnType() {
        if (!prefix.getReturnType(crudEntity.typeMirror).isSameType(element.returnType)) {
            throw HandlerException(
                "The return type of method $name must be ${prefix.getReturnType(crudEntity.typeMirror)}",
            )
        }
    }

    /**
     * 检查参数类型与数量
     */
    private fun checkParameters() {
        val parameters = element.parameters.mapTo(LinkedList()) { it }
        conditions.forEach { (property, kw) ->
            if (property != null) {
                kw.requiredParameterType(property.type).forEach {
                    if (parameters.isEmpty()) {
                        throw HandlerException("Missing parameter of type ${it.typeName}")
                    }
                    val parameter = parameters.pop()
                    if (!parameter.asType().isAssignable(it)) {
                        throw HandlerException("Parameter ${parameter.simpleName} should be of type ${parameter.asType().typeName}")
                    }
                }
            }
        }
    }

    /**
     * 解析方法名，分离出查询条件和排序使用的属性
     */
    private fun parseName() {
        val (_, prefixStr, condAndOrder) = methodNameSplitRegex.matchEntire(name)!!.groupValues
        prefix = PrefixEnum.valueOf(prefixStr.replaceFirstChar { it.uppercase() })
        var orderByClause: String? = null
        var left = 0
        var length = 1
        var propertyName: String? = null
        var after: String?
        while (left + length <= condAndOrder.length && left < condAndOrder.length) {
            propertyName = condAndOrder.substring(left, left + length).replaceFirstChar { it.lowercase() }
            after = condAndOrder.substring(left + length)
            val property = existProperties.find { it.name == propertyName }
            val keyword = keywordRegex.find(after)?.groupValues?.run {
                get(1).ifEmpty { get(2) }
            }
            if (property != null && keyword != null) {
                if (keyword == "OrderBy") {
                    conditions += property to ConditionalKeywordEnum.Is
                    orderByClause = after.substring(7)
                    propertyName = null
                    break
                } else {
                    when (keyword) {
                        "And", "Or" -> {
                            conditions += property to ConditionalKeywordEnum.Is
                            conditions += null to ConditionalKeywordEnum.valueOf(keyword)
                        }
                        // 当关键字为空字符串时当作Is处理
                        "" -> conditions += property to ConditionalKeywordEnum.Is
                        else -> conditions += property to ConditionalKeywordEnum.valueOf(keyword)
                    }
                    propertyName = null
                    left += length + keyword.length
                    length = 0
                    continue
                }
            }
            length++
        }
        if (propertyName != null) {
            throw HandlerException("missing property ${crudEntity.className}.${propertyName}")
        }
        if (orderByClause != null) {
            orderByItemRegex.findAll(orderByClause)
                .forEach { m ->
                    existProperties.find { it.name.replaceFirstChar { it.uppercase() } == m.groupValues[1] }
                        ?.let {
                            orderColumns += it to OrderEnum.valueOf(m.groupValues[2])
                        } ?: throw HandlerException("missing property ${crudEntity.className}.${propertyName}")
                }
        }
    }

    override fun getTemplate(): String {
        return when (prefix) {
            PrefixEnum.DeleteBy -> {
                //language=xml
                """
                    <delete>
                    delete from ${crudEntity.dbFullyQualifiedName} ${buildWhereClause()}
                    </delete>
                """.trimIndent()
            }
            else -> {
                //language=xml
                """
                    <select>
                    select ${buildSelectList()} from ${crudEntity.dbFullyQualifiedName} ${buildWhereClause()} ${buildOrderClause()}
                    </select>
                """.trimIndent()
            }
        }
    }

    private fun buildSelectList(): String {
        return crudEntity.getProperties().filter { it.column.exist }
            .joinToString { it.toSelectItem() }
    }

    private fun buildWhereClause(): String {
        val params = element.parameters.mapTo(LinkedList()) { "#{${it.simpleName}}" }
        return conditions.joinToString(separator = " ", prefix = "where ") { (property, keyword) ->
            keyword.render(listOf(property?.toSelectItem(),
                *(0 until keyword.numberOfParameter).map { params.pop() }.toTypedArray()))
        }
    }

    private fun buildOrderClause(): String {
        return orderColumns.joinToString(prefix = "order by ") { (prop, enum) ->
            "${prop.toSelectItem()} ${enum.name.lowercase()}"
        }
    }


    companion object {
        private val methodNameSplitRegex = "^(.*?By)(.*)$".toRegex()
        val keywordRegex =
            "^(LessThanEqual|GreaterThanEqual|IsNotNull|IsNull|LessThan|GreaterThan|NotIn|NotLike|Between|After|Before|Like|Not|In|True|False|)(And|OrderBy|Or|$)".toRegex()

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
                render = { "${it[0]} not null" }
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

        /**
         * 方法前缀
         */
        enum class PrefixEnum(
            /**
             * 输入crudEntity的类型返回方法返回值的类型
             */
            val getReturnType: (TypeMirror) -> TypeMirror,
        ) {
            FindBy({ List::class.type(it) }),
            FindOneBy({ it }),
            QueryBy({ List::class.type(it) }),
            QueryOneBy({ it }),
            CountBy({ Long::class.type() }),
            DeleteBy({ Long::class.type() }),
            ;
        }

        private val orderByItemRegex = "(.*?)(Asc|Desc)((?=[A-Z])|$)".toRegex()

        fun match(element: Element): Boolean {
            val name = element.simpleName.toString().replaceFirstChar { it.uppercase() }
            return PrefixEnum.values()
                .any { name.startsWith(it.name) }
        }

    }
}