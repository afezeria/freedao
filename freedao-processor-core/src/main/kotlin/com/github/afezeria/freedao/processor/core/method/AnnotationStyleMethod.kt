package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.annotation.Delete
import com.github.afezeria.freedao.annotation.Insert
import com.github.afezeria.freedao.annotation.Select
import com.github.afezeria.freedao.annotation.Update
import com.github.afezeria.freedao.processor.core.DaoModel
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class AnnotationStyleMethod(
    element: ExecutableElement, daoModel: DaoModel,
) : MethodModel(element, daoModel) {

    override fun getTemplate(): String {
        TODO("Not yet implemented")
    }

    companion object {
        fun match(element: Element): Boolean {
            return element.run {
                getAnnotation(Insert::class.java) != null
                        || getAnnotation(Select::class.java) != null
                        || getAnnotation(Update::class.java) != null
                        || getAnnotation(Delete::class.java) != null
            }
        }
    }
}