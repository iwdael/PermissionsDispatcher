package com.iwdael.permissiondispatcher.compiler.compat

import com.iwdael.permissiondispatcher.compiler.permission.PermissionGroup
import com.iwdael.permissionsdispatcher.annotation.*
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
fun PermissionsDispatcherNeeds.isSameGroup(p: PermissionDispatcherRationale): Boolean {
    if (this.identity != p.identity) return false
    if (this.value.size != p.value.size) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    return true
}

fun PermissionsDispatcherNeeds.isSameGroup(p: PermissionsDispatcherDenied): Boolean {
    if (this.identity != p.identity) return false
    if (this.value.size != p.value.size) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    return true
}

fun PermissionsDispatcherNeeds.isSameGroup(p: PermissionsDispatcherNeeds): Boolean {
    if (this.identity != p.identity) return false
    if (this.value.size != p.value.size) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    return true
}

fun PermissionsDispatcherNeeds.isSameGroup(p: PermissionsDispatcherRationale): Boolean {
    if (this.identity != p.identity) return false
    if (this.value.size != p.value.size) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    return true
}


fun PermissionDispatcherRationale.isSame(p: PermissionDispatcherRationale): Boolean {
    if (this.identity != p.identity) return false
    if (this.value.size != p.value.size) return false
    p.value.forEach {
        if (!this.value.contains(it)) return false
    }
    if (p.target != this.target) return false
    return true
}

fun List<PermissionDispatcherRationale>.hasSame(): Boolean {
    val temp = arrayListOf(*this.toTypedArray())
    val temp2 = arrayListOf<PermissionDispatcherRationale>()
    while (temp.isNotEmpty()) {
        val rationale = temp.removeAt(0)
        if (temp2.any { it.isSame(rationale) }) return true
        temp2.add(rationale)
    }
    return false
}

fun PermissionGroup.permissionNotDispatch(): MutableList<String> {
    return this.needs.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value
        .toMutableList()
        .apply {
            removeAll(rationale.map { it.getAnnotation(PermissionDispatcherRationale::class.java)!!.target })
        }
}

