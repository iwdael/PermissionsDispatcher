package com.iwdael.permissionsdispatcher.processor

import com.iwdael.kotlinsymbolprocessor.KSPFunction
import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale
import com.squareup.kotlinpoet.ClassName
import java.util.Locale

/**
 * @author iwdael
 * @since 2024/8/21
 * @desc this is FunctionUtils
 */

fun String.funInvoke(vararg parameters: String): String {
    return "${this}(${parameters.joinToString()})"
}

fun String.asTypeName() = ClassName.bestGuess(this)

val KSPFunction.values: Pair<List<String>, Int>
    get() = when {
        this.annotation(PermissionsDispatcherNeeds::class) != null -> this.annotation(PermissionsDispatcherNeeds::class)!!.values
        this.annotation(PermissionsDispatcherRationale::class) != null -> this.annotation(PermissionsDispatcherRationale::class)!!.values
        this.annotation(PermissionDispatcherRationale::class) != null -> this.annotation(PermissionDispatcherRationale::class)!!.values
        else -> this.annotation(PermissionsDispatcherDenied::class)!!.values
    }

fun List<KSPFunction>.groups(element: KSPFunction) = this.filter { it.values == element.values }
val KSPFunction.permissionName: String get() = "permission_${name}_${kspParameters.joinToString(separator = "_") { it.name }}_${values.second}".uppercase(Locale.getDefault())
val KSPFunction.permissionValue: String get() = values.first.joinToString { "\"${it}\"" }
val KSPFunction.targetPermissionValue: String get() = "\"${this.annotation(PermissionDispatcherRationale::class)!!.target}\""
