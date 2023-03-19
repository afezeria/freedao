package io.github.afezeria.freedao.processor.core

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.annotation.Dao
import io.github.afezeria.freedao.processor.core.method.MethodDefinition
import io.github.afezeria.freedao.processor.core.processor.*
import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import io.github.afezeria.freedao.processor.core.spi.BuildDaoService
import java.util.*

/**
 *
 */
class DaoHandler(
    val type: LazyType,
) {

    init {
        if (!type.isTopLevelType) {
            throw HandlerException("DAO must be top level interface")
        }
    }

    var crudEntityType: LazyType? = type.getAnnotation(Dao::class)!!
        .wrapperType { crudEntity }
        .takeIf { !it.isSameType(typeService.get(Any::class)) }

//    var crudEntity: EntityObjectModel? = null

    var implClassName = "${type.simpleName}Impl"

    var isJavaCode: Boolean = true

    var classBuilder: TypeSpec.Builder = typeService.createImplementClassTypeSpecBuilder(type, implClassName)
        .apply {
            annotations.removeIf {
                val className = it.type.toString()
                className == Dao::class.qualifiedName || className == Metadata::class.qualifiedName
            }
        }

    fun render() {
        val exceptions = type.allMethods
            .filter { Modifier.DEFAULT in it.modifiers || Modifier.STATIC in it.modifiers }
            .mapNotNull {
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
        JavaFile.builder(type.packageName, classBuilder.build())
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