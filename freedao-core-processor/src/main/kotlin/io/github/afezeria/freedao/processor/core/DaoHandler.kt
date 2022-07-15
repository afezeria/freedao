package io.github.afezeria.freedao.processor.core

import com.squareup.javapoet.*
import io.github.afezeria.freedao.annotation.Dao
import io.github.afezeria.freedao.processor.core.method.MethodDefinition
import io.github.afezeria.freedao.processor.core.processor.LazyType
import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import io.github.afezeria.freedao.processor.core.processor.isSameType
import io.github.afezeria.freedao.processor.core.processor.typeService
import io.github.afezeria.freedao.processor.core.spi.BuildDaoService
import java.util.*
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
 *
 */
class DaoHandler(
    val element: TypeElement,
    val type: LazyType
) : LazyType by type {

    init {
        if (!type.isTopLevelType()) {
            throw HandlerException("Dao must be top level interface")
        }
    }

    var crudEntityType: LazyType? = type.getAnnotation(Dao::class)
        .mirrorType { crudEntity }
        .takeIf { !it.isSameType(typeService.get(Any::class)) }

    var crudEntity: EntityObjectModel? = null

    //    var packageName = "${(element.enclosingElement as PackageElement).qualifiedName}"
    var implClassName = "${element.simpleName}Impl"
    var implQualifiedName = "$packageName.$implClassName"

    var isJavaCode: Boolean = true

    var classBuilder: TypeSpec.Builder = TypeSpec.classBuilder(implClassName).apply {
        addSuperinterface(element.asType())
        addModifiers(Modifier.PUBLIC)
        addAnnotations(
            this@DaoHandler.annotations
                .filter {
                    !it.type.isSameType(Dao::class) && !it.type.isSameType(Metadata::class)
                }
                .map {
                    AnnotationSpec.builder(
                        ClassName.get(
                            it.type.packageName,
                            it.type.simpleName,
                            *it.type.simpleNames
                        )
                    ).apply {
                        it.valueName2Literal().forEach { (name, literal) ->
                            addMember(name, CodeBlock.of(literal))
                        }
                    }.build()
                }
        )
        addAnnotations(
            element.annotationMirrors
                .filter { !it.annotationType.isSameType(Dao::class) }
                .filter { !it.annotationType.isSameType(Metadata::class) }
                .map {
                    AnnotationSpec.get(it)
                }
        )
    }

    init {
        if (element.enclosingElement.kind != ElementKind.PACKAGE
            || element.kind != ElementKind.INTERFACE
            || element.kind == ElementKind.ANNOTATION_TYPE
        ) {
            throw HandlerException("Dao must be top level interface")
        }
        crudEntity =
            element.getAnnotation(Dao::class.java)
                .mirroredType { crudEntity }
                .takeIf { !it.isSameType(Any::class) }
                ?.let { EntityObjectModel(it) }
    }

    fun render() {
        val exceptions = allMethods
            .filter { m ->
                m.modifiers.any { it == Modifier.DEFAULT || it == Modifier.STATIC }
            }.mapNotNull {
                typeService.catchHandlerException(type) {
                    MethodDefinition.build(this, it).render()
                }
            }
        if (exceptions.isNotEmpty()) {
            return
        }
//        val results = listOf(
//            element.enclosedElements,
//            *element.interfaces.map { (it as DeclaredType).asElement().enclosedElements }.toTypedArray()
//        ).flatten()
//            .filter {
//                it.kind == ElementKind.METHOD && !it.modifiers.contains(Modifier.DEFAULT)
//            }.map { element ->
//                runCatchingHandlerExceptionOrThrow(element) {
//                    MethodHandler(element as ExecutableElement, this).render()
//                }
//            }.takeIf { it.all { it != null } }
//            ?.map { it!! }
//            ?: return
//        classBuilder.addMethods(results)


        buildDaoServices.forEach { service ->
            service.build(this, classBuilder)
        }
        JavaFile.builder(packageName, classBuilder.build())
            .indent("    ")
            .build()
            .writeTo(processingEnvironment.filer)

    }

    companion object {
        val buildDaoServices by lazy {
            ServiceLoader.load(
                BuildDaoService::class.java,
                MainProcessor::class.java.classLoader
            ).sortedBy { it.order }
        }
    }

}