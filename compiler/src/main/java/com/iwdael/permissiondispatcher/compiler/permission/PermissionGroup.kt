package com.iwdael.permissiondispatcher.compiler.permission

import com.iwdael.annotationprocessorparser.Method
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class PermissionGroup(
    val needs: Method,
    val denied: Method?,
    val rationales: Method?,
    val rationale: List<Method>
)