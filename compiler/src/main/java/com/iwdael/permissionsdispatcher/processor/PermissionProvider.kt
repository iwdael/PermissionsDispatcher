package com.iwdael.permissionsdispatcher.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return PermissionProcessor(environment.codeGenerator, environment.logger)
    }
}