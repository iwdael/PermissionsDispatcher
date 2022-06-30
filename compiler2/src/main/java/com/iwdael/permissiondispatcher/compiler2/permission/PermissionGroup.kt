package com.iwdael.permissiondispatcher.compiler2.permission

import com.iwdael.annotationprocessorparser.Class
import com.iwdael.annotationprocessorparser.Method

class PermissionGroup(
    private val needs: Method,
    private val denied: Method?,
    private val rationales: Method?,
    private val rationale: List<Method>
)