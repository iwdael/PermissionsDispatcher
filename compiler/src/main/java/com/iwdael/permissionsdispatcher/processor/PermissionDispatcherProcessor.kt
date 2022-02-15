package com.iwdael.permissionsdispatcher.processor

import com.google.auto.service.AutoService
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher
import java.io.File
import java.io.FileWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@AutoService(Processor::class)
class PermissionDispatcherProcessor : AbstractProcessor() {
    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(PermissionsDispatcher::class.java.canonicalName)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        environment: RoundEnvironment
    ): Boolean {
        environment.getElementsAnnotatedWith(PermissionsDispatcher::class.java)
            .sortedBy { it.simpleName.toString() }
            .map { PermissionDispatcher(ClassElement(it)) }
            .forEach {
                val writer = FileWriter(findGenerateFile(it))
                if (it.element.sourceFileIsKotlin()) {
                    writer.write(KotlinGenerator(it).generate())
                } else {
                    writer.write(JavaGenerator(it).generate())
                }
                writer.flush()
                writer.close()
            }

        return true
    }

    private fun findGenerateFile(element: PermissionDispatcher): File {
        val generateFile = File(
            processingEnv.filer.createSourceFile(
                element.generatedFullClassName,
                element.element.element
            ).toUri()
        ).apply { parentFile.mkdirs() }
        return if (element.element.sourceFileIsKotlin()) {
            File(
                generateFile.parentFile,
                "${element.generatedClassName}.kt"
            ).apply { parentFile.mkdirs() }
        } else generateFile
    }


}