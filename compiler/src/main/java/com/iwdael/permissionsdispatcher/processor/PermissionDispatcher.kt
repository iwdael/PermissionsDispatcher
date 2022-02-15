package com.iwdael.permissionsdispatcher.processor


import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale
import com.iwdael.permissionsdispatcher.processor.exception.*

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

    init {
        validNeedsElements()
        validDeniedElements()
        validRotationElements()
    }


    private fun validNeedsElements() {
        for (element in needsMethods) {
            if (!element.isPublic()) throw NonPublicMethodException("${element.getFullClassName()}.${element.getName()}")
        }
        for (element in needsMethods) {
            if (element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.isEmpty())
                throw NonPermissionException("${element.getFullClassName()}.${element.getName()}")
        }
        val mapPermissions = mutableMapOf<List<String>, MutableList<Int>>()
        for (element in needsMethods) {
            if (mapPermissions.containsKey(element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.toList())) {
                val identities =
                    mapPermissions[element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.toList()]!!
                if (identities.contains(element.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.identity))
                    throw NonIdentityException("${element.getFullClassName()}.${element.getName()}")
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
            if (!element.isPublic()) throw NonPublicMethodException("${element.getFullClassName()}.${element.getName()}")
        }
        for (element in deniedMethods) {
            if (element.getAnnotation(PermissionsDispatcherDenied::class.java)!!.value.isEmpty())
                throw NonPermissionException("${element.getFullClassName()}.${element.getName()}")
        }
        for (element in deniedMethods) {
            if (element.getParameters().size > 2) throw NumberOfParametersExceedException(
                2,
                "${element.getFullClassName()}.${element.getName()}"
            )
            for (type in element.getParameters().map { it.asType() }) {
                if (type.toString() != "java.util.List<java.lang.String>" && type.toString() != "java.util.List<java.lang.Boolean>") {
                    throw TypeParameterException(
                        "java.util.List<java.lang.String>",
                        "java.util.List<java.lang.Boolean>",
                        "${element.getFullClassName()}.${element.getName()}"
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
                    "${element.getFullClassName()}.${element.getName()}"
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
            if (!element.isPublic()) throw NonPublicMethodException("${element.getFullClassName()}.${element.getName()}")
        }
        for (element in rationaleMethods) {
            if (element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.value.isEmpty())
                throw NonPermissionException("${element.getFullClassName()}.${element.getName()}")
        }
        for (element in rationaleMethods) {
            if (element.getParameters().size != 1) throw PermissionDispatcherException("Can only have one parameter, and the type is PermissionsRationale.(${element.getFullClassName()}.${element.getName()})")
            if (element.getParameters()[0].asType()
                    .toString() != "com.iwdael.permissionsdispatcher.annotation.PermissionsRationale"
            )
                throw PermissionDispatcherException("The parameter type can only be PermissionsRationale.(${element.getFullClassName()}.${element.getName()})")
        }
        val mapPermissions = mutableMapOf<List<String>, MutableList<Int>>()
        for (element in rationaleMethods) {
            if (mapPermissions.containsKey(element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.value.toList())) {
                val identities =
                    mapPermissions[element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.value.toList()]!!
                if (identities.contains(element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.identity)) throw NonIdentityException(
                    "${element.getFullClassName()}.${element.getName()}"
                )else{
                    identities.add(element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.identity)
                }
            } else {
                mapPermissions[element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.value.toList()] =
                    mutableListOf(element.getAnnotation(PermissionsDispatcherRationale::class.java)!!.identity)
            }

        }
    }

}