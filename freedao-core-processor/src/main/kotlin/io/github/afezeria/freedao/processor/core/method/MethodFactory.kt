package io.github.afezeria.freedao.processor.core.method

import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.processor.LazyMethod
import java.util.*

/**
 *
 * @author afezeria
 */
interface MethodFactory {
    fun order(): Int
    fun build(daoDefinition: DaoHandler, lazyMethod: LazyMethod): MethodDefinition?
}

interface WrapperMethodFactory {
    fun order(): Int
    fun build(
        daoDefinition: DaoHandler,
        lazyMethod: LazyMethod,
        wrapper: MethodDefinitionWrapper?
    ): MethodDefinitionWrapper?
}

abstract class MethodDefinitionWrapper : MethodDefinition {
    var delegate: MethodDefinition? = null
    fun init(method: MethodDefinition) {
        if (delegate == null) {
            delegate = method
        } else {
            val tmp = delegate
            require(tmp is MethodDefinitionWrapper) { "Repeat initialization" }
            tmp.init(method)
        }
        if (method !is WrapperMethodFactory) {
            check()
        }
    }

    abstract fun check()
}

interface MethodImplementFactory {
    fun build(methodDefinition: MethodDefinition): MethodDefinition
}


interface MethodDefinition {
    fun render()

    companion object {
        private val classLoader = MethodDefinition::class.java.classLoader

        private val methodFactories by lazy {
            ServiceLoader.load(
                MethodFactory::class.java,
                classLoader
            ).sortedBy { it.order() }.toMutableList()
        }
        private val wrapperMethodFactories by lazy {
            ServiceLoader.load(
                WrapperMethodFactory::class.java,
                classLoader
            ).sortedBy { it.order() }
        }
        private val methodImplementFactory by lazy {
            ServiceLoader.load(
                MethodImplementFactory::class.java,
                classLoader
            ).toList()
                .takeIf { it.size == 1 }
                ?.get(0)
                ?: throw HandlerException("Multiple MethodDefinitionWrapper implementations have been found. Please ensure that there is only one implementation class on the classpath")
        }


        fun build(daoDefinition: DaoHandler, lazyMethod: LazyMethod): MethodDefinition {
            val methodDefinition =
                methodFactories.firstNotNullOfOrNull {
                    it.build(daoDefinition, lazyMethod)
                } ?: wrapperMethodFactories.fold<_, MethodDefinitionWrapper?>(null) { wrapper, it ->
                    it.build(daoDefinition, lazyMethod, wrapper)
                }?.also { wrapper ->
                    methodFactories.firstNotNullOfOrNull { it.build(daoDefinition, lazyMethod) }
                        ?.let {
                            wrapper.init(it)
                        }
                } ?: throw HandlerException("Invalid method declare")
            return methodImplementFactory.build(methodDefinition)
        }
    }
}
