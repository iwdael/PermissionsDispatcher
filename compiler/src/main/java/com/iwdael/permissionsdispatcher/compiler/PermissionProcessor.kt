package com.iwdael.permissionsdispatcher.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.iwdael.kotlinsymbolprocessor.asKspClass
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionProcessor(private val env: SymbolProcessorEnvironment) :
    SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(PermissionsDispatcher::class.java.name)
            .map { it.asKspClass }
            .map { Permission(it) }
            .map { PermissionGenerator(it) }
            .forEach {
                try {
                    it.write(env.codeGenerator)
                } catch (_: Exception) {
                }
            }
        return emptyList()
    }
}