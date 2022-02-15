package com.iwdael.permissionsdispatcher.processor

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class KotlinGenerator(private val element: PermissionDispatcher) : Generator {
    private val source = arrayListOf<String>()
    override fun generate(): String {
        addPackage(element.packageName)
        addSpaceLine()
        addImport("com.iwdael.permissionsdispatcher.dispatcher.hasPermissions")
        addImport("com.iwdael.permissionsdispatcher.dispatcher.getPermissionFragment")
        addImport("com.iwdael.permissionsdispatcher.dispatcher.checkRequestPermission")
        addImport("com.iwdael.permissionsdispatcher.dispatcher.PermissionCallback")
        addImport("com.iwdael.permissionsdispatcher.annotation.PermissionsRationale")
        addImport("com.iwdael.permissionsdispatcher.dispatcher.showRequestPermissionRationale")
        addImport("androidx.core.content.PermissionChecker.PERMISSION_DENIED")
        addSpaceLine()
        element.needsMethods.forEach { addPermissions(it) }
        element.needsMethods.forEach {
            addRequestPermission(it)
            addSpaceLine()
        }
        return source.joinToString(separator = "\n")
    }

    override fun addPackage(packageName: String) {
        source.add("package $packageName")
    }

    override fun addSpaceLine() {
        source.add("")
    }

    override fun addImport(importName: String) {
        source.add("import $importName")
    }

    override fun addPermissions(method: MethodElement) {
        source.add("private val ${method.getPermissionVariable()} = arrayOf(${method.getPermissionStrings()})")
    }

    override fun addRequestPermission(method: MethodElement) {

        source.add(
            "fun ${element.targetClassName}.${method.getName()}WithPermission(${method.getKotlinMethodDeclaredParameter()}) {\n" +
                    "    if (hasPermissions(*${method.getPermissionVariable()})) {\n" +
                    "        ${method.getName()}(${method.getMethodParameter()})\n" +
                    "    } else {\n" +
                    "        val permissionFragment = getPermissionFragment() ?: return\n" +
                    "        val requestPermission = checkRequestPermission(*${method.getPermissionVariable()})\n" +
                    "        permissionFragment.setPermissionCallback(object : PermissionCallback {\n" +
                    "            override fun onRequestPermissionsResult(\n" +
                    "                requestCode: Int,\n" +
                    "                permissions: Array<out String>,\n" +
                    "                grantResults: IntArray\n" +
                    "            ) {\n" +
                    "                if (requestCode != (${method.getPermissionVariable()}.hashCode() shr 16)) return\n" +
                    "                val deniedPermissions = permissions.filterIndexed { index, permission ->  grantResults[index] == PERMISSION_DENIED }\n" +
                    "                val bannedResults = deniedPermissions.map { showRequestPermissionRationale(it) == false }\n" +
                    "                if (deniedPermissions.isEmpty())\n" +
                    "                    ${method.getName()}(${method.getMethodParameter()})\n" +
                    "                else\n" +
                    "                    ${element.deniedMethods.generateKotlinDeniedMethod(method)}\n" +
                    "            }\n" +
                    "        })\n" +
                    "${element.rationaleMethods.generateKotlinRequestMethod(method)}\n" +
                    "    }\n" +
                    "}"
        )
    }


}
//Banned