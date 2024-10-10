package com.iwdael.permissionsdispatcher.compiler

import com.iwdael.kotlinsymbolprocessor.KSPClass
import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale

typealias PDNeeds = PermissionsDispatcherNeeds
typealias PDDenied = PermissionsDispatcherDenied
typealias PDRationales = PermissionsDispatcherRationale
typealias PDRationale = PermissionDispatcherRationale

/**
 * @author iwdael
 * @since 2024/8/20
 * @desc this is Permissions
 */

class Permission(val kspClass: KSPClass) {
    val packageName = kspClass.kspPackage.name
    val targetName = kspClass.simpleName + "PermissionDispatcher"
    val need = kspClass.functionsByAnnotation(PDNeeds::class)
    val denied = kspClass.functionsByAnnotation(PDDenied::class)
    val rationales = kspClass.functionsByAnnotation(PDRationales::class)
    val rationale = kspClass.functionsByAnnotation(PDRationale::class)

    init {
        checkNeed()
        checkDenied()
        checkRotations()
        checkRotation()
    }

    private fun checkNeed() {
        val values = mutableListOf<Pair<List<String>, Int>>()
        for (func in need) {
            val (strings, id) = func.annotation(PDNeeds::class)!!.values
            val value = strings to id
            if (!func.isPublic) {
                throw NoPublicExp(func.qualifierName)
            }
            if (strings.isEmpty()) {
                throw NoPermissionExp(func.qualifierName)
            }
            if (values.contains(value)) {
                throw NoIdExp(func.qualifierName)
            }
            values.add(value)
        }
    }

    private fun checkDenied() {
        val values = mutableListOf<Pair<List<String>, Int>>()
        for (func in denied) {
            val (strings, id) = func.annotation(PDDenied::class)!!.values
            val value = strings to id
            if (!func.isPublic) {
                throw NoPublicExp(func.qualifierName)
            }
            if (strings.isEmpty()) {
                throw NoPermissionExp(func.qualifierName)
            }
            if (func.kspParameters.size > 2) {
                throw OutOfParameterExp(2, func.qualifierName)
            }
            if (need.groups(func).isEmpty()) {
                throw NoMatchNeedExp(func.qualifierName)
            }
            val validParameter = func.kspParameters
                .filter { it.type.simpleName == Map::class.simpleName}
                .filter { it.type.types.size == 2 }
                .filter { it.type.types[0]!!.simpleName == String::class.simpleName || it.type.types[1]!!.simpleName == Boolean::class.simpleName }

            if (validParameter.size != func.kspParameters.size || func.kspParameters.size != 1) {
                throw ParameterExp(func.qualifierName)
            }
            if (values.contains(value)) {
                throw NoIdExp(func.qualifierName)
            }
            values.add(value)
        }


    }

    private fun checkRotations() {
        val values = mutableListOf<Pair<List<String>, Int>>()
        for (func in rationales) {
            val (strings, id) = func.annotation(PDRationales::class)!!.values
            val value = strings to id
            if (!func.isPublic) {
                throw NoPublicExp(func.qualifierName)
            }
            if (strings.isEmpty()) {
                throw NoPermissionExp(func.qualifierName)
            }
            if (func.kspParameters.size != 1) {
                throw NoParamRationaleExp(func.qualifierName)
            }
            if (func.kspParameters[0].type.simpleName != "PermissionsRationale") {
                throw NoParamRationaleExp(func.qualifierName)
            }
            if (need.groups(func).isEmpty()) {
                throw NoMatchNeedExp(func.qualifierName)
            }
            if (values.contains(value)) {
                NoIdExp(func.qualifierName)
            }
            values.add(value)
        }
    }

    private fun checkRotation() {
        val values = arrayListOf<Triple<List<String>, String, Int>>()
        for (func in rationale) {
            val anno = func.annotation(PDRationale::class)!!
            val value = Triple(anno.value.toList().sorted(), anno.target, anno.identity)
            val (strings, target, _) = value
            if (!func.isPublic) {
                throw NoPublicExp(func.qualifierName)
            }
            if (strings.isEmpty()) {
                throw NoPermissionExp(func.qualifierName)
            }
            if (target.isEmpty()) {
                throw NoPermissionExp(func.qualifierName)
            }
            if (!strings.contains(target)) {
                throw NoMatchTarget(strings, func.qualifierName)
            }
            if (func.kspParameters.size != 1) {
                throw NoParamRationaleExp(func.qualifierName)
            }
            if (func.kspParameters[0].type.simpleName != "PermissionsRationale") {
                throw NoParamRationaleExp(func.qualifierName)
            }
            if (need.groups(func).isEmpty()) {
                throw NoMatchNeedExp(func.qualifierName)
            }
            if (values.contains(value)) {
                throw SameAnnotationExp(func.qualifierName)
            }
            if (rationale.groups(func).size != strings.size) {
                throw NumberSameExp(func.qualifierName)
            }
            values.add(value)
        }

    }
}