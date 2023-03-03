package io.github.afezeria.freedao.processor.core.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import io.github.afezeria.freedao.ParameterTypeHandler
import io.github.afezeria.freedao.ResultTypeHandler
import io.github.afezeria.freedao.processor.core.HandlerException
import kotlin.reflect.KClass

/**
 *
 * @author afezeria
 */

fun <T : Annotation> T.wrapperType(block: T.() -> Unit): LazyType {
    return typeService.getMirroredType(this, block)
}

/**
 * 检查类型是否是结果类型处理器和其是否匹配[type]
 * @receiver LazyType
 * @param type LazyType
 * @param errorMessage String
 * @return LazyType?
 */
fun LazyType.throwIfNotResultTypeHandlerOrNotMatchType(
    type: LazyType,
    errorMessage: LazyType.() -> String = { "$this does not match ${type.qualifiedName}" },
//    typeNotMatchMsg: TypeMirror.() -> String = { "$this does not match $type type" },
): LazyType? {
    if (this.isAbstract) {
        throw HandlerException("ResultTypeHandler must be Class")
    }
    if (this.isSameType(ResultTypeHandler::class)) {
        return null
    }
    val method = declaredMethods.find {
        it.simpleName == "handleResult"
                && it.parameters.size == 2
                && it.parameters[0].type.isSameType(Any::class)
                //TODO 将 ResultTypeHandler 改成接口，实现类继承该接口，实现类必须有无参构造器，在每个用到该类的DAO中创建一个静态的实例去实现调用
//                && it.parameters[1].type.isSameType()
                && Modifier.PUBLIC in it.modifiers
                && Modifier.STATIC in it.modifiers
    }
        ?: throw HandlerException("Invalid ResultTypeHandler:${this}, missing method:public static Object handleResult(Object,Class)")
    if (!method.returnType.isAssignable(type)) {
        throw HandlerException(errorMessage())
    }
    return this
}

/**
 * 检查类型是否是参数类型处理器的子类并且处理方法能够接受 [expectMatchType] 类型的参数
 * @receiver LazyType 参数处理器类型
 * @param expectMatchType LazyType 期望传递给处理器的变量类型
 * @param errorMessage [@kotlin.ExtensionFunctionType] Function1<LazyType, String> 错误消息
 * @return LazyType? 当@receiver类型是[ParameterTypeHandler]时返回null
 */
fun LazyType.throwIfNotParameterTypeHandlerOrNotMatchType(
    expectMatchType: LazyType,
    errorMessage: LazyType.() -> String = { "$this does not match ${expectMatchType.qualifiedName} type" },
): Pair<LazyType, LazyType>? {
    if (this.isAbstract) {
        throw HandlerException("ResultTypeHandler must be Class")
    }
    if (this.isSameType(ParameterTypeHandler::class)) {
        return null
    }
    val method = declaredMethods.find {
        it.simpleName == "handleParameter"
                && it.parameters.size == 1
                && it.parameters[0].type.isSameType(Any::class)
                //TODO 将 ParameterTypeHandler 改成接口，实现类继承该接口，实现类必须有无参构造器，在每个用到该类的DAO中创建一个静态的实例去实现调用
//                && it.parameters[1].type.isSameType()
                && Modifier.PUBLIC in it.modifiers
                && Modifier.STATIC in it.modifiers
    }
        ?: throw HandlerException("Invalid ParameterTypeHandler:${this}, missing method:public static Object handleParameter")
    // expectMatchType不是Any类型且无法分配给类的handlerParameter方法时抛出异常
    // expectMatchType是Any类型的场合代码生成时会尝试将其强转成参数处理器的参数类型
    if (!expectMatchType.isSameType(Any::class) && !expectMatchType.isAssignable(method.parameters[0].type)) {
        throw HandlerException(errorMessage())
    }
    return this to method.parameters[0].type
}

val KClass<*>.typeLA: LazyType
    get() = typeService.get(this)
val Class<*>.typeLA: LazyType
    get() = typeService.get(this)

val LazyType.className: ClassName
    get() = ClassName.get(packageName, simpleName)
val LazyType.typeName: TypeName
    get() {
        TODO()
    }

/**
 * 获取当前类实现的父类/接口[parentType]的类型变量[parameterName]对应的实参的类型
 * @receiver LazyType 当前类型
 * @param parentType LazyType 当前类型实现或继承的类型
 * @param parameterName String 类型参数名称
 * @return LazyType [parentType] 参数化后 [parameterName] 对应的实际参数
 */
fun LazyType.findTypeArgument(parentType: LazyType, parameterName: String): LazyType {
    if (this.isAssignable(parentType)) {
        //逻辑没问题的话应该不会执行到这里
        throw IllegalArgumentException("this(${this.className}) is not a subclass of parentType(${parentType.className})")
    }
    val map = typeParameters.associate { it.parameterName to (it as TypeArgument).argumentType }
    return findTypeArgumentHelper(this, parentType, parameterName, map)!!
}


private fun findTypeArgumentHelper(
    self: LazyType,
    parentType: LazyType,
    parameterName: String,
    typeArgumentMap: Map<String, LazyType> = mutableMapOf(),
): LazyType? {
    if (self.isSameType(parentType)) {
        return typeArgumentMap[parameterName] ?: throw IllegalStateException()
    }
    if (!self.isAssignable(parentType)) {
        return null
    }
    for (type in listOfNotNull(self.superClass, *self.interfaces.toTypedArray())) {
        val map = mutableMapOf<String, LazyType>()
        type.typeParameters.forEachIndexed { index, arg ->
            when (arg) {
                is TypeArgument -> {
                    map[arg.parameterName] = arg.argumentType
                }

                is TypePlaceholder -> {
                    map[arg.parameterName] = typeArgumentMap[arg.placeholderName]!!
                }

                else -> {
                    throw IllegalStateException()
                }
            }
        }
        val result = findTypeArgumentHelper(type, parentType, parameterName, map)
        if (result != null) {
            return result
        }
    }
    throw IllegalStateException()
}


fun KClass<*>.typeLA(vararg typeArgs: LazyType): LazyType {
    return if (typeArgs.isEmpty()) {
        typeService.get(this)
    } else {
        typeService.getParameterizedType(typeService.get(this), *typeArgs)
    }
}

fun LazyType.boxed(): LazyType = typeService.boxed(this)
fun LazyType.erasure(): LazyType = typeService.erasure(this)

fun LazyType.isSameType(type: LazyType): Boolean = typeService.isSameType(this, type)
fun LazyType.isSameType(clazz: KClass<*>): Boolean = typeService.isSameType(this, clazz.typeLA)
fun LazyType.isSameType(clazz: Class<*>): Boolean = typeService.isSameType(this, clazz.typeLA)

fun LazyType.isAssignable(type: LazyType): Boolean = typeService.isAssignable(this, type)
fun LazyType.isAssignable(clazz: Class<*>): Boolean = typeService.isAssignable(this, clazz.typeLA)

fun LazyType.isAssignable(clazz: KClass<*>): Boolean = typeService.isAssignable(this, clazz.typeLA)


val KClass<*>.LazyType: LazyType
    get() {
        return typeService.get(this)
    }

fun LazyType.isBeanType(): Boolean {
    if (qualifiedName.startsWith("java")
        || isAssignable(Collection::class)
        || isAssignable(Map::class)
        || allFields.none { it.hasGetter() }
    ) {
        return false
    }
    return true
}

fun LazyType.asBeanType(): BeanType? {
    if (qualifiedName.startsWith("java")) {
        return null
    }
    if (isAssignable(Collection::class)) {
        return null
    }
    if (isAssignable(Map::class)) {
        return null
    }
    if (allFields.any { it.hasGetter() }) {
        return BeanType(this)
    }
    return null
}

val LazyVariable.getterName: String
    get() = "get" + toString().replaceFirstChar { it.uppercaseChar() }

val LazyVariable.setterName: String
    get() = "set" + toString().replaceFirstChar { it.uppercaseChar() }

fun LazyVariable.hasSetter(): Boolean {
    return owner.declaredMethods.any {
        it.simpleName == setterName
                && it.parameters.size == 1
                && it.parameters[0].type.isSameType(type)
                && Modifier.PUBLIC in it.modifiers
                && Modifier.STATIC !in it.modifiers
    }
}

fun LazyVariable.hasGetter(): Boolean {
    return owner.declaredMethods.any {
        it.simpleName.contentEquals(getterName)
                && it.parameters.isEmpty()
                && it.returnType.isSameType(type)
                && Modifier.PUBLIC in it.modifiers
                && Modifier.STATIC !in it.modifiers
    }
}

fun <T : Annotation> T.mirroredTypeLA(block: T.() -> Unit): LazyType {
    return typeService.getMirroredType(this, block)
}
