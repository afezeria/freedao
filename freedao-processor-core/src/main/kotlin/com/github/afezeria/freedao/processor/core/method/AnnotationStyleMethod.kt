package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.annotation.Delete
import com.github.afezeria.freedao.annotation.Insert
import com.github.afezeria.freedao.annotation.Select
import com.github.afezeria.freedao.annotation.Update
import com.github.afezeria.freedao.processor.core.DaoHandler
import javax.lang.model.element.ExecutableElement

class AnnotationStyleMethod private constructor(
    element: ExecutableElement, daoHandler: DaoHandler,
) : MethodHandler(element, daoHandler) {

    override fun getTemplate(): String {
        TODO("Not yet implemented")
    }

    companion object {
        private val annotations = listOf(Insert::class.java, Select::class.java, Update::class.java, Delete::class.java)

        operator fun invoke(element: ExecutableElement, daoHandler: DaoHandler): AnnotationStyleMethod? {
            return annotations.find { element.getAnnotation(it) != null }
                ?.let { AnnotationStyleMethod(element, daoHandler) }
        }
    }
}