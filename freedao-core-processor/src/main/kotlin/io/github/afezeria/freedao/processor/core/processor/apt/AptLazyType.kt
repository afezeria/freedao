package io.github.afezeria.freedao.processor.core.processor.apt

import io.github.afezeria.freedao.processor.core.processor.*
import io.github.afezeria.freedao.processor.core.processor.Modifier
import java.util.concurrent.ConcurrentHashMap
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType

/**
 *
 * @author afezeria
 */
class AptLazyType private constructor(
    val declaredType: DeclaredType,
) : AptAnnotated(safe { declaredType.asElement() }), LazyType {
    override val element: TypeElement = super.element as TypeElement

    override val delegate: DeclaredType = declaredType

    override val isTopLevelType: Boolean by lazy {
        element.enclosingElement != null && element.enclosingElement is PackageElement
    }

    override val simpleName: String = element.simpleName.toString()

    override val id: String = declaredType.toString()

    override val packageName: String by lazy {
        var enclosingElement: Element? = safe { element.enclosingElement }
        var limit = 100
        while (limit-- > 0) {
            if (enclosingElement == null) {
                return@lazy ""
            }
            if (enclosingElement is PackageElement) {
                return@lazy sync { enclosingElement.toString() }
            }
            enclosingElement = safe { enclosingElement!!.enclosingElement }
        }
        throw RuntimeException()
    }
    override val qualifiedName: String = sync { element.toString() }

    override val superClass: LazyType by lazy(lock) {
        valueOf(element.superclass as DeclaredType)
    }
    override val interfaces: List<LazyType> by lazy {
        sync {
            element.interfaces
        }.map {
            valueOf(it as DeclaredType)
        }
    }

    override val typeParameters: List<TypeArgument> by lazy {
        element.typeParameters
        TODO("Not yet implemented")
    }

    override val declaredFields: List<LazyVariable> by lazy {
        element.enclosedElements.filter { it.kind == ElementKind.FIELD }
            .mapTo(mutableListOf()) { AptLazyVariable(it as VariableElement, this) }
    }

    override val declaredMethods: List<LazyMethod> by lazy {
        element.enclosedElements.filter { it.kind == ElementKind.METHOD }
            .mapTo(mutableListOf()) { AptLazyMethod(it as ExecutableElement, this) }
    }

    override val constructors: MutableList<LazyMethod> by lazy {
        element.enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR }
            .mapTo(mutableListOf()) { AptLazyMethod(it as ExecutableElement, this) }
    }
    override val allFields: List<LazyVariable> by lazy {
        doWalkTree(this) { it.declaredFields }
    }
    override val allMethods: List<LazyMethod> by lazy {
        doWalkTree(this) { it.declaredMethods }
    }

    override val modifiers: List<Modifier> by lazy {
        element.modifiers.map { AptHelper.modifierConvert(it) }
    }
    override val isAbstract: Boolean = element.modifiers.contains(javax.lang.model.element.Modifier.ABSTRACT)

    init {
        println()
        typeCache[declaredType.toString()] = this
    }

    private fun <T> doWalkTree(
        current: LazyType,
        targetAccessFn: (LazyType) -> List<T>,
    ): List<T> {
        return mutableListOf(
            current.superClass.let { doWalkTree(it, targetAccessFn) },
            *current.interfaces.map { doWalkTree(it, targetAccessFn) }.toTypedArray(),
            targetAccessFn(current)
        ).filterNotNull().flatten()
    }


    companion object {
        val typeCache = ConcurrentHashMap<String, AptLazyType>()
        fun valueOf(declaredType: DeclaredType): AptLazyType {
            val id = declaredType.toString()
            var type = typeCache[id]
            if (type == null) {
                sync {
                    type = typeCache[id]
                    if (type == null) {
                        type = AptLazyType(declaredType)
                        typeCache[id] = type!!
                    }
                }
            }
            return type!!
        }
    }

}