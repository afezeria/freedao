package com.github.afezeria.freedao.processor.core

import com.github.afezeria.freedao.annotation.Table
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ElementKind
import javax.lang.model.element.VariableElement
import javax.lang.model.type.*

/**
 *
 */
sealed class ObjectModel(val typeMirror: TypeMirror) {

    companion object {
        operator fun invoke(type: TypeMirror): ObjectModel {
            return when (type) {
                is DeclaredType -> {
                    val element = type.asElement()

                    if (type.isSubtype(Iterable::class)) {
                        IterableObjectModel(type)
                    } else if (type.isSubtype(Map::class.type)) {
                        MapObjectModel(type)
                    } else if (element.getAnnotation(Table::class.java) != null) {
                        EntityObjectModel(type)
                    } else {
                        BeanObjectModel(type)
                    }
                }
                is ArrayType -> IterableObjectModel(type)
                is PrimitiveType -> PrimitiveObjectModel(type)
                is NoType -> VoidObjectModel(type)
                else ->
                    throw IllegalStateException()
            }
        }
    }
}

open class BeanObjectModel(val type: DeclaredType) : ObjectModel(type) {
    val className: TypeName = type.typeName
    val element = type.asElement()
}

class EntityObjectModel(type: DeclaredType) : BeanObjectModel(type) {
    val table: String
    var schema: String = ""
    var primaryKey: MutableList<BeanProperty> = mutableListOf()
    lateinit var properties: List<BeanProperty>

    init {
        element.getAnnotation(Table::class.java)!!.let {
            table = it.name.ifBlank {
                type.simpleName.toSnakeCase()
            }
            if (it.schema.isNotBlank()) {
                schema = it.schema
            }
            it.primaryKeys.forEach { fieldName ->
                element.enclosedElements.forEach { fieldElement ->
                    if (fieldElement.kind == ElementKind.FIELD && fieldElement.simpleName.toString() == fieldName) {
                        primaryKey += BeanProperty(fieldElement as VariableElement)
                    }
                }
            }
        }
        properties = element.enclosedElements.filter { it.kind == ElementKind.FIELD && it.hasGetter() }
            .map { BeanProperty(it as VariableElement) }
    }

    val dbFullyQualifiedName: String by lazy {
        if (schema.isBlank()) {
            table.sqlQuote()
        } else {
            schema.sqlQuote() + "." + table.sqlQuote()
        }
    }
}


class VoidObjectModel(type: NoType) : ObjectModel(type)
class IterableObjectModel(type: TypeMirror) : ObjectModel(type) {
    lateinit var itemType: ObjectModel
}

class MapObjectModel(type: DeclaredType) : ObjectModel(type) {

}

class PrimitiveObjectModel(val type: PrimitiveType) : ObjectModel(type)

enum class PrimitiveTypeEnum {
    BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN;

    companion object {
        fun of(type: TypeMirror): PrimitiveTypeEnum {
            Char::class.javaPrimitiveType
            return when (type.kind) {
                TypeKind.BYTE -> BYTE
                TypeKind.CHAR -> CHAR
                TypeKind.SHORT -> SHORT
                TypeKind.INT -> INT
                TypeKind.LONG -> LONG
                TypeKind.FLOAT -> FLOAT
                TypeKind.DOUBLE -> DOUBLE
                TypeKind.BOOLEAN -> BOOLEAN
                else -> throw IllegalArgumentException()
            }
        }
    }
}