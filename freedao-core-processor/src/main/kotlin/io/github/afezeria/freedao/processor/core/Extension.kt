package io.github.afezeria.freedao.processor.core

import com.squareup.javapoet.TypeName
import io.github.afezeria.freedao.ParameterTypeHandler
import io.github.afezeria.freedao.ResultTypeHandler
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

/**
 *
 */
lateinit var processingEnvironment: ProcessingEnvironment
val elementUtils: Elements
    get() = processingEnvironment.elementUtils
val typeUtils: Types
    get() = processingEnvironment.typeUtils


val TypeMirror.typeName: TypeName
    get() = TypeName.get(this)

fun TypeMirror.isSameType(other: TypeMirror) = typeUtils.isSameType(this, other)
fun TypeMirror.isAssignable(other: TypeMirror) = typeUtils.isAssignable(this, other)

fun TypeMirror.isSameType(other: KClass<*>) = typeUtils.isSameType(this, other.type)
fun TypeMirror.isAssignable(other: KClass<*>) = typeUtils.isAssignable(this, other.type)

fun TypeMirror.erasure(): TypeMirror = typeUtils.erasure(this)

fun TypeMirror.boxed(): TypeMirror = if (this is PrimitiveType) {
    typeUtils.boxedClass(this).asType()
} else {
    this
}

/**
 * 查找bean属性
 * @receiver DeclaredType
 * @param name String 属性名称
 */
fun DeclaredType.getBeanPropertyType(
    name: String,
    errorMsg: () -> String,
): TypeMirror {
    fun findPropertyAndOwnerElement(type: DeclaredType, propName: String): Pair<TypeMirror, DeclaredType>? {
        val element = type.asElement() as TypeElement
        val propType = element.enclosedElements
            .find { it.simpleName.toString() == name && it.hasGetter() }
            ?.asType()
        return if (propType == null) {
            element.superclass?.takeIf { it !is NoType }
                ?.let { findPropertyAndOwnerElement(it as DeclaredType, propName) }
        } else {
            propType to type
        }
    }
    val (propType, propOwnerType) = findPropertyAndOwnerElement(this, name)
        ?: throw HandlerException(errorMsg())
    return propType.parameterized(this, propOwnerType)
}

val DeclaredType.simpleName: String
    get() {
        return asElement().simpleName.toString()
    }

fun <T : Annotation> T.mirroredType(block: T.() -> Unit): TypeMirror {
    try {
        block()
        //unreachable
        throw IllegalStateException()
    } catch (e: MirroredTypeException) {
        return e.typeMirror
    }
}

val VariableElement.getterName: String
    get() = "get" + toString().replaceFirstChar { it.uppercaseChar() }

val VariableElement.setterName: String
    get() = "set" + toString().replaceFirstChar { it.uppercaseChar() }

fun Element.hasSetter(): Boolean {
    return when (this) {
        is VariableElement -> {
            enclosingElement.enclosedElements.any {
                it is ExecutableElement
                        && it.simpleName.contentEquals(setterName)
                        && it.parameters.size == 1
                        && it.parameters[0].asType().isSameType(asType())
                        && it.modifiers.contains(Modifier.PUBLIC)
                        && !it.modifiers.contains(Modifier.STATIC)
            }
        }
        else -> false
    }
}

fun Element.hasGetter(): Boolean {
    return when (this) {
        is VariableElement -> {
            enclosingElement.enclosedElements.any {
                it is ExecutableElement
                        && it.simpleName.contentEquals(getterName)
                        && it.parameters.size == 0
                        && it.returnType.isSameType(asType())
                        && it.modifiers.contains(Modifier.PUBLIC)
                        && !it.modifiers.contains(Modifier.STATIC)
            }
        }
        else -> false
    }
}

val KClass<*>.type: DeclaredType
    get() {
        return typeUtils.getDeclaredType(elementUtils.getTypeElement(this.javaObjectType.name))
    }

fun KClass<*>.type(vararg typeArgs: TypeMirror): DeclaredType {
    val element = elementUtils.getTypeElement(javaObjectType.name)
    assert(typeArgs.isEmpty() || (element.typeParameters.size == typeArgs.size))

    return typeUtils.getDeclaredType(
        element,
        *typeArgs.map { if (it is PrimitiveType) it.boxed() else it }.toTypedArray()
    )
}

fun String.sqlQuote(): String {
    return "${global.quote}${this}${global.quote}"
}

val groupingRegex = Regex("[a-z]+|[0-9]+|[A-Z][a-z]+|[A-Z]++(?![a-z])|[A-Z]")

fun String.toSnakeCase(): String {
    return groupingRegex.findAll(this).joinToString("_") { it.value.lowercase() }
}

val DeclaredType.isAbstract: Boolean
    get() = asElement().modifiers.contains(Modifier.ABSTRACT)

/**
 * 获取[this]在[superType]上的类型参数[parameterName]的实际类型
 *
 * @receiver DeclaredType 当前类型，一般是dao函数的返回值或参数的类型
 * @param superType TypeMirror?
 * @param parameterName String?
 * @return DeclaredType?
 * @throws HandlerException 如果[this]不是[superType]的子类
 */
fun DeclaredType.findTypeArgument(superType: TypeMirror, parameterName: String): DeclaredType {
    assert(this.isAssignable(superType))
//    if (!this.isAssignable(superType)) {
//        throw HandlerException("$this can not assignable to $superType")
//    }
    val typeParameters = (this.asElement() as TypeElement).typeParameters
    val map = if (typeArguments.size != typeParameters.size) {
        typeParameters.associate { parameterElement ->
            parameterElement.simpleName.toString() to Any::class.type
        }
    } else {
        typeParameters.mapIndexed { index, parameterElement ->
            val typeMirror = typeArguments[index]
            if (typeMirror !is DeclaredType) {
                throw HandlerException("The value of type parameter ${typeParameters[index].simpleName} of ${this.asElement()} should be an explicit type, current:$typeMirror")
            }
            parameterElement.simpleName.toString() to typeMirror
        }.toMap()
    }
    //因为不允许实体类和接口有自己的范型参数，所以这里是肯定能够找到的
    return findTypeArgumentHelper(superType, parameterName, map)!!
}

private fun DeclaredType.findTypeArgumentHelper(
    targetType: TypeMirror,
    parameterName: String,
    typeArgumentMap: Map<String, DeclaredType> = mutableMapOf(),
): DeclaredType? {
    if (targetType.isSameType(typeUtils.erasure(this))) {
        val typeParameters = (asElement() as TypeElement).typeParameters
        if (typeParameters.isEmpty()) {
            throw HandlerException("${(this.asElement() as TypeElement).qualifiedName} has no type parameter")
        }
        return typeArgumentMap[parameterName]
            ?: throw HandlerException("${(this.asElement() as TypeElement).qualifiedName} has no type parameter $parameterName")
    }
    if (!this.isAssignable(targetType)) {
        return null
    }
    val typeElement = this.asElement() as TypeElement
    for (typeMirror in listOf(typeElement.superclass, *typeElement.interfaces.toTypedArray())) {
        if (typeMirror.isAssignable(targetType)) {
            val parentClassTypeParameters = ((typeMirror as DeclaredType).asElement() as TypeElement).typeParameters
            val map = mutableMapOf<String, DeclaredType>()
            typeMirror.typeArguments.forEachIndexed { index, typeArg ->
                when (typeArg.kind) {
                    TypeKind.DECLARED -> {
                        map[parentClassTypeParameters[index].simpleName.toString()] = typeArg as DeclaredType
                    }
                    TypeKind.TYPEVAR -> {
                        map[parentClassTypeParameters[index].simpleName.toString()] =
                            requireNotNull(typeArgumentMap[typeArg.typeName.toString()]) {
                                "The actual type of the type parameter ${typeArg.typeName} of $typeMirror cannot be determined"
                            }
                    }
                    else -> {
                        throw IllegalStateException()
                    }
                }
            }
            val result = typeMirror.findTypeArgumentHelper(targetType, parameterName, map)
            if (result != null) {
                return result
            }
        }
    }
    throw IllegalStateException()
}

/**
 * 获取带类型参数的类型的实际类型
 *
 * 例：
 *
 *      interface A<T>{
 *        T a();
 *      }
 *      interface B<Long>{}
 *
 *      TypeArg("T").parameterized(Type(B::class),Type(A::class)) => Type(Long::class)
 *
 * @receiver TypeMirror 带类型参数的类型，一般为方法返回值的类型
 * @param type DeclaredType 一般为当前dao接口的类型
 * @param enclosingElementType DeclaredType 一般为方法所属的接口的类型
 * @return TypeMirror receiver在type中的实际类型
 */
fun TypeMirror.parameterized(type: DeclaredType, enclosingElementType: DeclaredType): TypeMirror {
    return when (this) {
        is TypeVariable -> {
            type.findTypeArgument(enclosingElementType, typeName.toString())
        }
        is DeclaredType -> {
            val map = typeArguments.map {
                when (it) {
                    is TypeVariable -> {
                        type.findTypeArgument(enclosingElementType, it.typeName.toString())
                    }
                    else -> {
                        it.parameterized(type, enclosingElementType)
                    }
                }
            }
            typeUtils.getDeclaredType(this.asElement() as TypeElement, *map.toTypedArray())
        }
        else -> {
            this
        }
    }
}


@OptIn(ExperimentalContracts::class)
fun TypeMirror.isCustomJavaBean(): Boolean {
    contract {
        returns(true) implies (this@isCustomJavaBean is DeclaredType)
    }
    if (this !is DeclaredType) {
        return false
    }

    if (toString().startsWith("java")) {
        return false
    }
    if (this.isAssignable(Collection::class)) {
        return false
    }
    if (this.isAssignable(Map::class)) {
        return false
    }
    val el = this.asElement() as TypeElement
    el.enclosedElements.forEach {
        if (it.kind == ElementKind.FIELD && it.hasGetter()) {
            return true
        }
    }
    return el.superclass.isCustomJavaBean()
}

fun <R> runCatchingHandlerExceptionOrThrow(element: Element, block: () -> R): R? {
    try {
        return block()
    } catch (e: Throwable) {
        if (e is HandlerException) {
            processingEnvironment.messager.printMessage(Diagnostic.Kind.ERROR, e.message, element)
            if (global.debug) {
                val stringWriter = StringWriter()
                val printWriter = PrintWriter(stringWriter)
                e.printStackTrace(printWriter)
                processingEnvironment.messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    stringWriter.toString()
                )
            }
        } else {
            throw e
        }
    }
    return null
}


/**
 * 检查 this 是否是一个合法的ResultTypeHandler，以及该handler是否能够匹配[type]类型的返回值
 * @receiver TypeMirror
 * @param type DeclaredType
 * @param typeNotMatchMsg Function0<String> 类型不匹配时抛出的异常的错误信息
 * @throws HandlerException 如果receiver不是一个合法的结果类型处理器
 * @return 当receiver的类型为[ResultTypeHandler]时返回null，如果类型是一个合法的结果类型处理器则以DeclaredType类型返回该对象
 */
@OptIn(ExperimentalContracts::class)
fun TypeMirror.isResultTypeHandlerAndMatchType(
    type: TypeMirror,
    typeNotMatchMsg: TypeMirror.() -> String = { "$this does not match $type type" },
): DeclaredType? {
    contract {
        returns() implies (this@isResultTypeHandlerAndMatchType is DeclaredType)
    }
    if (this !is DeclaredType || this.isAbstract) {
        throw HandlerException("ResultTypeHandler must be Class")
    }
    if (this.isSameType(io.github.afezeria.freedao.ResultTypeHandler::class)) {
        return null
    }
    val handlerElement = this.asElement().enclosedElements
        .find {
            it is ExecutableElement
                    && it.kind == ElementKind.METHOD
                    && it.simpleName.toString() == "handleResult"
                    && it.parameters.size == 2
                    && it.parameters[0].asType().isSameType(Any::class)
                    && it.parameters[1].asType()
                .isSameType(Class::class.type(typeUtils.getWildcardType(Any::class.type, null)))
                    && it.modifiers.containsAll(listOf(Modifier.PUBLIC, Modifier.STATIC))
        } as ExecutableElement?
        ?: throw HandlerException("Invalid ResultTypeHandler:${this}, missing method:public static Object handleResult(Object.class)")
    if (!handlerElement.returnType.isAssignable(type)) {
        throw HandlerException(typeNotMatchMsg())
    }
    return this
}

/**
 * 检查 this 是否是一个合法的ParameterTypeHandler，以及该[type]是否能够匹配handle方法的参数类型
 * @receiver TypeMirror
 * @param type DeclaredType
 * @param typeNotMatchMsg Function0<String> 类型不匹配时抛出的异常的错误信息
 * @throws HandlerException 如果receiver不是一个合法的参数类型处理器
 * @return receiver类型为[ParameterTypeHandler]时返回null，否则返回类型为DeclaredType的receiver对象和该对象的handle方法的第一个参数的类型
 */
@OptIn(ExperimentalContracts::class)
fun TypeMirror.isParameterTypeHandlerAndMatchType(
    type: TypeMirror,
    typeNotMatchMsg: TypeMirror.() -> String = { "$this does not match $type type" },
): Pair<DeclaredType, TypeMirror>? {
    contract {
        returns() implies (this@isParameterTypeHandlerAndMatchType is DeclaredType)
    }
    if (this !is DeclaredType || this.isAbstract) {
        throw HandlerException("ParameterTypeHandler must be Class")
    }
    if (this.isSameType(io.github.afezeria.freedao.ParameterTypeHandler::class)) {
        return null
    }
    val handlerElement = this.asElement().enclosedElements
        .find {
            it is ExecutableElement
                    && it.kind == ElementKind.METHOD
                    && it.simpleName.toString() == "handleParameter"
                    && it.parameters.size == 1
                    && it.modifiers.containsAll(listOf(Modifier.PUBLIC, Modifier.STATIC))
                    && it.returnType !is NoType
        } as ExecutableElement?
        ?: throw HandlerException("Invalid ParameterTypeHandler:${this}, missing method:public static Object handleParameter")
    if (!type.isSameType(Any::class)) {
        if (!type.isAssignable(handlerElement.parameters[0].asType())) {
            throw HandlerException(typeNotMatchMsg())
        }
    }
    return this to handlerElement.parameters[0].asType()
}