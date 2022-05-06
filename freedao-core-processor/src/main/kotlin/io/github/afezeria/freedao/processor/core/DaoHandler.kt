package io.github.afezeria.freedao.processor.core

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.annotation.Dao
import io.github.afezeria.freedao.processor.core.method.MethodHandler
import io.github.afezeria.freedao.processor.core.spi.BuildDaoService
import java.util.*
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType

/**
 *
 */
class DaoHandler(val element: TypeElement) {

    var crudEntity: EntityObjectModel? = null

    var packageName = "${(element.enclosingElement as PackageElement).qualifiedName}"
    var implClassName = "${element.simpleName}Impl"
    var implQualifiedName = "$packageName.$implClassName"

    var isJavaCode: Boolean = true

    var classBuilder: TypeSpec.Builder = TypeSpec.classBuilder(implClassName).apply {
        addSuperinterface(element.asType())
        addModifiers(Modifier.PUBLIC)
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
        val results = listOf(
            element.enclosedElements,
            *element.interfaces.map { (it as DeclaredType).asElement().enclosedElements }.toTypedArray()
        ).flatten()
            .filter {
                it.kind == ElementKind.METHOD && !it.modifiers.contains(Modifier.DEFAULT)
            }.map { element ->
                runCatchingHandlerExceptionOrThrow(element) {
                    MethodHandler(element as ExecutableElement, this).render()
                }
            }.takeIf { it.all { it != null } }
            ?.map { it!! }
            ?: return
        classBuilder.addMethods(results)


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