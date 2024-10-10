package com.iwdael.permissionsdispatcher.compiler

import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale

/**
 * @author iwdael
 * @since 2024/8/21
 */
val PermissionsDispatcherNeeds.values: Pair<List<String>, Int> get() = value.toList().map { it.value }.sorted() to identity
val PermissionsDispatcherRationale.values: Pair<List<String>, Int> get() = value.toList().sorted() to identity
val PermissionDispatcherRationale.values: Pair<List<String>, Int> get() = value.toList().sorted() to identity
val PermissionsDispatcherDenied.values: Pair<List<String>, Int> get() = value.toList().sorted() to identity