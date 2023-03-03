package io.github.afezeria.freedao.processor.core.processor

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.ParameterTypeHandler
import io.github.afezeria.freedao.ResultTypeHandler
import io.github.afezeria.freedao.annotation.Table
import io.github.afezeria.freedao.processor.core.*
import javax.lang.model.element.*
import kotlin.reflect.KClass

/**
 * @author afezeria
 */
lateinit var typeService: TypeService

interface TypeService {
    /**
     * @throws HandlerException 类名 [className] 不存在时抛出
     * @param string String
     * @return LazyType
     */
    fun get(className: String): LazyType
    fun get(clazz: Class<*>): LazyType

    fun get(clazz: KClass<*>): LazyType {
        return this.get(clazz.javaObjectType)
    }

    fun getPrimitiveType(enum: PrimitiveTypeEnum): PrimitiveType
    fun getWildcardType(
        extendsBound: LazyType?,
        superBound: LazyType?,
    ): LazyType

    /**
     * 获取参数化类型
     * @param target LazyType 包含范型参数的类型
     * @param arguments Array<out LazyType> 范型参数类型
     */
    fun getParameterizedType(target: LazyType, vararg typeArgs: LazyType): LazyType

    fun boxed(type: LazyType): LazyType
    fun erasure(type: LazyType): LazyType

    fun isSameType(t1: LazyType, t2: LazyType): Boolean

    fun isAssignable(t1: LazyType, t2: LazyType): Boolean

    fun catchHandlerException(position: Any?, block: () -> Unit): Exception?

    fun <T : Annotation> getMirroredType(annotation: T, block: T.() -> Unit): LazyType
    fun createMethodSpecBuilder(method: LazyMethod): MethodSpec.Builder
    fun createTypeSpecBuilder(type: LazyType): TypeSpec.Builder
}


lateinit var ANY_TYPE: LazyType

interface LAnnotated {
    val delegate: Any

    val annotationNames: List<String>

    fun <T : Annotation> getAnnotation(clazz: KClass<T>): T?
    fun <T : Annotation> getAnnotations(clazz: KClass<T>): List<T>
}


/**
 * apt/ksp 类型转换层
 *
 * 当通过[typeService]获取一个类的[LazyType]实例时，该实例的类型必须为[LazyType]继承树中允许的最深层的类型
 *
 * 比如当类是一个自定义的java bean且该类上有标注[io.github.afezeria.freedao.annotation.Table]时，
 * 该实例的类型必须为[EntityType]
 *
 * @property packageName String
 * @property simpleName String
 * @property qualifiedName String
 * @property declaredFields MutableList<LazyVariable>
 * @property declaredMethods MutableList<LazyMethod>
 * @property constructors MutableList<LazyMethod>
 * @property allFields MutableList<out LazyVariable>
 * @property allMethods MutableList<LazyMethod>
 * @property isAbstract Boolean
 */
interface LazyType : LAnnotated {
    val packageName: String
    val simpleName: String
    val qualifiedName: String

    val superClass: LazyType
    val interfaces: List<LazyType>
    val typeParameters: List<TypeParameter>

    val declaredFields: List<LazyVariable>
    val declaredMethods: List<LazyMethod>
    val constructors: MutableList<LazyMethod>
    val modifiers: List<Modifier>

    /**
     * 类及其父类的所有字段，该列表所有字段的类型都是已参数化的
     */
    val allFields: List<LazyVariable>


    /**
     * 继承树上的所有方法，不包含被重载的方法
     */
    val allMethods: List<LazyMethod>

    val isAbstract: Boolean


    val isTopLevelType: Boolean
}

open class TypeParameter(
    val parameterName: String,
    val type: LazyType = ANY_TYPE,
) : LazyType by type

class TypePlaceholder(val placeholderName: String, parameterName: String) : TypeParameter(parameterName)
class TypeArgument(
    parameterName: String,
    val argumentType: LazyType,
) : TypeParameter(parameterName, argumentType)


interface AnnotationType : LazyType

enum class Modifier {
    /**
     * The modifier `public`
     */
    PUBLIC,

    /**
     *  The modifier `protected`
     */
    PROTECTED,

    /**
     *  The modifier `private`
     */
    PRIVATE,

    /**
     *  The modifier `abstract`
     */
    ABSTRACT,

    /**
     * The modifier `default`
     * @since 1.8
     */
    DEFAULT,

    /**
     *  The modifier `static`
     */
    STATIC,

    /**
     * The modifier `sealed`
     * @since 17
     */
    SEALED,

    /**
     * The modifier `non-sealed`
     * @since 17
     */
    NON_SEALED,

    /**
     *  The modifier `final`
     */
    FINAL,

    /**
     *  The modifier `transient`
     */
    TRANSIENT,

    /**
     *  The modifier `volatile`
     */
    VOLATILE,

    /**
     *  The modifier `synchronized`
     */
    SYNCHRONIZED,

    /**
     *  The modifier `native`
     */
    NATIVE,

    /**
     *  The modifier `strictfp`
     */
    STRICTFP,

}

interface LazyMethod : LAnnotated {
    fun buildMethodSpec(): MethodSpec.Builder

    val typeParameters: List<LazyType>
    val returnType: LazyType

    /**
     * 全限定名，格式：packageName.className.methodName
     */
    val qualifiedName: String

    /**
     * 方法名称
     */
    val simpleName: String
    val owner: LazyType
    val modifiers: List<Modifier>
    val parameters: List<LazyParameter>
}

interface LazyVariable : LAnnotated {
    /**
     * 变量声明所在的类型
     */
    val owner: LazyType
    val simpleName: String
    val type: LazyType
    val modifier: List<Modifier>
}

interface LazyParameter : LazyVariable {
    val index: Int
}

class VirtualLazyParameterImpl(
    override val simpleName: String,
    override val type: LazyType,
    override val index: Int,
) : LazyParameter {
    override val owner: LazyType = ErrorLazyType
    override val modifier: List<Modifier> = emptyList()
    override val delegate: Any = Any()
    override val annotationNames: List<String> = emptyList()

    override fun <T : Annotation> getAnnotation(clazz: KClass<T>): T? {
        return null
    }

    override fun <T : Annotation> getAnnotations(clazz: KClass<T>): List<T> {
        return emptyList()
    }

}

class NoType : LazyType {
    override val packageName: String
        get() = TODO("Not yet implemented")
    override val simpleName: String
        get() = TODO("Not yet implemented")
    override val qualifiedName: String
        get() = TODO("Not yet implemented")
    override val superClass: LazyType
        get() = TODO("Not yet implemented")
    override val interfaces: List<LazyType>
        get() = TODO("Not yet implemented")
    override val typeParameters: List<TypeArgument>
        get() = TODO("Not yet implemented")
    override val declaredFields: List<LazyVariable>
        get() = TODO("Not yet implemented")
    override val declaredMethods: List<LazyMethod>
        get() = TODO("Not yet implemented")
    override val constructors: MutableList<LazyMethod>
        get() = TODO("Not yet implemented")
    override val modifiers: List<Modifier>
        get() = TODO("Not yet implemented")
    override val allFields: List<LazyVariable>
        get() = TODO("Not yet implemented")
    override val allMethods: MutableList<LazyMethod>
        get() = TODO("Not yet implemented")
    override val isAbstract: Boolean
        get() = TODO("Not yet implemented")
    override val isTopLevelType: Boolean
        get() = TODO("Not yet implemented")
    override val delegate: Any
        get() = TODO("Not yet implemented")
    override val annotationNames: List<String>
        get() = TODO("Not yet implemented")

    override fun <T : Annotation> getAnnotation(clazz: KClass<T>): T? {
        TODO("Not yet implemented")
    }


    override fun <T : Annotation> getAnnotations(clazz: KClass<T>): List<T> {
        TODO("Not yet implemented")
    }

}

enum class PrimitiveTypeEnum {
    BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE,
}

sealed interface PrimitiveType : LazyType {
    val typeEnumValue: PrimitiveTypeEnum
    override val annotationNames: List<String>
        get() = TODO("Not yet implemented")

    override fun <T : Annotation> getAnnotation(clazz: KClass<T>): T? {
        TODO("Not yet implemented")
    }

    override fun <T : Annotation> getAnnotations(clazz: KClass<T>): List<T> {
        TODO("Not yet implemented")
    }

    override val packageName: String
        get() = TODO("Not yet implemented")
    override val simpleName: String
        get() = TODO("Not yet implemented")
    override val qualifiedName: String
        get() = TODO("Not yet implemented")
    override val declaredFields: List<LazyVariable>
        get() = TODO("Not yet implemented")
    override val declaredMethods: List<LazyMethod>
        get() = TODO("Not yet implemented")
    override val constructors: MutableList<LazyMethod>
        get() = TODO("Not yet implemented")
    override val allFields: MutableList<out LazyVariable>
        get() = TODO("Not yet implemented")
    override val allMethods: MutableList<LazyMethod>
        get() = TODO("Not yet implemented")
    override val isAbstract: Boolean
        get() = TODO("Not yet implemented")
}

interface CollectionType : LazyType {
    val elementType: LazyType
}

interface MapType : LazyType {
    val keyType: LazyType
    val valueType: LazyType
}

interface ColumnAnnotation {
    val columnName: String
        get() = ""
    val exist: Boolean
        get() = true
    val supportInsert: Boolean
        get() = true
    val supportUpdate: Boolean
        get() = true
    val resultTypeHandle: LazyType?
        get() = typeService.get(ResultTypeHandler::class)
    val parameterTypeHandle: LazyType?
        get() = typeService.get(ParameterTypeHandler::class)
}


open class BeanType(type: LazyType) : LazyType by type {
    private val allFieldsBack: List<BeanProperty> by lazy {
        type.allFields.map { BeanProperty(it) }
    }
    override val allFields: List<BeanProperty>
        get() = allFieldsBack

    /**
     * 获取定义在类上的映射规则
     *
     * 映射规则包括参数数量最少的构造器的参数以及排除掉和这些参数同名的字段后的所有存在 setter方法的字段
     */
    val defaultMappings: List<MappingData> by lazy {
        val mappings = mutableListOf<MappingData>()
        //映射结果为java bean
        //处理构造器参数映射
        val constructor =
            this.constructors.filter { Modifier.PUBLIC in it.modifiers }
                .minByOrNull { it.parameters.size }
                ?: throw HandlerException("Return type $qualifiedName must have a public constructor")

        //处理构造器参数映射
        constructor.parameters.forEachIndexed { index, param ->
            allFields.find {
                it.exist && it.simpleName == param.simpleName && it.type.boxed().isSameType(param.type.boxed())
            }?.apply {
                mappings += MappingData(
                    source = columnName,
                    target = simpleName,
                    typeHandlerLA = resultTypeHandle,
                    targetTypeLA = type.boxed(),
                    constructorParameterIndex = index,
                )
            }
                ?: throw HandlerException("Constructor parameter name must be the same as field name:${qualifiedName}.${param.simpleName}")
        }

        //处理属性映射，排除在构造器参数中存在的字段
        allFields.filter { it.hasSetter() && it.exist && mappings.none { m -> m.target == it.simpleName } }
            .forEach {
                mappings += MappingData(
                    source = it.columnName,
                    target = it.simpleName,
                    typeHandlerLA = it.resultTypeHandle,
                    targetTypeLA = it.type.boxed(),
                    constructorParameterIndex = -1
                )
            }
        mappings
    }

}

class EntityType(type: LazyType) : BeanType(type) {
    val table: String
    var schema: String = ""
    var primaryKey: List<BeanProperty>

    init {
        getAnnotation(Table::class)!!.let {
            table = it.name.ifBlank {
                it.value.ifBlank {
                    type.simpleName.toSnakeCase()
                }
            }
            schema = it.schema
            primaryKey = allFields.filter { f ->
                f.columnName in it.primaryKeys

            }
        }
//        checkJoinAnnotation()
    }

    val dbFullyQualifiedName: String by lazy {
        if (schema.isBlank()) {
            table.sqlQuote()
        } else {
            schema.sqlQuote() + "." + table.sqlQuote()
        }
    }

}

class BeanProperty(variable: LazyVariable) : LazyVariable by variable, ColumnAnnotation {
    fun templateParameterStr(parameterName: String): String {
        return "#{$parameterName.$simpleName${parameterTypeHandle?.let { ",typeHandler=${it}" } ?: ""}}"
    }
}

object ErrorLazyType : LazyType {
    override val packageName: String
        get() = TODO("Not yet implemented")
    override val simpleName: String
        get() = TODO("Not yet implemented")
    override val qualifiedName: String
        get() = TODO("Not yet implemented")
    override val superClass: LazyType
        get() = TODO("Not yet implemented")
    override val interfaces: List<LazyType>
        get() = TODO("Not yet implemented")
    override val typeParameters: List<TypeArgument>
        get() = TODO("Not yet implemented")
    override val declaredFields: List<LazyVariable>
        get() = TODO("Not yet implemented")
    override val declaredMethods: List<LazyMethod>
        get() = TODO("Not yet implemented")
    override val constructors: MutableList<LazyMethod>
        get() = TODO("Not yet implemented")
    override val modifiers: List<Modifier>
        get() = TODO("Not yet implemented")
    override val allFields: List<LazyVariable>
        get() = TODO("Not yet implemented")
    override val allMethods: List<LazyMethod>
        get() = TODO("Not yet implemented")
    override val isAbstract: Boolean
        get() = TODO("Not yet implemented")
    override val isTopLevelType: Boolean
        get() = TODO("Not yet implemented")
    override val delegate: Any
        get() = TODO("Not yet implemented")
    override val annotationNames: List<String>
        get() = TODO("Not yet implemented")

    override fun <T : Annotation> getAnnotation(clazz: KClass<T>): T? {
        TODO("Not yet implemented")
    }

    override fun <T : Annotation> getAnnotations(clazz: KClass<T>): List<T> {
        TODO("Not yet implemented")
    }

}