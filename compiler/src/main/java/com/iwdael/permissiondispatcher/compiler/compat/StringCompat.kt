package com.iwdael.permissiondispatcher.compiler.compat

import com.iwdael.annotationprocessorparser.Parameter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
fun Parameter.kotlinType(): TypeName {
    return when {
        this.type == "java.lang.String" -> STRING
        this.type == "java.lang.String[]" -> ARRAY.parameterizedBy(STRING)
        this.type == "int" -> INT
        this.type == "int[]" -> INT_ARRAY
        this.type == "boolean" -> BOOLEAN
        this.type == "boolean[]" -> BOOLEAN_ARRAY
        this.type == "float" -> FLOAT
        this.type == "float[]" -> FLOAT
        this.type == "long" -> LONG
        this.type == "long[]" -> LONG_ARRAY
        this.type == "double" -> DOUBLE
        this.type == "double[]" -> DOUBLE_ARRAY
        this.type == "short" -> SHORT
        this.type == "short[]" -> SHORT_ARRAY
        this.type == "byte" -> BYTE
        this.type == "byte[]" -> BOOLEAN_ARRAY
        this.type == "char" -> CHAR
        this.type == "char[]" -> CHAR
        this.type.endsWith("[]") -> ARRAY.parameterizedBy(ClassName.bestGuess(this.type.replace("[]","")))
        else -> {
            val type = this.type
                .replace("java.lang","kotlin")
                .replace("java.util","kotlin")
                .replace("Integer","Int")
            ClassName.bestGuess(type)
        }
    }
}
