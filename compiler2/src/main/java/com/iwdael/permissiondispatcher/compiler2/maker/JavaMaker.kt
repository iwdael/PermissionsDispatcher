package com.iwdael.permissiondispatcher.compiler2.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.permissiondispatcher.compiler2.compat.isSameGroup
import com.iwdael.permissiondispatcher.compiler2.permission.PermissionGroup
import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class JavaMaker(private val clazz: Class) : Maker {
    private val className = clazz.name
    private val `package` = clazz.`package`.name
    private val classNameGenerator = "${className}PermissionDispatcher"
    private val packageGenerator = "${`package`}.permission.dispatcher"
    override fun make(filer: Filer) {
        val needs =
            clazz.methods
                .filter { it.getAnnotation(PermissionsDispatcherNeeds::class.java) != null }
                .toMutableList()
        val denies =
            clazz.methods
                .filter { it.getAnnotation(PermissionsDispatcherDenied::class.java) != null }
                .toMutableList()
        val rationales =
            clazz.methods
                .filter { it.getAnnotation(PermissionsDispatcherRationale::class.java) != null }
                .toMutableList()
        val rationale =
            clazz.methods
                .filter { it.getAnnotation(PermissionDispatcherRationale::class.java) != null }
                .toMutableList()

        while (needs.isNotEmpty()) {
            val gNeeds = needs.removeAt(0)
            val gDenied =
                denies.filter {
                    gNeeds.getAnnotation(PermissionsDispatcherNeeds::class.java)!!
                        .isSameGroup(it.getAnnotation(PermissionsDispatcherDenied::class.java)!!)
                }
            if (gDenied.size > 1) throw Exception("There can only be at most one `PermissionsDispatcherDenied` in the same set of permissions:${gNeeds.owner}.${gNeeds.name}")

            val gRationales = rationales.filter {
                gNeeds.getAnnotation(PermissionsDispatcherNeeds::class.java)!!
                    .isSameGroup(it.getAnnotation(PermissionsDispatcherRationale::class.java)!!)
            }
            if (gDenied.size > 1) throw Exception("There can only be at most one `PermissionsDispatcherDenied` in the same set of permissions:${gNeeds.owner}.${gNeeds.name}")

            val gRationale = rationale.filter {
                gNeeds.getAnnotation(PermissionsDispatcherNeeds::class.java)!!
                    .isSameGroup(it.getAnnotation(PermissionDispatcherRationale::class.java)!!)
            }
            PermissionGroup(gNeeds, gDenied.firstOrNull(), gRationales.firstOrNull(), gRationale)
        }
        JavaFile
            .builder(
                packageGenerator,
                TypeSpec
                    .classBuilder(classNameGenerator)
                    .addModifiers(Modifier.PUBLIC)
                    .build()
            )
            .build()
            .writeTo(filer)
    }
}