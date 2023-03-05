package io.github.afezeria.freedao.processor.core.processor

import java.util.*

class TypeDescription private constructor(val name: String) {
    val typeArguments: MutableList<TypeDescription> = mutableListOf()
    override fun toString(): String {
        return "$name${typeArguments.joinToString(separator = ",").takeIf { it.isNotEmpty() }?.let { "<$it>" } ?: ""}"
    }

    fun toLazyType(typeService: TypeService): LazyType {
        val type = typeService.getByClassName(name)
        val typeArgs = typeArguments.map { it.toLazyType(typeService) }
        typeService.getParameterizedType(type, *typeArgs.toTypedArray())
        return type
    }

    companion object {

        operator fun invoke(text: String): TypeDescription {
            val str = text.replace(" ", "")
            val stack = LinkedList<TypeDescription>()
            var head = 0
            var tail = 0
            while (tail < str.length) {
                when (str[tail]) {
                    '<' -> {
                        val node = TypeDescription(str.substring(head, tail))
                        stack.peekLast()?.typeArguments?.add(node)
                        stack.add(node)
                        head = tail + 1
                    }

                    '>' -> {
                        if (tail > head) {
                            stack.last.typeArguments.add(TypeDescription(str.substring(head, tail)))
                        }
                        if (stack.size > 1) {
                            stack.removeLast()
                        }
                        head = tail + 1
                    }

                    ',' -> {
                        if (tail > head) {
                            stack.last.typeArguments.add(TypeDescription(str.substring(head, tail)))
                        }
                        head = tail + 1
                    }
                }
                tail++
            }
            return if (head == 0) {
                return TypeDescription(str)
            } else {
                stack.first
            }
        }
    }
}