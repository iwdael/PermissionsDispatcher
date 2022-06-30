package com.iwdael.permissiondispatcher.compiler2.compat

import com.iwdael.permissionsdispatcher.annotation.*

fun PermissionsDispatcherNeeds.isSameGroup(p: PermissionDispatcherRationale): Boolean {
    if (this.identity != p.identity) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    return true
}

fun PermissionsDispatcherNeeds.isSameGroup(p: PermissionsDispatcherDenied): Boolean {
    if (this.identity != p.identity) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    return true
}

fun PermissionsDispatcherNeeds.isSameGroup(p: PermissionsDispatcherNeeds): Boolean {
    if (this.identity != p.identity) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    return true
}

fun PermissionsDispatcherNeeds.isSameGroup(p: PermissionsDispatcherRationale): Boolean {
    if (this.identity != p.identity) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    return true
}


