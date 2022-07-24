package com.iwdael.permissiondispatcher.compiler

import com.google.auto.service.AutoService
import com.iwdael.annotationprocessorparser.Class
import com.iwdael.permissiondispatcher.compiler.maker.JavaMaker
import com.iwdael.permissiondispatcher.compiler.maker.KotlinMaker
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(PermissionsDispatcher::class.java.canonicalName)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv?.getElementsAnnotatedWith(PermissionsDispatcher::class.java)
            ?.map { Class(it) }
            ?.map { if (it.getAnnotation(Metadata::class.java)==null) JavaMaker(it) else KotlinMaker(it) }
            ?.forEach { it.make(processingEnv.filer) }
        return true
    }
}