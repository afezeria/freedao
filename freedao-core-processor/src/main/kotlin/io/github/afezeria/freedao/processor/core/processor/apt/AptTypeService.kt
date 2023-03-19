package io.github.afezeria.freedao.processor.core.processor.apt

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.global
import io.github.afezeria.freedao.processor.core.processingEnvironment
import io.github.afezeria.freedao.processor.core.processor.*
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

class AptTypeService(val processingEnv: ProcessingEnvironment) : TypeService {
    val typeUtils = processingEnv.typeUtils
    val elementUtils = processingEnv.elementUtils
    override fun getByClassName(className: String): LazyType {
        val element = sync { elementUtils.getTypeElement(className) }
        val declaredType = sync { typeUtils.getDeclaredType(element) }
        return AptLazyType.valueOf(declaredType)
    }

    override fun getPrimitiveType(enum: PrimitiveTypeEnum): PrimitiveType {
        val type = typeCache.computeIfAbsent(enum.name.lowercase()) {
            sync {
                val type = typeUtils.getPrimitiveType(
                    when (enum) {
                        PrimitiveTypeEnum.BOOLEAN -> TypeKind.BOOLEAN
                        PrimitiveTypeEnum.BYTE -> TypeKind.BYTE
                        PrimitiveTypeEnum.SHORT -> TypeKind.SHORT
                        PrimitiveTypeEnum.INT -> TypeKind.INT
                        PrimitiveTypeEnum.LONG -> TypeKind.LONG
                        PrimitiveTypeEnum.CHAR -> TypeKind.CHAR
                        PrimitiveTypeEnum.FLOAT -> TypeKind.FLOAT
                        PrimitiveTypeEnum.DOUBLE -> TypeKind.DOUBLE
                    }
                )
                object : PrimitiveType {
                    override val typeEnumValue: PrimitiveTypeEnum = enum
                    override val id: String = enum.name.lowercase()
                    override val delegate: Any = type
                }
            }
        }
        return type as PrimitiveType
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
        type.typeParameters

        TODO("Not yet implemented")
    }

    override fun isSameType(t1: LazyType, t2: LazyType): Boolean {
        return t1 == t2
    }

    override fun isAssignable(t1: LazyType, t2: LazyType): Boolean {
        return typeAssignableCache.computeIfAbsent(t1.id + ":" + t2.id) {
            typeUtils.isAssignable(t1.delegate as TypeMirror, t2.delegate as TypeMirror)
        }
    }

    override fun catchHandlerException(position: Any?, block: () -> Unit): Exception? {
        position as Element
        try {
            block()
        } catch (e: Throwable) {
            if (e is HandlerException) {
                processingEnvironment.messager.printMessage(Diagnostic.Kind.ERROR, e.message, e.element ?: position)
                if (global.debug) {
                    val stringWriter = StringWriter()
                    val printWriter = PrintWriter(stringWriter)
                    e.printStackTrace(printWriter)
                    processingEnvironment.messager.printMessage(
                        Diagnostic.Kind.WARNING,
                        stringWriter.toString()
                    )
                }
            } else {
                throw e
            }
        }
        return null
    }

    override fun <T : Annotation> getMirroredType(annotation: T, block: T.() -> Unit): LazyType {
        val declaredType = sync {
            try {
                block(annotation)
                //unreachable
                throw IllegalStateException()
            } catch (e: MirroredTypeException) {
                e.typeMirror as DeclaredType
            }
        }
        return AptLazyType.valueOf(declaredType)
    }

    override fun createMethodSpecBuilder(method: LazyMethod): MethodSpec.Builder {
        return MethodSpec.overriding(method.delegate as ExecutableElement)
    }

    override fun createImplementClassTypeSpecBuilder(interfaceType: Any, implementClassName: String): TypeSpec.Builder {
        interfaceType as DeclaredType
        return TypeSpec.classBuilder(implementClassName).apply {
            addSuperinterface(interfaceType)
            addModifiers(Modifier.PUBLIC)

            addAnnotations(
                interfaceType.asElement().annotationMirrors
                    .map { AnnotationSpec.get(it) }
            )
        }
    }

}