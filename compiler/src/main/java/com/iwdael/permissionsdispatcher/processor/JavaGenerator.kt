package com.iwdael.permissionsdispatcher.processor

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
        element.needsMethods.forEach { addPermissions(it) }
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

    override fun addRequestPermission(method: MethodElement) {
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
}