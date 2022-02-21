package com.iwdael.permissionsdispatcher.processor

import com.iwdael.permissionsdispatcher.annotation.PermissionDispatcherRationale
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class JavaGenerator(val element: PermissionDispatcher) : Generator {
    private val source = arrayListOf<String>()

    override fun generate(): String {
        addPackage(element.packageName)
        addSpaceLine()
        addImport("com.iwdael.permissionsdispatcher.dispatcher.PermissionCallback")
        addImport("com.iwdael.permissionsdispatcher.dispatcher.PermissionFragment")
        addImport("com.iwdael.permissionsdispatcher.annotation.PermissionsRationale")
        addImport("static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale")
        addImport("static androidx.core.content.PermissionChecker.PERMISSION_DENIED")
        addImport("static com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt.checkRequestPermission")
        addImport("static com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt.getPermissionFragment")
        addImport("static com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt.hasPermissions")
        addImport("static com.iwdael.permissionsdispatcher.dispatcher.ContextCompatKt.showRequestPermissionRationale")
        addImport("org.jetbrains.annotations.NotNull")
        addImport("java.util.ArrayList")
        addImport("java.util.List")
        addSpaceLine()
        addClassTop()

        element.needsMethods.forEach {
            addRequestPermission(it)
            addSpaceLine()
        }
        addClassBottom()
        return source.joinToString(separator = "\n")
    }

    private fun addClassTop() {
        source.add("public class ${element.targetClassName}PermissionDispatcher {\n")
    }

    private fun addClassBottom() {
        source.add("}\n")
    }

    override fun addPackage(packageName: String) {
        source.add("package $packageName;")
    }

    override fun addSpaceLine() {
        source.add("")
    }

    override fun addImport(importName: String) {
        source.add("import $importName;")
    }

    override fun addPermissions(method: MethodElement) {
        source.add("    private static final String[] ${method.getPermissionVariable()} = new String[]{${method.getPermissionStrings()}};")
    }
    override fun addPermissions(k:String,v:String) {
        source.add("    private static final String[] ${k} = new String[]{${v}};")
    }
    override fun addTargetPermissions(method: MethodElement) {
        source.add("    private static final String[] ${method.getPermissionVariable()} = new String[]{${method.getTargetPermissionStrings()}};")
    }

    override fun addRequestPermission(method: MethodElement) {
        val singleRationales = element.rationaleSingleMethods.finds(method)
        if (singleRationales.isEmpty()){
            addPermissions(method)
            source.add(
                "    public static void ${method.getName()}WithPermission(${element.targetClassName} target, ${method.getJavaMethodDeclaredParameter()}) {\n" +
                        "        if (hasPermissions(target, ${method.getPermissionVariable()})) {\n" +
                        "            target.${method.getName()}(${method.getMethodParameter()});\n" +
                        "        } else {\n" +
                        "            PermissionFragment permissionFragment = getPermissionFragment(target);\n" +
                        "            if (permissionFragment == null) return;\n" +
                        "            String[] requestPermission = checkRequestPermission(target, ${method.getPermissionVariable()});\n" +
                        "            permissionFragment.setPermissionCallback(new PermissionCallback() {\n" +
                        "                @Override\n" +
                        "                public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {\n" +
                        "                    if (requestCode != (${method.getPermissionVariable()}.hashCode() >> 16)) return;\n" +
                        "                    List<String> deniedPermissions = new ArrayList<>();\n" +
                        "                    for (int index = 0; index < permissions.length; index++) {\n" +
                        "                        if (grantResults[index] == PERMISSION_DENIED)\n" +
                        "                            deniedPermissions.add(permissions[index]);\n" +
                        "                    }\n" +
                        "                    List<Boolean> bannedResults = new ArrayList<>();\n" +
                        "                    for (int index = 0; index < deniedPermissions.size(); index++) {\n" +
                        "                        bannedResults.add(!showRequestPermissionRationale(target, deniedPermissions.get(index)));\n" +
                        "                    }\n" +
                        "                    if (deniedPermissions.isEmpty())\n" +
                        "                        target.${method.getName()}(${method.getMethodParameter()});\n" +
                        "                    else\n" +
                        "                        ${element.deniedMethods.generateJavaDeniedMethod(method)}\n" +
                        "                }\n" +
                        "            });\n" +
                        "${element.rationaleMethods.generateJavaRequestMethod(method)}\n" +
                        "        }\n" +
                        "    }"
            )
        }
        else {
            val singlePermission = singleRationales.map { it.getAnnotation(
                PermissionDispatcherRationale::class.java)!!.target }
            val allPermission = method.getAnnotation(PermissionsDispatcherNeeds::class.java)!!.value.toMutableList()
            allPermission.removeAll(singlePermission)
            fun getMethodName(index:Int ) = if (index!=0) singleRationales[index].getName() else method.getName()
            fun getNextMethodName(index:Int) =if (index+1<singleRationales.size) "${singleRationales[index+1].getName()}WithPermission" else if (allPermission.isEmpty()) method.getName() else "${method.getName()}WithPermissionRemainder"
            fun isOuterMethod(index:Int) =allPermission.isEmpty() && index == singleRationales.size-1
            singleRationales.forEachIndexed { index, rationale ->
                addTargetPermissions(rationale)
                source.add(
                    "    public static void ${getMethodName(index)}WithPermission(${element.targetClassName} target, ${method.getJavaMethodDeclaredParameter()}) {\n" +
                            "        if (hasPermissions(target, ${rationale.getPermissionVariable()})) {\n" +
                            "            ${if (isOuterMethod(index)) "target." else ""}${getNextMethodName(index)}(${if (isOuterMethod(index)) "" else "target " }${if (method.getMethodParameter().trim().isNotEmpty() &&  isOuterMethod(index)) "" else ","}${method.getMethodParameter()});\n" +
                            "        } else {\n" +
                            "            PermissionFragment permissionFragment = getPermissionFragment(target);\n" +
                            "            if (permissionFragment == null) return;\n" +
                            "            String[] requestPermission = checkRequestPermission(target, ${rationale.getPermissionVariable()});\n" +
                            "            permissionFragment.setPermissionCallback(new PermissionCallback() {\n" +
                            "                @Override\n" +
                            "                public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {\n" +
                            "                    if (requestCode != (${rationale.getPermissionVariable()}.hashCode() >> 16)) return;\n" +
                            "                    List<String> deniedPermissions = new ArrayList<>();\n" +
                            "                    for (int index = 0; index < permissions.length; index++) {\n" +
                            "                        if (grantResults[index] == PERMISSION_DENIED)\n" +
                            "                            deniedPermissions.add(permissions[index]);\n" +
                            "                    }\n" +
                            "                    List<Boolean> bannedResults = new ArrayList<>();\n" +
                            "                    for (int index = 0; index < deniedPermissions.size(); index++) {\n" +
                            "                        bannedResults.add(!showRequestPermissionRationale(target, deniedPermissions.get(index)));\n" +
                            "                    }\n" +
                            "                    if (deniedPermissions.isEmpty())\n" +
                            "                        ${if (isOuterMethod(index)) "target." else ""}${getNextMethodName(index)}(${if (isOuterMethod(index)) "" else "target " }${if (method.getMethodParameter().trim().isNotEmpty() && isOuterMethod(index)) "" else ","}${method.getMethodParameter()});\n" +
                            "                    else\n" +
                            "                        ${element.deniedMethods.generateJavaDeniedMethod(method)}\n" +
                            "                }\n" +
                            "            });\n" +
                            "            target.${rationale.getName()}(new PermissionsRationale() {\n" +
                            "                @Override\n" +
                            "                public void apply() {\n" +
                            "                    permissionFragment.requestPermissions(${rationale.getPermissionVariable()}, ${rationale.getPermissionVariable()}.hashCode()>>16 );\n" +
                            "                }\n" +
                            "\n" +
                            "                @Override\n" +
                            "                public void deny() {\n" +
                            "                    permissionFragment.setPermissionCallback(null);\n" +
                            "                }\n" +
                            "            });\n" +
                            "        }\n" +
                            "    }"
                )
            }

            if (allPermission.isNotEmpty()){
                addPermissions(method.getPermissionVariable(), allPermission.joinToString(transform = {"\"$it\""}))
                source.add(
                    "    public static void ${method.getName()}WithPermissionRemainder(${element.targetClassName} target, ${method.getJavaMethodDeclaredParameter()}) {\n" +
                            "        if (hasPermissions(target, ${method.getPermissionVariable()})) {\n" +
                            "            target.${method.getName()}(${method.getMethodParameter()});\n" +
                            "        } else {\n" +
                            "            PermissionFragment permissionFragment = getPermissionFragment(target);\n" +
                            "            if (permissionFragment == null) return;\n" +
                            "            String[] requestPermission = checkRequestPermission(target, ${method.getPermissionVariable()});\n" +
                            "            permissionFragment.setPermissionCallback(new PermissionCallback() {\n" +
                            "                @Override\n" +
                            "                public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {\n" +
                            "                    if (requestCode != (${method.getPermissionVariable()}.hashCode() >> 16)) return;\n" +
                            "                    List<String> deniedPermissions = new ArrayList<>();\n" +
                            "                    for (int index = 0; index < permissions.length; index++) {\n" +
                            "                        if (grantResults[index] == PERMISSION_DENIED)\n" +
                            "                            deniedPermissions.add(permissions[index]);\n" +
                            "                    }\n" +
                            "                    List<Boolean> bannedResults = new ArrayList<>();\n" +
                            "                    for (int index = 0; index < deniedPermissions.size(); index++) {\n" +
                            "                        bannedResults.add(!showRequestPermissionRationale(target, deniedPermissions.get(index)));\n" +
                            "                    }\n" +
                            "                    if (deniedPermissions.isEmpty())\n" +
                            "                        target.${method.getName()}(${method.getMethodParameter()});\n" +
                            "                    else\n" +
                            "                        ${element.deniedMethods.generateJavaDeniedMethod(method)}\n" +
                            "                }\n" +
                            "            });\n" +
                            "${element.rationaleMethods.generateJavaRequestMethod(method)}\n" +
                            "        }\n" +
                            "    }"
                )
            }
        }
    }
}