package com.iwdael.permissiondispatcher.compiler.compat

import com.iwdael.annotationprocessorparser.Method
import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale
import com.squareup.javapoet.ClassName

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */

fun needsEquals(a: List<Method>, a2: List<Method>): Boolean {
    return true
}

fun Method.permissionFieldName(): String {
    return "PERMISSION_${this.name.toUpperCase()}_${
        this.parameter
            .map { ClassName.bestGuess(it.simpleType()) }
            .map { it.simpleName().toUpperCase() }
            .joinToString(separator = "_")
    }"
}

fun Method.permissionMethodName(): String {
    return this.name + "WithPermission"
}

fun Method.permissionJavaFiledValue(): String {
    return if (getAnnotation(PermissionDispatcherRationale::class.java) != null)
        "{\"${getAnnotation(PermissionDispatcherRationale::class.java)!!.target}\"}"
    else if (getAnnotation(PermissionsDispatcherRationale::class.java) != null)
        getAnnotation(PermissionsDispatcherRationale::class.java)!!.value
            .joinToString(prefix = "{", postfix = "}", separator = ",", transform = { "\"${it}\"" })
    else if (getAnnotation(PermissionsDispatcherDenied::class.java) != null)
        getAnnotation(PermissionsDispatcherDenied::class.java)!!.value
            .joinToString(prefix = "{", postfix = "}", separator = ",", transform = { "\"${it}\"" })
    else
        getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value
            .joinToString(prefix = "{", postfix = "}", separator = ",", transform = { "\"${it}\"" })
}

fun Method.permissionKotlinFiledValue(): String {
    return if (getAnnotation(PermissionDispatcherRationale::class.java) != null)
        "(\"${getAnnotation(PermissionDispatcherRationale::class.java)!!.target}\")"
    else if (getAnnotation(PermissionsDispatcherRationale::class.java) != null)
        getAnnotation(PermissionsDispatcherRationale::class.java)!!.value
            .joinToString(prefix = "(", postfix = ")", separator = ",", transform = { "\"${it}\"" })
    else if (getAnnotation(PermissionsDispatcherDenied::class.java) != null)
        getAnnotation(PermissionsDispatcherDenied::class.java)!!.value
            .joinToString(prefix = "(", postfix = ")", separator = ",", transform = { "\"${it}\"" })
    else
        getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value
            .joinToString(prefix = "(", postfix = ")", separator = ",", transform = { "\"${it}\"" })
}