package com.iwdael.permissionsdispatcher.processor


import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale
import com.iwdael.permissionsdispatcher.processor.exception.*
import java.util.*

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionDispatcher(val element: ClassElement) {
    val packageName = element.getPackage()
    val targetClassName = element.getClassName()
    val generatedClassName = targetClassName + "PermissionDispatcher"
    val generatedFullClassName = "${packageName}.${generatedClassName}"
    val needsMethods = element.getMethodElement(PermissionsDispatcherNeeds::class)
    val deniedMethods = element.getMethodElement(PermissionsDispatcherDenied::class)
    val rationaleMethods = element.getMethodElement(PermissionsDispatcherRationale::class)
    val rationaleSingleMethods = element.getMethodElement(PermissionDispatcherRationale::class)

    init {
        validNeedsElements()
        validDeniedElements()
        validRotationElements()
        validRotationSingleElements()
    }


    private fun validNeedsElements() {
        for (element in needsMethods) {
            if (!element.isPublic()) throw NonPublicMethodException("${element.getFullClassName()}#${element.getName()}")
        }
        for (element in needsMethods) {
            if (element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.isEmpty())
                throw NonPermissionException("${element.getFullClassName()}#${element.getName()}")
        }
        val mapPermissions = mutableMapOf<List<String>, MutableList<Int>>()
        for (element in needsMethods) {
            if (mapPermissions.containsKey(element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.toList())) {
                val identities =
                    mapPermissions[element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.toList()]!!
                if (identities.contains(element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.identity))
                    throw NonIdentityException("${element.getFullClassName()}#${element.getName()}")
                else {
                    identities.add(element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.identity)
                }
            } else {
                mapPermissions[element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.toList()] =
                    mutableListOf(element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.identity)
            }

        }
    }

    private fun validDeniedElements() {
        for (element in deniedMethods) {
            if (!element.isPublic()) throw NonPublicMethodException("${element.getFullClassName()}#${element.getName()}")
        }
        for (element in deniedMethods) {
            if (element.getAnnotation(PermissionsDispatcherDenied::class.java)!!.value.isEmpty())
                throw NonPermissionException("${element.getFullClassName()}#${element.getName()}")
        }
        for (element in deniedMethods) {
            if (element.getParameters().size > 2) throw NumberOfParametersExceedException(
                2,
                "${element.getFullClassName()}#${element.getName()}"
            )
            for (type in element.getParameters().map { it.asType() }) {
                if (type.toString() != "java.util.List<java.lang.String>" && type.toString() != "java.util.List<java.lang.Boolean>") {
                    throw TypeParameterException(
                        "java.util.List<java.lang.String>",
                        "java.util.List<java.lang.Boolean>",
                        "${element.getFullClassName()}#${element.getName()}"
                    )
                }
            }
        }
        val mapPermissions = mutableMapOf<List<String>, MutableList<Int>>()
        for (element in deniedMethods) {
            if (mapPermissions.containsKey(element.getAnnotation(PermissionsDispatcherDenied::class.java)!!.value.toList())) {
                val identities =
                    mapPermissions[element.getAnnotation(PermissionsDispatcherDenied::class.java)!!.value.toList()]!!
                if (identities.contains(element.getAnnotation(PermissionsDispatcherDenied::class.java)!!.identity)) throw NonIdentityException(
                    "${element.getFullClassName()}#${element.getName()}"
                ) else {
                    identities.add(element.getAnnotation(PermissionsDispatcherDenied::class.java)!!.identity)
                }
            } else {
                mapPermissions[element.getAnnotation(PermissionsDispatcherDenied::class.java)!!.value.toList()] =
                    mutableListOf(element.getAnnotation(PermissionsDispatcherDenied::class.java)!!.identity)
            }

        }
    }

    private fun validRotationElements() {
        for (element in rationaleMethods) {
            if (!element.isPublic()) throw NonPublicMethodException("${element.getFullClassName()}#${element.getName()}")
        }
        for (element in rationaleMethods) {
            if (element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.value.isEmpty())
                throw NonPermissionException("${element.getFullClassName()}#${element.getName()}")
        }
        for (element in rationaleMethods) {
            if (element.getParameters().size != 1) throw PermissionDispatcherException("Can only have one parameter, and the type is PermissionsRationale.(${element.getFullClassName()}#${element.getName()})")
            if (element.getParameters()[0].asType()
                    .toString() != "com.iwdael.permissionsdispatcher.annotation.PermissionsRationale"
            )
                throw PermissionDispatcherException("The parameter type can only be PermissionsRationale.(${element.getFullClassName()}#${element.getName()})")
        }
        val mapPermissions = mutableMapOf<List<String>, MutableList<Int>>()
        for (element in rationaleMethods) {
            if (mapPermissions.containsKey(element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.value.toList())) {
                val identities =
                    mapPermissions[element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.value.toList()]!!
                if (identities.contains(element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.identity)) throw NonIdentityException(
                    "${element.getFullClassName()}#${element.getName()}"
                ) else {
                    identities.add(element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.identity)
                }
            } else {
                mapPermissions[element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.value.toList()] =
                    mutableListOf(element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.identity)
            }

        }
    }

    private fun validRotationSingleElements() {
        for (element in rationaleSingleMethods) {
            if (!element.isPublic()) throw NonPublicMethodException("${element.getFullClassName()}#${element.getName()}")
        }
        for (element in rationaleSingleMethods) {
            if (element.getAnnotation(PermissionDispatcherRationale::class.java)!!.value.isEmpty())
                throw NonPermissionException("${element.getFullClassName()}#${element.getName()}")
            if (element.getAnnotation(PermissionDispatcherRationale::class.java)!!.target.isEmpty())
                throw NonPermissionException("${element.getFullClassName()}#${element.getName()}")
            val set =
                element.getAnnotation(PermissionDispatcherRationale::class.java)!!.value.toList()
            if (!set.contains(element.getAnnotation(PermissionDispatcherRationale::class.java)!!.target)) {
                throw PermissionDispatcherException("The parameter(target) can only choose one in the set${set.joinToString(prefix = "[" ,separator = "," ,postfix = "]")}.(${element.getFullClassName()}#${element.getName()})")
            }
        }
        for (element in rationaleSingleMethods) {
            if (element.getAnnotation(PermissionDispatcherRationale::class.java)!!.target.isEmpty())
                throw PermissionDispatcherException("Please specify the permissions that need to be blocked.(${element.getFullClassName()}#${element.getName()})")
        }
        for (element in rationaleSingleMethods) {
            if (element.getParameters().size != 1) throw PermissionDispatcherException("Can only have one parameter, and the type is PermissionRationale.(${element.getFullClassName()}#${element.getName()})")
            if (element.getParameters()[0].asType()
                    .toString() != "com.iwdael.permissionsdispatcher.annotation.PermissionsRationale"
            )
                throw PermissionDispatcherException("The parameter type can only be PermissionsRationale.(${element.getFullClassName()}#${element.getName()})")
        }
        val listTarget = arrayListOf<Triple<List<String>, String, Int>>()
        for (element in rationaleSingleMethods) {
            val triple = Triple(
                element.getAnnotation(PermissionDispatcherRationale::class.java)!!.value.toList(),
                element.getAnnotation(PermissionDispatcherRationale::class.java)!!.target,
                element.getAnnotation(PermissionDispatcherRationale::class.java)!!.identity
            )
            if (listTarget.contains(triple)) {
                throw PermissionDispatcherException("The same group of the same permission can only be intercepted once.(${element.getFullClassName()}#${element.getName()})")
            }
            listTarget.add(triple)
        }
    }


}