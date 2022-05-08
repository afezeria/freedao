package io.github.afezeria.freedao.processor.core

import com.squareup.javapoet.TypeName
import io.github.afezeria.freedao.annotation.Table
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

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

    }

    val dbFullyQualifiedName: String by lazy {
        if (schema.isBlank()) {
            table.sqlQuote()
        } else {
            schema.sqlQuote() + "." + table.sqlQuote()
        }
    }

    companion object {
        operator fun invoke(type: TypeMirror): EntityObjectModel {
            if (!type.isCustomJavaBean()
                || (type.asElement() as TypeElement).getAnnotation(Table::class.java) == null
            ) {
                throw HandlerException("The class that are arguments to Dao.crudEntity must be custom java bean and annotated by ${Table::class.qualifiedName}")
            }
            return EntityObjectModel(type)
        }

    }
}