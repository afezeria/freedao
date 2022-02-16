package com.github.afezeria.freedao.processor.core

import com.github.afezeria.freedao.annotation.Dao
import com.github.afezeria.freedao.processor.core.method.MethodModel
import com.github.afezeria.freedao.processor.core.spi.BuildDaoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

/**
 *
 */
class DaoModel(val element: TypeElement) {

    lateinit var annotation: Any
    var crudEntity: EntityObjectModel? = null

    var packageName = "${(element.enclosingElement as PackageElement).qualifiedName}"
    var implClassName = "${element.simpleName}Impl"
    var implQualifiedName = "$packageName.$implClassName"

    var isJavaCode: Boolean = true

    var classBuilder: TypeSpec.Builder = TypeSpec.classBuilder(implClassName).apply {
        addSuperinterface(element.asType())
        addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        addAnnotations(
            element.annotationMirrors
                .filter { !it.annotationType.isSameType(Dao::class) }
                .map {
                    AnnotationSpec.get(it)
                }
        )
    }

    init {
        if (element.enclosingElement.kind != ElementKind.PACKAGE) {
            throw HandlerException("dao interface must be top level interface")
        }
        crudEntity =
            element.getAnnotation(Dao::class.java)
                .mirroredType { crudEntity }
                .takeIf { !it.isSameType(Any::class) }
                ?.let { ObjectModel(it) }
                ?.let {
                    if (it !is EntityObjectModel) {
                        throw HandlerException("The class as the value of crudEntity must be annotated with table")
                    } else {
                        it
                    }
                }
    }

    fun render() {
        val results = listOf(
            element.enclosedElements,
            *element.interfaces.map { (it as DeclaredType).asElement().enclosedElements }.toTypedArray()
        ).flatten()
            .filter {
                it.kind == ElementKind.METHOD && !it.modifiers.contains(Modifier.DEFAULT)
            }.map { element ->
                runCatching {
                    MethodModel(element as ExecutableElement, this).render()
                }.apply {
                    this.exceptionOrNull()?.let { e ->
                        if (e is HandlerException) {
                            processingEnvironment.messager.printMessage(Diagnostic.Kind.ERROR, e.message, element)
                            if (debug) {
                                val stringWriter = StringWriter()
                                val printWriter = PrintWriter(stringWriter)
                                e.printStackTrace(printWriter)
                                processingEnvironment.messager.printMessage(
                                    Diagnostic.Kind.ERROR,
                                    stringWriter.toString()
                                )
                            }
                        } else {
                            throw e
                        }
                    }
                }
            }.takeIf { it.all { it.isSuccess } }
            ?.map { it.getOrNull()!! }
            ?: return
        classBuilder.addMethods(results)


        buildDaoServices.forEach { service ->
            service.build(this, classBuilder)
        }
        JavaFile.builder(packageName, classBuilder.build()).build()
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