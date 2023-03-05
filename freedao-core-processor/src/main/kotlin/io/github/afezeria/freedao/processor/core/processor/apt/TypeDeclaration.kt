package io.github.afezeria.freedao.processor.core.processor.apt

import io.github.afezeria.freedao.processor.core.processor.LazyType
import io.github.afezeria.freedao.processor.core.processor.TypeService
import java.util.*

class TypeDeclaration private constructor(val name: String) {
    val typeArguments: MutableList<TypeDeclaration> = mutableListOf()
    override fun toString(): String {
        return "$name${typeArguments.joinToString(separator = ",").takeIf { it.isNotEmpty() }?.let { "<$it>" } ?: ""}"
    }

    fun toLazyType(typeService: TypeService): LazyType {
        val type = typeService.get(name)
        val typeArgs = typeArguments.map { it.toLazyType(typeService) }
        typeService.getParameterizedType(type, *typeArgs.toTypedArray())
        return type
    }

    companion object {

        operator fun invoke(text: String): TypeDeclaration {
            val stack = LinkedList<TypeDeclaration>()
            var head = 0
            var tail = 0
            while (tail < text.length) {
                when (text[tail]) {
                    '<' -> {
                        val node = TypeDeclaration(text.substring(head, tail))
                        stack.peekLast()?.typeArguments?.add(node)
                        stack.add(node)
                        head = tail + 1
                    }

                    '>' -> {
                        if (tail > head) {
                            stack.last.typeArguments.add(TypeDeclaration(text.substring(head, tail)))
                        }
                        if (stack.size > 1) {
                            stack.removeLast()
                        }
                        head = tail + 1
                    }

                    ',' -> {
                        if (tail > head) {
                            stack.last.typeArguments.add(TypeDeclaration(text.substring(head, tail)))
                        }
                        head = tail + 1
                    }
                }
                tail++
            }
            return if (head == 0) {
                return TypeDeclaration(text)
            } else {
                stack.first
            }
        }
    }
}