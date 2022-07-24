package com.iwdael.permissiondispatcher.compiler.compat

import com.iwdael.annotationprocessorparser.Parameter
import com.squareup.kotlinpoet.*
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
fun Parameter.asParameterSpec(): ParameterSpec.Builder {
    return ParameterSpec.builder(this.name, this.kotlinType())

}