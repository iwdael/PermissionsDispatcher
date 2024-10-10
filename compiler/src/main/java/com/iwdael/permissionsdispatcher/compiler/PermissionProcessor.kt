package com.iwdael.permissionsdispatcher.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import com.iwdael.kotlinsymbolprocessor.asKspClass
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(PermissionsDispatcher::class.java.name)
//            .filter { it.validate() }
            .map { it.asKspClass }
            .map { Permission(it) }
            .map { PermissionGenerator(it) }
            .forEach { it.write(codeGenerator) }
        return emptyList()
    }
}