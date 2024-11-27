package com.iwdael.permissionsdispatcher.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.iwdael.kotlinsymbolprocessor.KSPFunction
import com.iwdael.kotlinsymbolprocessor.asTypeName
import com.iwdael.kotlinsymbolprocessor.asParameter
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * @author iwdael
 * @since 2024/8/21
 * @desc this is PermissionGenerator
 */
private const val CLASS_PERMISSION_CALLBACK = "com.iwdael.permissionsdispatcher.PermissionCallback"
private const val CLASS_PERMISSION_RATIONALE = "com.iwdael.permissionsdispatcher.annotation.PermissionsRationale"
private const val CLASS_PERMISSION = "com.iwdael.permissionsdispatcher.Permission"
private const val ARRAY_OF = "arrayOf"

class PermissionGenerator(private val permission: Permission) {
    fun write(codeGenerator: CodeGenerator) {
        FileSpec.builder(permission.packageName, permission.targetName)
            .indent("    ")
            .addImport("androidx.activity.result.contract", "ActivityResultContracts")
            .addImport("com.iwdael.permissionsdispatcher", "checkRequestPermission")
            .addImport("com.iwdael.permissionsdispatcher", "showRequestPermissionRationale")
            .addImport("com.iwdael.permissionsdispatcher", "registerPermissionLauncher")
            .apply { injectCode().invoke(this) }
            .build()
            .writeTo(codeGenerator, false)
    }

    private fun injectCode(): (FileSpec.Builder.() -> Unit) = { permission.need.forEach { injectNeed(it).invoke(this) } }

    private fun injectNeed(func: KSPFunction): (FileSpec.Builder.() -> Unit) = {
        val rationale = permission.rationale.groups(func)
        if (rationale.isEmpty()) {
            injectPermission(false, 0, func, func, null).invoke(this)
        } else {
            rationale.forEachIndexed { index, curFunc ->
                injectPermission(true, index, func, curFunc, rationale.getOrNull(index + 1)).invoke(this)
            }
        }
    }

    private fun injectPermission(isSingle: Boolean, index: Int, func: KSPFunction, curFunc: KSPFunction, nextFunc: KSPFunction?): (FileSpec.Builder.() -> Unit) = {
        if (isSingle)
            addProperty(
                PropertySpec.builder(curFunc.permissionName, Array::class.asTypeName().plusParameter(CLASS_PERMISSION.asTypeName()))
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(ARRAY_OF.funInvoke(curFunc.targetPermissionValue(func))).build()
            )
        else
            addProperty(
                PropertySpec.builder(curFunc.permissionName, Array::class.asTypeName().plusParameter(CLASS_PERMISSION.asTypeName()))
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(ARRAY_OF.funInvoke(curFunc.permissionValue)).build()
            )


        val funName = if (index == 0) "${func.name}WidthPermission" else "${curFunc.name}WidthPermission"
        addFunction(
            FunSpec.builder(funName)
                .apply { if (index != 0) addModifiers(KModifier.PRIVATE) }
                .apply { injectFunction(isSingle, func, curFunc, nextFunc).invoke(this) }
                .build()
        )
    }


    private fun injectFunction(isSingle: Boolean, func: KSPFunction, curFunc: KSPFunction, nextFunc: KSPFunction?): (FunSpec.Builder.() -> Unit) = {
        receiver(permission.kspClass.asTypeName())
        addParameters(func.kspParameters.map { it.asParameter() })
        addStatement("val requestPermission = checkRequestPermission(*%N)", curFunc.permissionName)
        beginControlFlow("if (requestPermission.isEmpty())")
        addStatement((nextFunc?.name?.let { "${it}WidthPermission" } ?: func.name).funInvoke(*func.kspParameters.map { it.name }.toTypedArray()))
        nextControlFlow("else")
        addStatement(
            "val permissionLauncher = registerPermissionLauncher(%L)",
            TypeSpec.anonymousClassBuilder()
                .superclass(CLASS_PERMISSION_CALLBACK.asTypeName())
                .addFunction(
                    FunSpec.builder("onPermissionResult")
                        .apply { injectAnonymous(isSingle, func, curFunc, nextFunc).invoke(this) }
                        .build()
                )
                .build()
        )
        val rationale = permission.rationales.groups(curFunc).firstOrNull()
        if (rationale != null || isSingle) {
            addStatement(
                "%N(%L)",
                rationale?.name ?: curFunc.name,
                TypeSpec.anonymousClassBuilder()
                    .addSuperinterface(CLASS_PERMISSION_RATIONALE.asTypeName())
                    .addFunction(
                        FunSpec
                            .builder("apply")
                            .addModifiers(KModifier.OVERRIDE)
                            .addStatement("permissionLauncher.launch(requestPermission)")
                            .build()
                    )
                    .addFunction(
                        FunSpec
                            .builder("deny")
                            .addModifiers(KModifier.OVERRIDE)
                            .addStatement("permissionLauncher.unregister()")
                            .build()
                    )
                    .build()
            )
        } else {
            addStatement("permissionLauncher.launch(requestPermission)")
        }

        endControlFlow()

    }

    private fun injectAnonymous(isSingle: Boolean, func: KSPFunction, curFunc: KSPFunction, nextFunc: KSPFunction?): (FunSpec.Builder.() -> Unit) = {
        addModifiers(KModifier.OVERRIDE)
        addParameter("result", Map::class.asTypeName().parameterizedBy(String::class.asTypeName(), Boolean::class.asTypeName()))
        beginControlFlow("if (result.values.all { it })")
        addStatement((nextFunc?.name?.let { "${it}WidthPermission" } ?: func.name).funInvoke(*func.kspParameters.map { it.name }.toTypedArray()))
        nextControlFlow("else")
        val denied = permission.denied.groups(curFunc).firstOrNull()
        if (denied != null) {
            addStatement("${denied.name}(result)")
        }
        endControlFlow()
    }
}
