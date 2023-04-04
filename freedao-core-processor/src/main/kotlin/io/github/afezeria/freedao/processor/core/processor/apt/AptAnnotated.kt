package io.github.afezeria.freedao.processor.core.processor.apt

import io.github.afezeria.freedao.processor.core.processor.LAnnotated
import java.util.concurrent.ConcurrentHashMap
import javax.lang.model.element.Element
import kotlin.reflect.KClass

/**
 *
 * @author afezeria
 */
abstract class AptAnnotated(open val element: Element) : LAnnotated {
    private val annotationCache: ConcurrentHashMap<KClass<*>, Any?> = ConcurrentHashMap()

    override val annotationNames: List<String> by lazy {
        element.annotationMirrors.map { it.annotationType.toString() }
    }

    override fun <T : Annotation> getAnnotation(clazz: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return annotationCache.getOrPut(clazz) {
            element.getAnnotation(clazz.javaObjectType)
        } as T?
    }

    override fun <T : Annotation> getAnnotations(clazz: KClass<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return annotationCache.getOrPut(clazz) {
            element.getAnnotationsByType(clazz.javaObjectType).toList()
        } as List<T>
    }
}