package com.iwdael.permissiondispatcher.compiler.compat

import com.iwdael.permissiondispatcher.compiler.permission.PermissionGroup
import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
fun PermissionGroup.createJavaFiled(): MutableList<FieldSpec> {
    if (this.rationale.isEmpty()) {
        return mutableListOf(
            FieldSpec.builder(
                ArrayTypeName.get(arrayOf("").javaClass), this.needs.permissionFieldName()
            )
                .addModifiers(Modifier.FINAL)
                .addModifiers(Modifier.PRIVATE)
                .addModifiers(Modifier.STATIC)
                .initializer("new String[]${this.needs.permissionJavaFiledValue()}")
                .build()

        )
    } else {
        val filed = this.rationale.map { method ->
            FieldSpec.builder(
                ArrayTypeName.get(arrayOf("").javaClass),
                method.permissionFieldName()
            )
                .addModifiers(Modifier.FINAL)
                .addModifiers(Modifier.PRIVATE)
                .addModifiers(Modifier.STATIC)
                .initializer("new String[]${method.permissionJavaFiledValue()}")
                .build()
        }.toMutableList()
        if (permissionNotDispatch().isNotEmpty()) {
            val fileSpec =
                FieldSpec.builder(
                    ArrayTypeName.get(arrayOf("").javaClass),
                    needs.permissionFieldName()
                )
                    .addModifiers(Modifier.FINAL)
                    .addModifiers(Modifier.PRIVATE)
                    .addModifiers(Modifier.STATIC)
                    .initializer(
                        "new String[]${
                            permissionNotDispatch().joinToString(
                                separator = ",",
                                postfix = "}",
                                prefix = "{",
                                transform = { "\"${it}\"" })
                        }"
                    )
            filed.add(fileSpec.build())
        }
        return filed
    }
}

fun PermissionGroup.createJavaMethod(): MutableList<MethodSpec> {
    return if (this.rationale.isEmpty()) mutableListOf(createNeedsMethod())
    else mutableListOf<MethodSpec>()
        .apply {
            this@createJavaMethod.rationale.forEachIndexed { index, method ->
                add(
                    this@createJavaMethod.createRationaleMethod(
                        index,
                        index == this@createJavaMethod.rationale.size - 1,
                        this@createJavaMethod.permissionNotDispatch().isNotEmpty()
                    )
                )
            }
            add(this@createJavaMethod.createNotDispatchMethod())
        }
}

private fun PermissionGroup.createNeedsMethod(): MethodSpec {
    return MethodSpec.methodBuilder(this.needs.permissionMethodName())
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(ClassName.bestGuess(this.needs.owner), "target")
        .addParameters(
            this.needs.parameter
                .map { ParameterSpec.builder(ClassName.bestGuess(it.type), it.name).build() }
        )
        .beginControlFlow(
            "if (\$T.adapterPermission(${this.needs.permissionFieldName()}).length == 0 || " +
                    "\$T.hasPermissions(\$T.adapterPermission(${this.needs.permissionFieldName()})))",
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt")
        )
        .addCode(
            "target.${this.needs.name}(${
                this.needs.parameter.joinToString(
                    separator = ", ",
                    transform = { it.name })
            });\n"
        )
        .nextControlFlow("else")
        .addCode(
            "\$T permissionFragment = \$T.getPermissionFragment(target);\n",
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionFragment"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
        )
        .addCode("if (permissionFragment == null) return;\n")
        .addCode(
            "\$T[] requestPermission = \$T.checkRequestPermission(\$T.adapterPermission(${this.needs.permissionFieldName()}));\n",
            ClassName.get(String::class.java),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt")
        )
        .addCode(
            CodeBlock.builder()
                .beginControlFlow(
                    "permissionFragment.setPermissionCallback(new \$T()",
                    ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionCallback")
                )
                .add("@Override\n")
                .add(
                    CodeBlock.builder()
                        .beginControlFlow(
                            "public void onRequestPermissionsResult(int requestCode, @\$T String[] permissions, @\$T int[] grantResults)",
                            ClassName.bestGuess("androidx.annotation.NonNull"),
                            ClassName.bestGuess("androidx.annotation.NonNull")
                        )
                        .addStatement("if (requestCode != (${this.needs.permissionFieldName()}.hashCode() >> 16)) return")
                        .addStatement(
                            "\$T<\$T> deniedPermissions = new \$T<>()",
                            ClassName.bestGuess("java.util.List"),
                            ClassName.get(String::class.java),
                            ClassName.bestGuess("java.util.ArrayList"),
                        )
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("for (int index = 0; index < permissions.length; index++)")
                                .addStatement(
                                    "if (grantResults[index] == \$T.PERMISSION_DENIED) deniedPermissions.add(permissions[index])",
                                    ClassName.bestGuess("androidx.core.content.PermissionChecker")
                                )
                                .endControlFlow()
                                .build()
                        )
                        .addStatement("List<Boolean> bannedResults = new ArrayList<>()")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("for (int index = 0; index < deniedPermissions.size(); index++)")
                                .addStatement(
                                    "bannedResults.add(!\$T.showRequestPermissionRationale(target, deniedPermissions.get(index)))",
                                    ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt")
                                )
                                .endControlFlow()
                                .build()
                        )
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("if (deniedPermissions.isEmpty())")
                                .addStatement(
                                    "target.${this.needs.name}(${
                                        this.needs.parameter.joinToString(
                                            separator = ", ",
                                            transform = { it.name })
                                    })"
                                )
                                .nextControlFlow("else")
                                .apply {
                                    if (this@createNeedsMethod.denied != null) {
                                        addStatement(
                                            "target.${this@createNeedsMethod.denied.name}(${
                                                this@createNeedsMethod.denied.parameter.map {
                                                    if (it.type == "java.util.List<java.lang.String>") "deniedPermissions" else "bannedResults"
                                                }.joinToString(", ")
                                            })"
                                        )
                                    }
                                }
                                .endControlFlow()
                                .build()
                        )
                        .endControlFlow()
                        .build()
                )
                .unindent().add("});\n")
                .build()

        )
        .apply {
            if (this@createNeedsMethod.rationales == null)
                addCode("permissionFragment.requestPermissions(requestPermission, (${this@createNeedsMethod.needs.permissionFieldName()}.hashCode() >> 16));\n")
            else
                addCode(
                    CodeBlock.builder()
                        .beginControlFlow(
                            "target.${this@createNeedsMethod.rationales.name}(new \$T()",
                            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionsRationale")
                        )
                        .add("@Override\n")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("public void apply()")
                                .addStatement("permissionFragment.requestPermissions(requestPermission, ${this@createNeedsMethod.needs.permissionFieldName()}.hashCode() >> 16)")
                                .endControlFlow()
                                .build()
                        )
                        .add("@Override\n")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("public void deny()")
                                .addStatement("permissionFragment.setPermissionCallback(null)")
                                .endControlFlow()
                                .build()
                        )
                        .unindent()
                        .add("});\n")
                        .build()
                )
        }
        .endControlFlow()
        .build()


}

private fun PermissionGroup.createRationaleMethod(
    index: Int,
    isLast: Boolean,
    notDispatch: Boolean
): MethodSpec {
    return MethodSpec.methodBuilder(
        if (index == 0) this.needs.permissionMethodName()
        else this.rationale[index - 1].permissionMethodName()
    )
        .addModifiers(Modifier.STATIC)
        .apply {
            if (index == 0) addModifiers(Modifier.PUBLIC)
            else addModifiers(Modifier.PRIVATE)
        }
        .addParameter(ClassName.bestGuess(this.needs.owner), "target")
        .addParameters(
            this.needs.parameter
                .map { ParameterSpec.builder(ClassName.bestGuess(it.type), it.name).build() }
        )
        .beginControlFlow(
            "if (\$T.adapterPermission(${this.rationale[index].permissionFieldName()}).length == 0 || " +
                    "\$T.hasPermissions(\$T.adapterPermission(${this.rationale[index].permissionFieldName()})))",
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt")
        )
        .apply {
            if (!isLast) {
                addCode(
                    "${this@createRationaleMethod.rationale[index].permissionMethodName()}(${
                        this@createRationaleMethod.needs.parameter.joinToString(
                            prefix = "target",
                            separator = "",
                            transform = { ", " + it.name })
                    });\n"
                )
            } else if (notDispatch) {
                addCode(
                    "${this@createRationaleMethod.rationale[index].permissionMethodName()}(${
                        this@createRationaleMethod.needs.parameter.joinToString(
                            prefix = "target",
                            separator = "",
                            transform = { ", " + it.name })
                    });\n"
                )
            } else {
                addCode(
                    "target.${this@createRationaleMethod.needs.name}(${
                        this@createRationaleMethod.needs.parameter.joinToString(
                            separator = ", ",
                            transform = { it.name })
                    });\n"
                )
            }
        }
        .nextControlFlow("else")
        .addCode(
            "\$T permissionFragment = \$T.getPermissionFragment(target);\n",
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionFragment"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
        )
        .addCode("if (permissionFragment == null) return;\n")
        .addCode(
            "\$T[] requestPermission = \$T.checkRequestPermission(\$T.adapterPermission(${this.rationale[index].permissionFieldName()}));\n",
            ClassName.get(String::class.java),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt")
        )
        .addCode(
            CodeBlock.builder()
                .beginControlFlow(
                    "permissionFragment.setPermissionCallback(new \$T()",
                    ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionCallback")
                )
                .add("@Override\n")
                .add(
                    CodeBlock.builder()
                        .beginControlFlow(
                            "public void onRequestPermissionsResult(int requestCode, @\$T String[] permissions, @\$T int[] grantResults)",
                            ClassName.bestGuess("androidx.annotation.NonNull"),
                            ClassName.bestGuess("androidx.annotation.NonNull")
                        )
                        .addStatement("if (requestCode != (${this.rationale[index].permissionFieldName()}.hashCode() >> 16)) return")
                        .addStatement(
                            "\$T<\$T> deniedPermissions = new \$T<>()",
                            ClassName.bestGuess("java.util.List"),
                            ClassName.get(String::class.java),
                            ClassName.bestGuess("java.util.ArrayList"),
                        )
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("for (int index = 0; index < permissions.length; index++)")
                                .addStatement(
                                    "if (grantResults[index] == \$T.PERMISSION_DENIED) deniedPermissions.add(permissions[index])",
                                    ClassName.bestGuess("androidx.core.content.PermissionChecker")
                                )
                                .endControlFlow()
                                .build()
                        )
                        .addStatement("List<Boolean> bannedResults = new ArrayList<>()")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("for (int index = 0; index < deniedPermissions.size(); index++)")
                                .addStatement(
                                    "bannedResults.add(!\$T.showRequestPermissionRationale(target, deniedPermissions.get(index)))",
                                    ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt")
                                )
                                .endControlFlow()
                                .build()
                        )
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("if (deniedPermissions.isEmpty())")
                                .apply {
                                    if (!isLast) {
                                        add(
                                            "${this@createRationaleMethod.rationale[index].permissionMethodName()}(${
                                                this@createRationaleMethod.needs.parameter.joinToString(
                                                    prefix = "target",
                                                    separator = "",
                                                    transform = { ", " + it.name })
                                            });\n"
                                        )
                                    } else if (notDispatch) {
                                        add(
                                            "${this@createRationaleMethod.rationale[index].permissionMethodName()}(${
                                                this@createRationaleMethod.needs.parameter.joinToString(
                                                    prefix = "target",
                                                    separator = "",
                                                    transform = { ", " + it.name })
                                            });\n"
                                        )
                                    } else {
                                        add(
                                            "target.${this@createRationaleMethod.needs.name}(${
                                                this@createRationaleMethod.needs.parameter.joinToString(
                                                    separator = ", ",
                                                    transform = { it.name })
                                            });\n"
                                        )
                                    }
                                }
                                .nextControlFlow("else")
                                .apply {
                                    if (this@createRationaleMethod.denied != null) {
                                        addStatement(
                                            "target.${this@createRationaleMethod.denied.name}(${
                                                this@createRationaleMethod.denied.parameter.map {
                                                    if (it.type == "java.util.List<java.lang.String>") "deniedPermissions" else "bannedResults"
                                                }.joinToString(", ")
                                            })"
                                        )
                                    }
                                }
                                .endControlFlow()
                                .build()
                        )
                        .endControlFlow()
                        .build()
                )
                .unindent().add("});\n")
                .build()

        )
        .apply {

            addCode(
                CodeBlock.builder()
                    .beginControlFlow(
                        "target.${this@createRationaleMethod.rationale[index].name}(new \$T()",
                        ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionsRationale")
                    )
                    .add("@Override\n")
                    .add(
                        CodeBlock.builder()
                            .beginControlFlow("public void apply()")
                            .addStatement("permissionFragment.requestPermissions(requestPermission, ${this@createRationaleMethod.rationale[index].permissionFieldName()}.hashCode() >> 16)")
                            .endControlFlow()
                            .build()
                    )
                    .add("@Override\n")
                    .add(
                        CodeBlock.builder()
                            .beginControlFlow("public void deny()")
                            .addStatement("permissionFragment.setPermissionCallback(null)")
                            .endControlFlow()
                            .build()
                    )
                    .unindent()
                    .add("});\n")
                    .build()
            )
        }
        .endControlFlow()
        .build()


}

private fun PermissionGroup.createNotDispatchMethod(): MethodSpec {
    return MethodSpec.methodBuilder(this.rationale.last().permissionMethodName())
        .addModifiers(Modifier.STATIC)
        .addModifiers(Modifier.PRIVATE)
        .addParameter(ClassName.bestGuess(this.needs.owner), "target")
        .addParameters(
            this.needs.parameter
                .map { ParameterSpec.builder(ClassName.bestGuess(it.type), it.name).build() }
        )
        .beginControlFlow(
            "if (\$T.adapterPermission(${this.needs.permissionFieldName()}).length == 0 || " +
                    "\$T.hasPermissions(\$T.adapterPermission(${this.needs.permissionFieldName()})))",
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt")
        )
        .addCode(
            "target.${this@createNotDispatchMethod.needs.name}(${
                this@createNotDispatchMethod.needs.parameter.joinToString(
                    separator = ", ",
                    transform = { it.name })
            });\n"
        )
        .nextControlFlow("else")
        .addCode(
            "\$T permissionFragment = \$T.getPermissionFragment(target);\n",
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionFragment"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
        )
        .addCode("if (permissionFragment == null) return;\n")
        .addCode(
            "\$T[] requestPermission = \$T.checkRequestPermission(\$T.adapterPermission(${this.needs.permissionFieldName()}));\n",
            ClassName.get(String::class.java),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt"),
            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.UtilKt")
        )
        .addCode(
            CodeBlock.builder()
                .beginControlFlow(
                    "permissionFragment.setPermissionCallback(new \$T()",
                    ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionCallback")
                )
                .add("@Override\n")
                .add(
                    CodeBlock.builder()
                        .beginControlFlow(
                            "public void onRequestPermissionsResult(int requestCode, @\$T String[] permissions, @\$T int[] grantResults)",
                            ClassName.bestGuess("androidx.annotation.NonNull"),
                            ClassName.bestGuess("androidx.annotation.NonNull")
                        )
                        .addStatement("if (requestCode != (${this.needs.permissionFieldName()}.hashCode() >> 16)) return")
                        .addStatement(
                            "\$T<\$T> deniedPermissions = new \$T<>()",
                            ClassName.bestGuess("java.util.List"),
                            ClassName.get(String::class.java),
                            ClassName.bestGuess("java.util.ArrayList"),
                        )
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("for (int index = 0; index < permissions.length; index++)")
                                .addStatement(
                                    "if (grantResults[index] == \$T.PERMISSION_DENIED) deniedPermissions.add(permissions[index])",
                                    ClassName.bestGuess("androidx.core.content.PermissionChecker")
                                )
                                .endControlFlow()
                                .build()
                        )
                        .addStatement("List<Boolean> bannedResults = new ArrayList<>()")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("for (int index = 0; index < deniedPermissions.size(); index++)")
                                .addStatement(
                                    "bannedResults.add(!\$T.showRequestPermissionRationale(target, deniedPermissions.get(index)))",
                                    ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt")
                                )
                                .endControlFlow()
                                .build()
                        )
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("if (deniedPermissions.isEmpty())")
                                .add(
                                    "target.${this@createNotDispatchMethod.needs.name}(${
                                        this@createNotDispatchMethod.needs.parameter.joinToString(
                                            separator = ", ",
                                            transform = { it.name })
                                    });\n"
                                )
                                .nextControlFlow("else")
                                .apply {
                                    if (this@createNotDispatchMethod.denied != null) {
                                        addStatement(
                                            "target.${this@createNotDispatchMethod.denied.name}(${
                                                this@createNotDispatchMethod.denied.parameter.map {
                                                    if (it.type == "java.util.List<java.lang.String>") "deniedPermissions" else "bannedResults"
                                                }.joinToString(", ")
                                            })"
                                        )
                                    }
                                }
                                .endControlFlow()
                                .build()
                        )
                        .endControlFlow()
                        .build()
                )
                .unindent().add("});\n")
                .build()

        )
        .apply {
            if (this@createNotDispatchMethod.rationales == null) {
                addCode("permissionFragment.requestPermissions(requestPermission, ${this@createNotDispatchMethod.needs.permissionFieldName()}.hashCode() >> 16);\n")
            } else {
                addCode(
                    CodeBlock.builder()
                        .beginControlFlow(
                            "target.${this@createNotDispatchMethod.rationales.name}(new \$T()",
                            ClassName.bestGuess("com.iwdael.permissionsdispatcher.dispatcher.PermissionsRationale")
                        )
                        .add("@Override\n")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("public void apply()")
                                .addStatement("permissionFragment.requestPermissions(requestPermission, ${this@createNotDispatchMethod.needs.permissionFieldName()}.hashCode() >> 16)")
                                .endControlFlow()
                                .build()
                        )
                        .add("@Override\n")
                        .add(
                            CodeBlock.builder()
                                .beginControlFlow("public void deny()")
                                .addStatement("permissionFragment.setPermissionCallback(null)")
                                .endControlFlow()
                                .build()
                        )
                        .unindent()
                        .add("});\n")
                        .build()
                )
            }
        }
        .endControlFlow()
        .build()


}

