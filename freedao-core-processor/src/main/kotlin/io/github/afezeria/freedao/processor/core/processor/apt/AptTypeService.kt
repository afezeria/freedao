package io.github.afezeria.freedao.processor.core.processor.apt

import io.github.afezeria.freedao.processor.core.processor.LazyType
import io.github.afezeria.freedao.processor.core.processor.TypeService
import kotlin.reflect.KClass

class AptTypeService : TypeService {
    override fun get(string: String): LazyType {
        TODO("Not yet implemented")
    }

    override fun get(clazz: KClass<*>): LazyType {
        TODO("Not yet implemented")
    }

    override fun boxed(type: LazyType): LazyType {
        TODO("Not yet implemented")
    }

    override fun erasure(type: LazyType): LazyType {
        TODO("Not yet implemented")
    }

    override fun isSameType(t1: LazyType, t2: LazyType): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSameType(type: LazyType, clazz: KClass<*>): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAssignable(t1: LazyType, t2: LazyType): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAssignable(type: LazyType, clazz: KClass<*>): Boolean {
        TODO("Not yet implemented")
    }

    override fun catchHandlerException(position: Any?, block: () -> Unit): Exception? {
        TODO("Not yet implemented")
    }

}
