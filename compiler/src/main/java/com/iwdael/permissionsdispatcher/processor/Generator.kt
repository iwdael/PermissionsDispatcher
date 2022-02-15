package com.iwdael.permissionsdispatcher.processor

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
interface Generator {
    fun generate(): String
    fun addPackage(packageName: String)
    fun addSpaceLine()
    fun addImport(importName: String)
    fun addPermissions(method: MethodElement)
    fun addRequestPermission(method: MethodElement)
}