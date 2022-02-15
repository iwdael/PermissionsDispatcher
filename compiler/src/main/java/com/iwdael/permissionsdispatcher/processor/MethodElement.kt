package com.iwdael.permissionsdispatcher.processor

import javax.lang.model.element.*

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class MethodElement(private val element: ExecutableElement) {

    fun getSimpleClassName() = element.enclosingElement.simpleName
    fun getFullClassName() = element.enclosingElement.toString()
    fun getName() = element.simpleName.toString()
    override fun toString() = "$element"
    fun <T : Annotation> getAnnotation(clazz: Class<T>): T? {
        return element.getAnnotation(clazz)
    }

    fun getParameters(): MutableList<out VariableElement> {
        return element.parameters ?: arrayListOf()
    }

    fun isPublic(): Boolean {
        return element.modifiers?.contains(Modifier.PUBLIC) == true
    }
}