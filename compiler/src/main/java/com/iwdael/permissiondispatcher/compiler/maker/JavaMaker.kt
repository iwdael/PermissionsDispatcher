package com.iwdael.permissiondispatcher.compiler.maker

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.permissiondispatcher.compiler.compat.createJavaFiled
import com.iwdael.permissiondispatcher.compiler.compat.createJavaMethod
import com.iwdael.permissiondispatcher.compiler.compat.hasSame
import com.iwdael.permissiondispatcher.compiler.compat.isSameGroup
import com.iwdael.permissiondispatcher.compiler.permission.PermissionGroup
import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
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
        val group = mutableListOf<PermissionGroup>()
        while (needs.isNotEmpty()) {
            val gNeed = needs.removeAt(0)
            val gDenied =
                denies.filter {
                    gNeed.getAnnotation(PermissionsDispatcherNeeds::class.java)!!
                        .isSameGroup(it.getAnnotation(PermissionsDispatcherDenied::class.java)!!)
                }
            if (gDenied.size > 1) throw Exception("There can only be at most one `PermissionsDispatcherDenied` in the same set of permissions:${gNeed.owner}.${gNeed.name}")

            val gRationales = rationales.filter {
                gNeed.getAnnotation(PermissionsDispatcherNeeds::class.java)!!
                    .isSameGroup(it.getAnnotation(PermissionsDispatcherRationale::class.java)!!)
            }
            if (gDenied.size > 1) throw Exception("There can only be at most one `PermissionsDispatcherDenied` in the same set of permissions:${gNeed.owner}.${gNeed.name}")

            val gRationale = rationale.filter {
                gNeed.getAnnotation(PermissionsDispatcherNeeds::class.java)!!
                    .isSameGroup(it.getAnnotation(PermissionDispatcherRationale::class.java)!!)
            }

            if (gRationale.map { it.getAnnotation(PermissionDispatcherRationale::class.java)!! }
                    .hasSame())
                throw Exception("There can only be at most one `PermissionsDispatcherDenied` in the same set of permissions:${gNeed.owner}.${gNeed.name}")

            group.add(
                PermissionGroup(gNeed, gDenied.firstOrNull(), gRationales.firstOrNull(), gRationale)
            )
        }
        JavaFile
            .builder(
                packageGenerator,
                TypeSpec
                    .classBuilder(classNameGenerator)
                    .addModifiers(Modifier.PUBLIC)
                    .addFields(group.map { it.createJavaFiled() }.flatMap { it })
                    .addMethods(group.map { it.createJavaMethod() }.flatMap { it })
                    .build()
            )
            .build()
            .writeTo(filer)
    }
}