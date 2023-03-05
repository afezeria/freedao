package io.github.afezeria.freedao.processor.core.processor.apt

import io.github.afezeria.freedao.processor.core.processor.*
import io.github.afezeria.freedao.processor.core.processor.Modifier
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType

/**
 *
 * @author afezeria
 */
class AptLazyType(val declaredType: DeclaredType) : AptAnnotated(declaredType.asElement()), LazyType {
    override val element: TypeElement = declaredType.asElement() as TypeElement

    override val delegate: Any = declaredType

    override val isTopLevelType: Boolean by lazy { element.enclosingElement != null && element.enclosingElement is PackageElement }

    override val simpleName: String = element.simpleName.toString()

    override val id: String = declaredType.toString()

    override val packageName: String by lazy {
        var enclosingElement: Element? = element.enclosingElement
        var limit = 100
        while (limit-- > 0) {
            if (enclosingElement == null) {
                return@lazy ""
            }
            if (enclosingElement is PackageElement) {
                return@lazy enclosingElement.toString()
            }
            enclosingElement = enclosingElement.enclosingElement
        }
        throw RuntimeException()
    }
    override val qualifiedName: String = element.toString()

    override val superClass: LazyType by lazy {
        AptLazyType(element.superclass as DeclaredType)
    }
    override val interfaces: List<LazyType> by lazy {
        element.interfaces.map {
            AptLazyType(it as DeclaredType)
        }
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
        ).filterNotNull()
            .flatten()
    }

    override val typeParameters: List<TypeArgument>
        get() = TODO("Not yet implemented")

    companion object {
        operator fun invoke() {

        }
    }

}