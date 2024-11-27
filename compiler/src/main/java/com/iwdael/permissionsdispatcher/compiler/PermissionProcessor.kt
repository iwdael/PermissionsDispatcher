package com.iwdael.permissionsdispatcher.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import com.iwdael.kotlinsymbolprocessor.asKspClass
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionProcessor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {
    private var processed = false

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (processed) return emptyList()
        processed = true
        resolver.getAllFiles()
            .toList()
            .flatMap { it.declarations }
            .filter { it.isAnnotationPresent(PermissionsDispatcher::class) }
            .onEach {
                val dependencies = Dependencies(aggregating = false, it.containingFile!!)
                PermissionGenerator(Permission(it.asKspClass)).write(env.codeGenerator, dependencies)
            }
        return emptyList()
    }


}