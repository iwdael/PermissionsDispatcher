package com.iwdael.permissionsdispatcher.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.iwdael.kotlinsymbolprocessor.asKspClass
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionProcessor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray())
        resolver.getSymbolsWithAnnotation(PermissionsDispatcher::class.java.name)
            .filter { it is KSClassDeclaration }
            .map { it.asKspClass }
            .map { Permission(it) }
            .map { PermissionGenerator(it) }
            .onEach {
                it.write(env.codeGenerator, dependencies)
            }
        return emptyList()
    }
}