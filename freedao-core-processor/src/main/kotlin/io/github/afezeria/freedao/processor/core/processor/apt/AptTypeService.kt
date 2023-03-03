package io.github.afezeria.freedao.processor.core.processor.apt

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.processor.core.processor.*
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

class AptTypeService(val processingEnv: ProcessingEnvironment) : TypeService {
    val typeUtils = processingEnv.typeUtils
    val elementUtils = processingEnv.elementUtils
    override fun get(className: String): LazyType {
        val element = elementUtils.getTypeElement(className)
        return getLazyTypeByTypeElement(element)
    }

    override fun get(clazz: Class<*>): LazyType {
        val element = elementUtils.getTypeElement(clazz.name)
        return getLazyTypeByTypeElement(element)
    }

    private fun getLazyTypeByTypeElement(typeElement: TypeElement): LazyType {
        val declaredType = typeUtils.getDeclaredType(typeElement)
        return AptLazyType(declaredType)
    }

    override fun getPrimitiveType(enum: PrimitiveTypeEnum): PrimitiveType {
        TODO("Not yet implemented")
    }

    override fun getWildcardType(extendsBound: LazyType?, superBound: LazyType?): LazyType {
        TODO("Not yet implemented")
    }

    override fun getParameterizedType(target: LazyType, vararg typeArgs: LazyType): LazyType {
        TODO("Not yet implemented")
    }

    override fun boxed(type: LazyType): LazyType {
        return if (type is PrimitiveType) {
            when (type.typeEnumValue) {
                PrimitiveTypeEnum.BOOLEAN -> get(Boolean::class.java)
                PrimitiveTypeEnum.BYTE -> get(Byte::class.java)
                PrimitiveTypeEnum.SHORT -> get(Short::class.java)
                PrimitiveTypeEnum.INT -> get(Int::class.java)
                PrimitiveTypeEnum.LONG -> get(Long::class.java)
                PrimitiveTypeEnum.CHAR -> get(Char::class.java)
                PrimitiveTypeEnum.FLOAT -> get(Float::class.java)
                PrimitiveTypeEnum.DOUBLE -> get(Double::class.java)
            }
        } else {
            type
        }
    }

    override fun erasure(type: LazyType): LazyType {
        TODO("Not yet implemented")
    }

    override fun isSameType(t1: LazyType, t2: LazyType): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAssignable(t1: LazyType, t2: LazyType): Boolean {
        TODO("Not yet implemented")
    }

    override fun catchHandlerException(position: Any?, block: () -> Unit): Exception? {
        TODO("Not yet implemented")
    }

    override fun <T : Annotation> getMirroredType(annotation: T, block: T.() -> Unit): LazyType {
        TODO("Not yet implemented")
    }

    override fun createMethodSpecBuilder(method: LazyMethod): MethodSpec.Builder {
        TODO("Not yet implemented")
    }

    override fun createTypeSpecBuilder(type: LazyType): TypeSpec.Builder {
        TODO("Not yet implemented")
    }
}


class ClassNameNode private constructor(val name: String) {
    val typeArguments: MutableList<ClassNameNode> = mutableListOf()
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

        operator fun invoke(text: String): ClassNameNode {
            val stack = LinkedList<ClassNameNode>()
            var head = 0
            var tail = 0
            while (tail < text.length) {
                when (text[tail]) {
                    '<' -> {
                        val node = ClassNameNode(text.substring(head, tail))
                        stack.peekLast()?.typeArguments?.add(node)
                        stack.add(node)
                        head = tail + 1
                    }

                    '>' -> {
                        if (tail > head) {
                            stack.last.typeArguments.add(ClassNameNode(text.substring(head, tail)))
                        }
                        if (stack.size > 1) {
                            stack.removeLast()
                        }
                        head = tail + 1
                    }

                    ',' -> {
                        if (tail > head) {
                            stack.last.typeArguments.add(ClassNameNode(text.substring(head, tail)))
                        }
                        head = tail + 1
                    }
                }
                tail++
            }
            return if (head == 0) {
                return ClassNameNode(text)
            } else {
                stack.first
            }
        }
    }
}

suspend fun main() {

    checkAll(
        listOf(
//            "abc",
//            "java.util.Map<java.lang.String,java.lang.String>",
            "a<b<c<f<d,g>>>,C<d>>",
        ).exhaustive()
    ) {
        it shouldBe ClassNameNode(it).toString()
    }

}
