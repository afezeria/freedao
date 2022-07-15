package io.github.afezeria.freedao.processor.core.processor

import com.squareup.javapoet.MethodSpec
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

/**
 * @author afezeria
 */
lateinit var typeService: TypeService

interface TypeService {
    fun get(string: String): LazyType
    fun get(clazz: KClass<*>): LazyType

    fun boxed(type: LazyType): LazyType
    fun erasure(type: LazyType): LazyType

    fun isSameType(t1: LazyType, t2: LazyType): Boolean
    fun isAssignable(t1: LazyType, t2: LazyType): Boolean
    fun isSameType(type: LazyType, clazz: KClass<*>): Boolean
    fun isAssignable(type: LazyType, clazz: KClass<*>): Boolean


    fun catchHandlerException(position: Any?, block: () -> Unit): Exception?
}

fun LazyType.boxed(): LazyType = typeService.boxed(this)
fun LazyType.erasure(): LazyType = typeService.erasure(this)

fun LazyType.isSameType(type: LazyType): Boolean = typeService.isSameType(this, type)
fun LazyType.isAssignable(type: LazyType): Boolean = typeService.isAssignable(this, type)
fun LazyType.isSameType(clazz: KClass<*>): Boolean = typeService.isSameType(this, clazz)
fun LazyType.isAssignable(clazz: KClass<*>): Boolean = typeService.isAssignable(this, clazz)

fun LazyType.findTypeArgument(type: KClass<*>, parameterName: String): LazyType {
    TODO()
}

val KClass<*>.lazyType: LazyType
    get() {
        return typeService.get(this)
    }

lateinit var ANY_TYPE: LazyType

interface LAnnotated {
    val annotations: List<AnnotationInstance<*>>
    fun getAnnotation(string: String): AnnotationInstance<*>
    fun <T : Annotation> getAnnotation(clazz: KClass<T>): AnnotationInstance<T>
    fun getAnnotations(string: String): List<AnnotationInstance<*>>
    fun <T : Annotation> getAnnotations(clazz: KClass<T>): List<AnnotationInstance<T>>
}

interface AnnotationInstance<T : Annotation> {
    val type: AnnotationType
    fun valueName2Literal(): List<Pair<String, String>>
    fun <V> value(fn: T.() -> V): V
    fun <V> value(name: String): V
    fun mirrorType(fn: T.() -> KClass<*>): LazyType
    fun mirrorType(name: String): LazyType
}

interface LazyType : LAnnotated {
    val packageName: String
    val simpleName: String
    val simpleNames: Array<String>

    val declaredFields: MutableList<LazyVariable>
    val declaredMethods: MutableList<LazyMethod>

    val allFields: MutableList<LazyVariable>

    /**
     * 继承树上的所有方法，不包含被重载的方法
     */
    val allMethods: MutableList<LazyMethod>

    val isAbstract: Boolean


    fun isTopLevelType(): Boolean {
        return simpleNames.isEmpty()
    }
}

interface AnnotationType : LazyType

interface LazyMethod : LAnnotated {
    fun buildMethodSpec(): MethodSpec.Builder

    val typeParameters: List<LazyType>
    val returnType: LazyType
    val name: String
    val owner: LazyType
    val modifiers: List<Modifier>
    val isStatic: String
    val isDefault: String
    val parameters: List<LazyParameter>
}

interface LazyVariable : LAnnotated {
    val name: String
    val type: LazyType
    val modifier: List<Modifier>
}

interface LazyParameter : LazyVariable {
    val index: Int
}

class NoType(override val isAbstract: Boolean) : LazyType {
    override val packageName: String
        get() = TODO("Not yet implemented")
    override val simpleName: String
        get() = TODO("Not yet implemented")
    override val simpleNames: Array<String>
        get() = TODO("Not yet implemented")
    override val declaredFields: MutableList<LazyVariable>
        get() = TODO("Not yet implemented")
    override val declaredMethods: MutableList<LazyMethod>
        get() = TODO("Not yet implemented")
    override val allFields: MutableList<LazyVariable>
        get() = TODO("Not yet implemented")
    override val allMethods: MutableList<LazyMethod>
        get() = TODO("Not yet implemented")


    override val annotations: List<AnnotationInstance<*>>
        get() = TODO("Not yet implemented")

    override fun getAnnotation(string: String): AnnotationInstance<*> {
        TODO("Not yet implemented")
    }

    override fun <T : Annotation> getAnnotation(clazz: KClass<T>): AnnotationInstance<T> {
        TODO("Not yet implemented")
    }

    override fun getAnnotations(string: String): List<AnnotationInstance<*>> {
        TODO("Not yet implemented")
    }

    override fun <T : Annotation> getAnnotations(clazz: KClass<T>): List<AnnotationInstance<T>> {
        TODO("Not yet implemented")
    }
}

interface PrimitiveType : LazyType {

}

interface CollectionType : LazyType {
    val elementType: LazyType
}

interface MapType : LazyType {
    val keyType: LazyType
    val valueType: LazyType
}

interface BeanType : LazyType

interface EntityType : BeanType


