package io.github.afezeria.freedao.processor.core

import com.squareup.javapoet.TypeName
import io.github.afezeria.freedao.annotation.Column
import io.github.afezeria.freedao.annotation.Join
import io.github.afezeria.freedao.annotation.ReferenceValue
import io.github.afezeria.freedao.annotation.Table
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 *
 */
open class BeanObjectModel(val type: DeclaredType) {
    val className: TypeName = type.typeName
    val element = type.asElement() as TypeElement
    var properties: MutableList<BeanProperty> = mutableListOf()

    init {
        element.enclosedElements.filter { it.kind == ElementKind.FIELD && it.hasGetter() }
            .forEach { properties += BeanProperty(it as VariableElement) }
        //处理父类属性
        var superType = element.superclass
        while (superType.isCustomJavaBean()) {
            val el = superType.asElement() as TypeElement
            el.enclosedElements
                .filter {
                    it.kind == ElementKind.FIELD
                            && it.hasGetter()
                            && properties.none { p -> p.name == it.simpleName.toString() }
                }
                .forEach {
                    properties += BeanProperty(it as VariableElement)
                }

            superType = el.superclass
        }
    }
}

class EntityObjectModel private constructor(type: DeclaredType) : BeanObjectModel(type) {
    val table: String
    var schema: String = ""
    var primaryKey: List<BeanProperty>

    init {
        element.getAnnotation(Table::class.java)!!.let {
            table = it.name.ifBlank {
                it.value.ifBlank {
                    type.simpleName.toSnakeCase()
                }
            }
            schema = it.schema
            primaryKey = properties.filter { prop ->
                prop.column.name in it.primaryKeys
            }
        }
        checkJoinAnnotation()
    }

    fun checkJoinAnnotation() {
        val joinMap = mutableMapOf<String, TypeMirror?>()
        for (join in element.getAnnotationsByType(Join::class.java)) {
            if (join.id.isBlank()) {
                throw HandlerException("$type Join.id cannot be blank")
            }
            if (join.foreignKey.isEmpty()) {
                throw HandlerException("$type Join.foreignKey cannot be empty")
            }
            val foreignKey = join.foreignKey.map {
                properties.find { p -> p.column.name == it }
                    ?: throw HandlerException("${type.simpleName}: foreign key column does not exist, no property mapped to $it")
            }
            val entityClass = join.mirroredType { entityClass }.takeIf { !it.isSameType(Any::class) }
            joinMap[join.id] = entityClass
            if (entityClass == null) {
                if (join.table.isBlank()) {
                    throw HandlerException("$type Join.Table cannot be blank when Join.entityClass is not specified")
                }
                if (join.referenceKey.isEmpty()) {
                    throw HandlerException("$type Join.referenceKey cannot be empty when Join.entityClass is not specified")
                }
                if (join.referenceKey.size != foreignKey.size) {
                    throw HandlerException("$type Join.referenceKey and Join.foreignKey must be the same length")
                }
            } else {
                validationEntity(entityClass)
                val entityClassElement = entityClass.asElement()
                val joinTable = entityClassElement.getAnnotation(Table::class.java)
                val joinTableColumnName2Type = entityClassElement.enclosedElements.asSequence()
                    .filter {
                        it.kind == ElementKind.FIELD && !it.modifiers.contains(Modifier.STATIC)
                                && it.getAnnotation(Column::class.java)?.exist ?: true
                    }.associateBy {
                        (it.getAnnotation(Column::class.java)
                            ?.name?.takeIf { it.isNotBlank() } ?: it.simpleName.toString()
                            .toSnakeCase())
                    }
                val realReferenceKey: Array<String> = if (join.referenceKey.isNotEmpty()) {
                    join.referenceKey
                } else {
                    if (joinTable.primaryKeys.isEmpty()) {
                        throw HandlerException("$entityClass no primary key specified")
                    } else {
                        joinTable.primaryKeys
                    }
                }
                realReferenceKey.forEachIndexed { index, s ->
                    val propElement = joinTableColumnName2Type[s]
                        ?: throw HandlerException("missing property mapped to column $s in $entityClass")
                    val propType = propElement.asType()
                    val fkProperty = foreignKey[index]
                    val fkType = fkProperty.type
                    if (!fkType.isSameType(propType)) {
                        throw HandlerException("foreign key field and reference key field have different types. index:$index, field: ${type.simpleName}.${fkProperty.name}:${fkType}, ${entityClass.simpleName}.${propElement.simpleName}:${propType}")
                    }
                }
            }
        }
        //检查关联属性
        for (property in properties) {
            property.referenceValue?.let {
                if (!joinMap.containsKey(it.joinId)) {
                    throw HandlerException("${type.simpleName}.${property.name}: joinId:${it.joinId} does not exist")
                }
                //根据entityClass关联时查找引用的字段
                //字段不存在时忽略
                //存在时检查类型是否一致，不一致时抛出异常
                joinMap[it.joinId]?.let { entityType ->
                    entityType as DeclaredType
                    for (element in entityType.asElement().enclosedElements) {
                        if (element.kind == ElementKind.FIELD && !element.modifiers.contains(Modifier.STATIC)) {
                            val joinColumnName = element.getAnnotation(Column::class.java)?.let {
                                it.name.takeIf { it.isNotBlank() }
                            } ?: element.simpleName.toString().toSnakeCase()

                            if (joinColumnName == it.columnName) {
                                if (!property.type.isSameType(element.asType())) {
                                    throw HandlerException("inconsistent reference field types: ${type.simpleName}.${property.name}:${property.type}, ${entityType.simpleName}.${element.simpleName}:${element.asType()}")
                                }
                                break
                            }
                        }

                    }
                }
            }
        }
    }

    val dbFullyQualifiedName: String by lazy {
        if (schema.isBlank()) {
            table.sqlQuote()
        } else {
            schema.sqlQuote() + "." + table.sqlQuote()
        }
    }

    companion object {

        @OptIn(ExperimentalContracts::class)
        fun validationEntity(type: TypeMirror) {
            contract {
                returns() implies (type is DeclaredType)
            }
            if (!type.isCustomJavaBean()
                || (type.asElement() as TypeElement).getAnnotation(Table::class.java) == null
            ) {
                throw HandlerException("The class that are arguments to Dao.crudEntity must be custom java bean and annotated by ${Table::class.qualifiedName}")
            }
        }

        operator fun invoke(type: TypeMirror): EntityObjectModel {
            validationEntity(type)
            return EntityObjectModel(type)
        }

    }
}

class BeanProperty(
    val element: VariableElement,
) {
    val name: String = element.simpleName.toString()
    val type: TypeMirror = element.asType()

    val hasSetter by lazy {
        element.hasSetter()
    }

    //判断流程，方法上做了映射按方法的来
    //实体类做了映射按实体类的来
    //都没有就将变量名转成下划线风格
    val column: ColumnAnn = ColumnAnn(element)
    val referenceValue: ReferenceValue? = element.getAnnotation(ReferenceValue::class.java)

    fun toSelectItem(tableAlias: String = ""): String {
        return "${tableAlias.replace(".+".toRegex()) { it.value + "." }}${column.name.sqlQuote()} as ${column.name.sqlQuote()}"
    }

    fun sqlParameterStr(parameterName: String): String {
        return "#{$parameterName.$name${column.parameterTypeHandle?.let { ",typeHandler=${it}" } ?: ""}}"
    }

    override fun toString(): String {
        return "BeanProperty(name='$name', type=$type)"
    }

    val setterName: String by lazy { "set${name.replaceFirstChar { it.uppercaseChar() }}" }

}