package com.iwdael.permissiondispatcher.compiler.compat

import com.iwdael.annotationprocessorparser.Parameter
import com.squareup.javapoet.*
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
fun Parameter.asFieldSpec(): FieldSpec.Builder {
    return FieldSpec.builder(TypeName.get(this.e.asType()), this.name)
}

fun Parameter.simpleType(): String {
    return if (this.type.contains("("))
        this.type.substring(0, this.type.indexOf("("))
    else if (this.type.contains("["))
        this.type.substring(0, this.type.indexOf("["))
    else if (this.type.contains("<"))
        this.type.substring(0, this.type.indexOf("<"))
    else if (this.type.contains("{"))
        this.type.substring(0, this.type.indexOf("{"))
    else this.type
}