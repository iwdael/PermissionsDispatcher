package com.iwdael.permissionsdispatcher.processor

import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale
import java.lang.StringBuilder

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */

fun String.javaFullClass2KotlinShotClass(): String {
    return when {
        this == "java.lang.String" -> "String"
        this == "java.lang.String[]" -> "Array<String>"
        this == "int" -> "Int"
        this == "int[]" -> "IntArray"
        this == "boolean" -> "Boolean"
        this == "boolean[]" -> "BooleanArray"
        this == "float" -> "Float"
        this == "float[]" -> "FloatArray"
        this == "long" -> "Long"
        this == "long[]" -> "LongArray"
        this == "double" -> "Double"
        this == "double[]" -> "DoubleArray"
        this == "short" -> "Short"
        this == "short[]" -> "ShortArray"
        this == "byte" -> "Byte"
        this == "byte[]" -> "ByteArray"
        this == "char" -> "Char"
        this == "char[]" -> "CharArray"
        this.endsWith("[]") -> "Array<${this.replace("[]", "")}>".javaFullClass2KotlinShotClass()
        this.contains("java.util.") -> this.replace("java.util.", "")
            .javaFullClass2KotlinShotClass()
        this.contains("java.lang.") -> this.replace("java.lang.", "")
            .javaFullClass2KotlinShotClass()
        this.contains("Integer") -> this.replace("Integer", "Int").javaFullClass2KotlinShotClass()
        else -> this
    }
}

fun String.javaFullClass2JavaShotClass(): String {
    return when {
        else -> this
    }
}

fun MethodElement.getPermissionVariable(): String {
    return "Permission_${this.getName()}${
        this.getParameters().map { it.simpleName.toString() }
            .joinToString(separator = "_", prefix = "_")
    }_${getAnnotationPair().second}".toUpperCase()
}

private fun MethodElement.getAnnotationPair(): Pair<List<String>, Int> {
    return when {
        this.getAnnotation(PermissionsDispatcherNeeds::class.java) != null -> {
            this.getAnnotation(PermissionsDispatcherNeeds::class.java)!!
                .let { it.value.toList() to it.identity }
        }
        this.getAnnotation(PermissionsDispatcherRationale::class.java) != null -> {
            this.getAnnotation(PermissionsDispatcherRationale::class.java)!!
                .let { it.value.toList() to it.identity }
        }
        this.getAnnotation(PermissionDispatcherRationale::class.java) != null -> {
            this.getAnnotation(PermissionDispatcherRationale::class.java)!!
                .let { it.value.toList() to it.identity }
        }
        else -> this.getAnnotation(PermissionsDispatcherDenied::class.java)!!
            .let { it.value.toList() to it.identity }
    }
}


fun MethodElement.getPermissionStrings(): String {
    return  when{
        getAnnotation(PermissionsDispatcherNeeds::class.java) !=null -> getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value
        getAnnotation(PermissionsDispatcherRationale::class.java) !=null -> getAnnotation(PermissionsDispatcherRationale::class.java)!!.value
        getAnnotation(PermissionDispatcherRationale::class.java) !=null -> getAnnotation(PermissionDispatcherRationale::class.java)!!.value
        getAnnotation(PermissionsDispatcherDenied::class.java) !=null -> getAnnotation(PermissionsDispatcherDenied::class.java)!!.value
        else -> arrayOf()
    } .joinToString(
        separator = ","
    ) { "\"$it\"" }
}
fun MethodElement.getTargetPermissionStrings(): String {
    return when{
          getAnnotation(PermissionDispatcherRationale::class.java) !=null -> getAnnotation(PermissionDispatcherRationale::class.java)!!.target.let {
              arrayOf(it)
          }
         else -> arrayOf()
    } .joinToString(
        separator = ","
    ) { "\"$it\"" }
}


fun MethodElement.getKotlinMethodDeclaredParameter(): String {
    return this.getParameters()
        .joinToString(separator = ",") {
            "${it.simpleName}: ${it.asType().toString().javaFullClass2KotlinShotClass()}"
        }
}

fun MethodElement.getJavaMethodDeclaredParameter(): String {
    return this.getParameters()
        .joinToString(separator = ",") {
            "${it.asType().toString().javaFullClass2JavaShotClass()} ${it.simpleName}"
        }
}

fun MethodElement.getMethodParameter(): String {
    return this.getParameters().map { it.simpleName.toString() }
        .joinToString(separator = ",")
}

private fun List<MethodElement>.find(element: MethodElement): MethodElement? {
    for (method in this) {
        if (method.getAnnotationPair() == element.getAnnotationPair()) return method
    }
    return null
}

fun List<MethodElement>.finds(element: MethodElement): MutableList<MethodElement> {
    val list = mutableListOf<MethodElement>()
    for (method in this) {
        if (method.getAnnotationPair() == element.getAnnotationPair()) {
            list.add(method)
        }
    }
    return list
}

fun List<MethodElement>.generateKotlinDeniedMethod(element: MethodElement): String {
    val method = find(element) ?: return "{}"
    val builder = StringBuilder()
    method.getParameters().forEach {
        if (it.asType().toString() == "java.util.List<java.lang.String>") {
            if (builder.isNotEmpty()) builder.append(", ")
            builder.append("deniedPermissions")
        }
        if (it.asType().toString() == "java.util.List<java.lang.Boolean>") {
            if (builder.isNotEmpty()) builder.append(", ")
            builder.append("bannedResults")
        }
    }
    return "${method.getName()}($builder)"
}

fun List<MethodElement>.generateJavaDeniedMethod(element: MethodElement): String {
    val method = find(element) ?: return "{}"
    val builder = StringBuilder()
    method.getParameters().forEach {
        if (it.asType().toString() == "java.util.List<java.lang.String>") {
            if (builder.isNotEmpty()) builder.append(", ")
            builder.append("deniedPermissions")
        }
        if (it.asType().toString() == "java.util.List<java.lang.Boolean>") {
            if (builder.isNotEmpty()) builder.append(", ")
            builder.append("bannedResults")
        }
    }
    return "target.${method.getName()}($builder);"
}

fun List<MethodElement>.generateKotlinRequestMethod(element: MethodElement): String {
    val method = find(element)
        ?: return "        permissionFragment.requestPermissions(requestPermission, (${element.getPermissionVariable()}.hashCode() shr 16))"
    return "        ${method.getName()}(object :PermissionsRationale{\n" +
            "            override fun apply() {\n" +
            "                permissionFragment.requestPermissions(requestPermission, (${element.getPermissionVariable()}.hashCode() shr 16))\n" +
            "            }\n" +
            "\n" +
            "            override fun deny() {\n" +
            "                permissionFragment.setPermissionCallback(null)\n" +
            "            }\n" +
            "        })"
}

fun List<MethodElement>.generateJavaRequestMethod(element: MethodElement): String {
    val method = find(element)
        ?: return "            permissionFragment.requestPermissions(requestPermission, (${element.getPermissionVariable()}.hashCode() >> 16));"
    return "            target.${method.getName()}(new PermissionsRationale() {\n" +
            "                @Override\n" +
            "                public void apply() {\n" +
            "                    permissionFragment.requestPermissions(requestPermission,${element.getPermissionVariable()}.hashCode()>>16 );\n" +
            "                }\n" +
            "\n" +
            "                @Override\n" +
            "                public void deny() {\n" +
            "                    permissionFragment.setPermissionCallback(null);\n" +
            "                }\n" +
            "            });"
}