package com.iwdael.permissiondispatcher.compiler.compat

import com.iwdael.permissiondispatcher.compiler.permission.PermissionGroup
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */

fun PermissionGroup.createKotlinFiled(): MutableList<PropertySpec> {
    if (this.rationale.isEmpty()) {
        return mutableListOf(
            PropertySpec.builder(
                this.needs.permissionFieldName(),
                ARRAY.parameterizedBy(STRING),
                KModifier.PRIVATE
            )
                .initializer("arrayOf${this.needs.permissionKotlinFiledValue()}")
                .build()
        )
    } else {
        val filed = this.rationale.map { method ->
            PropertySpec.builder(
                method.permissionFieldName(),
                ARRAY.parameterizedBy(STRING),
                KModifier.PRIVATE
            )
                .initializer("arrayOf${method.permissionKotlinFiledValue()}")
                .build()
        }.toMutableList()
        if (permissionNotDispatch().isNotEmpty()) {
            val fileSpec =
                PropertySpec.builder(
                    needs.permissionFieldName(),
                    ARRAY.parameterizedBy(STRING),
                    KModifier.PRIVATE
                )
                    .initializer(
                        "arrayOf${
                            permissionNotDispatch().joinToString(
                                separator = ", ",
                                postfix = ")",
                                prefix = "(",
                                transform = { "\"${it}\"" })
                        }"
                    )
            filed.add(fileSpec.build())
        }
        return filed
    }
}

fun PermissionGroup.createKotlinMethod(): MutableList<FunSpec> {
    return if (this.rationale.isEmpty()) mutableListOf(createNeedsMethod())
    else mutableListOf<FunSpec>()
        .apply {
            this@createKotlinMethod.rationale.forEachIndexed { index, method ->
                add(
                    this@createKotlinMethod.createRationaleMethod(
                        index,
                        index == this@createKotlinMethod.rationale.size - 1,
                        this@createKotlinMethod.permissionNotDispatch().isNotEmpty()
                    )
                )
            }
            add(this@createKotlinMethod.createNotDispatchMethod())
        }
}

private fun PermissionGroup.createNeedsMethod(): FunSpec {
    return FunSpec.builder(this.needs.permissionMethodName())
        .receiver(ClassName.bestGuess(this.needs.owner))
        .apply {
            this@createNeedsMethod.needs.parameter.forEach {
                addParameter(it.asParameterSpec().build())
            }
        }
        .beginControlFlow(
            "if(adapterPermission(*${this.needs.permissionFieldName()}).size == 0 || hasPermissions(adapterPermission(*${this.needs.permissionFieldName()})))"
        )
        .addStatement("${this.needs.name}${
            this.needs.parameter
                .joinToString(prefix = "(", postfix = ")", separator = ", ") { it.name }
        }")
        .nextControlFlow("else")
        .addStatement("val permissionFragment = getPermissionFragment() ?: return")
        .addStatement("val requestPermission = checkRequestPermission(*adapterPermission(*${this.needs.permissionFieldName()}))")
        .addCode(
            CodeBlock
                .builder()
                .beginControlFlow("permissionFragment.setPermissionCallback(object : PermissionCallback")
                .add(
                    CodeBlock
                        .builder()
                        .beginControlFlow("override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)")
                        .addStatement("if (requestCode != (${this.needs.permissionFieldName()}.hashCode() shr 16)) return")
                        .addStatement(
                            "val deniedPermissions = permissions.filterIndexed { index, _ ->  grantResults[index] == %T.PERMISSION_DENIED }",
                            ClassName.bestGuess("androidx.core.content.PermissionChecker")
                        )
                        .addStatement("val bannedResults = deniedPermissions.map { showRequestPermissionRationale(it) == false }")
                        .add(
                            CodeBlock
                                .builder()
                                .beginControlFlow("if (deniedPermissions.isEmpty())")
                                .addStatement("${this.needs.name}${
                                    this.needs.parameter
                                        .joinToString(
                                            prefix = "(",
                                            postfix = ")",
                                            separator = ", "
                                        ) { it.name }
                                }")
                                .nextControlFlow("else")
                                .apply {
                                    if (this@createNeedsMethod.denied != null)
                                        addStatement(
                                            "${this@createNeedsMethod.denied.name}(${
                                                this@createNeedsMethod.denied.parameter.map {
                                                    if (it.type == "java.util.List<java.lang.String>") "deniedPermissions" else "bannedResults"
                                                }.joinToString(", ")
                                            })"
                                        )
                                }
                                .endControlFlow()
                                .build()
                        )
                        .endControlFlow()
                        .build()
                )
                .unindent()
                .addStatement("})")
                .build()

        )

        .apply {
            if (this@createNeedsMethod.rationales == null) {
                addStatement("permissionFragment.requestPermissions(requestPermission, (${this@createNeedsMethod.needs.permissionFieldName()}.hashCode() shr 16))")
            } else {
                addCode(
                    CodeBlock.builder()
                        .beginControlFlow("${this@createNeedsMethod.rationales.name}(object :PermissionsRationale")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("override fun apply()")
                                .addStatement("permissionFragment.requestPermissions(requestPermission, (${this@createNeedsMethod.needs.permissionFieldName()}.hashCode() shr 16))")
                                .endControlFlow()
                                .build()
                        )
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("override fun deny()")
                                .addStatement("permissionFragment.setPermissionCallback(null) ")
                                .endControlFlow()
                                .build()
                        )
                        .unindent()
                        .addStatement("})")
                        .build()
                )
            }
        }
        .endControlFlow()
        .build()
}

private fun PermissionGroup.createRationaleMethod(
    index: Int,
    isLast: Boolean,
    notDispatch: Boolean
): FunSpec {
    return FunSpec.builder(
        if (index == 0)
            this.needs.permissionMethodName()
        else
            this.rationale[index - 1].permissionMethodName()
    )
        .receiver(ClassName.bestGuess(this.needs.owner))
        .apply { if (index != 0) addModifiers(KModifier.PRIVATE) }
        .apply {
            this@createRationaleMethod.needs.parameter.forEach {
                addParameter(it.asParameterSpec().build())
            }
        }
        .beginControlFlow(
            "if(adapterPermission(*${this.rationale[index].permissionFieldName()}).size == 0 || hasPermissions(adapterPermission(*${this.rationale[index].permissionFieldName()})))"
        )
        .apply {
            if (!isLast) {
                addStatement(
                    "${this@createRationaleMethod.rationale[index].permissionMethodName()}(${
                        this@createRationaleMethod.needs.parameter.joinToString(
                            separator = ", ",
                            transform = { it.name })
                    })"
                )
            } else if (notDispatch) {
                addStatement(
                    "${this@createRationaleMethod.rationale[index].permissionMethodName()}(${
                        this@createRationaleMethod.needs.parameter.joinToString(
                            separator = ", ",
                            transform = { it.name })
                    })"
                )
            } else {
                addStatement(
                    "${this@createRationaleMethod.needs.name}(${
                        this@createRationaleMethod.needs.parameter.joinToString(
                            separator = ", ",
                            transform = { it.name })
                    })"
                )
            }
        }
        .nextControlFlow("else")
        .addStatement("val permissionFragment = getPermissionFragment() ?: return")
        .addStatement("val requestPermission = checkRequestPermission(*adapterPermission(*${this.rationale[index].permissionFieldName()}))")
        .addCode(
            CodeBlock
                .builder()
                .beginControlFlow("permissionFragment.setPermissionCallback(object : PermissionCallback")
                .add(
                    CodeBlock
                        .builder()
                        .beginControlFlow("override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)")
                        .addStatement("if (requestCode != (${this.rationale[index].permissionFieldName()}.hashCode() shr 16)) return")
                        .addStatement(
                            "val deniedPermissions = permissions.filterIndexed { index, _ ->  grantResults[index] == %T.PERMISSION_DENIED }",
                            ClassName.bestGuess("androidx.core.content.PermissionChecker")
                        )
                        .addStatement("val bannedResults = deniedPermissions.map { showRequestPermissionRationale(it) == false }")
                        .add(
                            CodeBlock
                                .builder()
                                .beginControlFlow("if (deniedPermissions.isEmpty())")
                                .apply {
                                    if (!isLast) {
                                        addStatement(
                                            "${this@createRationaleMethod.rationale[index].permissionMethodName()}(${
                                                this@createRationaleMethod.needs.parameter.joinToString(
                                                    separator = ", ",
                                                    transform = { it.name })
                                            })"
                                        )
                                    } else if (notDispatch) {
                                        addStatement(
                                            "${this@createRationaleMethod.rationale[index].permissionMethodName()}(${
                                                this@createRationaleMethod.needs.parameter.joinToString(
                                                    separator = ", ",
                                                    transform = { it.name })
                                            })"
                                        )
                                    } else {
                                        addStatement(
                                            "${this@createRationaleMethod.needs.name}(${
                                                this@createRationaleMethod.needs.parameter.joinToString(
                                                    separator = ", ",
                                                    transform = { it.name })
                                            })"
                                        )
                                    }
                                }
                                .nextControlFlow("else")
                                .apply {
                                    if (this@createRationaleMethod.denied != null)
                                        addStatement(
                                            "${this@createRationaleMethod.denied.name}(${
                                                this@createRationaleMethod.denied.parameter.map {
                                                    if (it.type == "java.util.List<java.lang.String>") "deniedPermissions" else "bannedResults"
                                                }.joinToString(", ")
                                            })"
                                        )
                                }
                                .endControlFlow()
                                .build()
                        )
                        .endControlFlow()
                        .build()
                )
                .unindent()
                .addStatement("})")
                .build()

        )
        .addCode(
            CodeBlock.builder()
                .beginControlFlow("${this@createRationaleMethod.rationale[index].name}(object :PermissionsRationale")
                .add(
                    CodeBlock.builder()
                        .beginControlFlow("override fun apply()")
                        .addStatement("permissionFragment.requestPermissions(requestPermission, (${this@createRationaleMethod.rationale[index].permissionFieldName()}.hashCode() shr 16))")
                        .endControlFlow()
                        .build()
                )
                .add(
                    CodeBlock.builder()
                        .beginControlFlow("override fun deny()")
                        .addStatement("permissionFragment.setPermissionCallback(null) ")
                        .endControlFlow()
                        .build()
                )
                .unindent()
                .addStatement("})")
                .build()
        )
        .endControlFlow()
        .build()
}

private fun PermissionGroup.createNotDispatchMethod(): FunSpec {
    return FunSpec.builder(this.rationale[this.rationale.size - 1].permissionMethodName())
        .receiver(ClassName.bestGuess(this.needs.owner))
        .apply {
            this@createNotDispatchMethod.needs.parameter.forEach {
                addParameter(it.asParameterSpec().build())
            }
        }
        .addModifiers(KModifier.PRIVATE)
        .beginControlFlow(
            "if(adapterPermission(*${this.needs.permissionFieldName()}).size == 0 || hasPermissions(adapterPermission(*${this.needs.permissionFieldName()})))"
        )
        .addStatement("${this.needs.name}${
            this.needs.parameter
                .joinToString(prefix = "(", postfix = ")", separator = ", ") { it.name }
        }")
        .nextControlFlow("else")
        .addStatement("val permissionFragment = getPermissionFragment() ?: return")
        .addStatement("val requestPermission = checkRequestPermission(*adapterPermission(*${this.needs.permissionFieldName()}))")
        .addCode(
            CodeBlock
                .builder()
                .beginControlFlow("permissionFragment.setPermissionCallback(object : PermissionCallback")
                .add(
                    CodeBlock
                        .builder()
                        .beginControlFlow("override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)")
                        .addStatement("if (requestCode != (${this.needs.permissionFieldName()}.hashCode() shr 16)) return")
                        .addStatement(
                            "val deniedPermissions = permissions.filterIndexed { index, _ ->  grantResults[index] == %T.PERMISSION_DENIED }",
                            ClassName.bestGuess("androidx.core.content.PermissionChecker")
                        )
                        .addStatement("val bannedResults = deniedPermissions.map { showRequestPermissionRationale(it) == false }")
                        .add(
                            CodeBlock
                                .builder()
                                .beginControlFlow("if (deniedPermissions.isEmpty())")
                                .addStatement("${this.needs.name}${
                                    this.needs.parameter
                                        .joinToString(
                                            prefix = "(",
                                            postfix = ")",
                                            separator = ", "
                                        ) { it.name }
                                }")
                                .nextControlFlow("else")
                                .apply {
                                    if (this@createNotDispatchMethod.denied != null)
                                        addStatement(
                                            "${this@createNotDispatchMethod.denied.name}(${
                                                this@createNotDispatchMethod.denied.parameter.map {
                                                    if (it.type == "java.util.List<java.lang.String>") "deniedPermissions" else "bannedResults"
                                                }.joinToString(", ")
                                            })"
                                        )
                                }
                                .endControlFlow()
                                .build()
                        )
                        .endControlFlow()
                        .build()
                )
                .unindent()
                .addStatement("})")
                .build()

        )

        .apply {
            if (this@createNotDispatchMethod.rationales == null) {
                addStatement("permissionFragment.requestPermissions(requestPermission, (${this@createNotDispatchMethod.needs.permissionFieldName()}.hashCode() shr 16))")
            } else {
                addCode(
                    CodeBlock.builder()
                        .beginControlFlow("${this@createNotDispatchMethod.rationales.name}(object :PermissionsRationale")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("override fun apply()")
                                .addStatement("permissionFragment.requestPermissions(requestPermission, (${this@createNotDispatchMethod.needs.permissionFieldName()}.hashCode() shr 16))")
                                .endControlFlow()
                                .build()
                        )
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("override fun deny()")
                                .addStatement("permissionFragment.setPermissionCallback(null) ")
                                .endControlFlow()
                                .build()
                        )
                        .unindent()
                        .addStatement("})")
                        .build()
                )
            }
        }
        .endControlFlow()
        .build()
}

