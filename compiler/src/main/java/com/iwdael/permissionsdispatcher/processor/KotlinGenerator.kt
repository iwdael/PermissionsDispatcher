package com.iwdael.permissionsdispatcher.processor

import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds

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
    override fun addPermissions(k:String,v:String) {
        source.add("private val $k = arrayOf(${v})")
    }

    override fun addTargetPermissions(method: MethodElement) {
        source.add("private val ${method.getPermissionVariable()} = arrayOf(${method.getTargetPermissionStrings()})")
    }

    override fun addRequestPermission(method: MethodElement) {
        val singleRationales = element.rationaleSingleMethods.finds(method)
        if (singleRationales.isEmpty()){
            addPermissions(method)
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
        } else {
            val singlePermission = singleRationales.map { it.getAnnotation(PermissionDispatcherRationale::class.java)!!.target }
            val allPermission = method.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.toMutableList()
            allPermission.removeAll(singlePermission)

            fun getMethodName(index:Int ) = if (index!=0) singleRationales[index].getName() else method.getName()
            fun getNextMethodName(index:Int) =if (index+1<singleRationales.size) "${singleRationales[index+1].getName()}WithPermission" else if (allPermission.isEmpty()) method.getName() else "${method.getName()}WithPermissionRemainder"

            singleRationales.forEachIndexed { index, rationale ->
                addTargetPermissions(rationale)
                source.add(
                    "${if (index != 0) "private " else ""}fun ${element.targetClassName}.${getMethodName(index)}WithPermission(${method.getKotlinMethodDeclaredParameter()}) {\n" +
                            "    if (hasPermissions(*${rationale.getPermissionVariable()})) {\n" +
                            "        ${getNextMethodName(index )}(${method.getMethodParameter()})\n" +
                            "    } else {\n" +
                            "        val permissionFragment = getPermissionFragment() ?: return\n" +
                            "        val requestPermission = checkRequestPermission(*${rationale.getPermissionVariable()})\n" +
                            "        permissionFragment.setPermissionCallback(object : PermissionCallback {\n" +
                            "            override fun onRequestPermissionsResult(\n" +
                            "                requestCode: Int,\n" +
                            "                permissions: Array<out String>,\n" +
                            "                grantResults: IntArray\n" +
                            "            ) {\n" +
                            "                if (requestCode != (${rationale.getPermissionVariable()}.hashCode() shr 16)) return\n" +
                            "                val deniedPermissions = permissions.filterIndexed { index, permission ->  grantResults[index] == PERMISSION_DENIED }\n" +
                            "                val bannedResults = deniedPermissions.map { showRequestPermissionRationale(it) == false }\n" +
                            "                if (deniedPermissions.isEmpty())\n" +
                            "                    ${getNextMethodName(index)}(${method.getMethodParameter()})\n" +
                            "                else\n" +
                            "                    ${element.deniedMethods.generateKotlinDeniedMethod(method)}\n" +
                            "            }\n" +
                            "        })\n" +
                            "        ${rationale.getName()}(object :PermissionsRationale{\n" +
                            "            override fun apply() {\n" +
                            "                permissionFragment.requestPermissions(requestPermission, (${rationale.getPermissionVariable()}.hashCode() shr 16))\n" +
                            "            }\n" +
                            "\n" +
                            "            override fun deny() {\n" +
                            "                permissionFragment.setPermissionCallback(null)\n" +
                            "            }\n" +
                            "        })\n" +
                            "    }\n" +
                            "}\n\n\n"
                )

            }
            if (allPermission.isNotEmpty()){
                addPermissions(method.getPermissionVariable(),allPermission.joinToString(",") { "\"${it}\"" })
                source.add(
                    "fun ${element.targetClassName}.${method.getName()}WithPermissionRemainder(${method.getKotlinMethodDeclaredParameter()}) {\n" +
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

    }


}
