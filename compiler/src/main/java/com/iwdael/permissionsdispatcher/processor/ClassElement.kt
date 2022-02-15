package com.iwdael.permissionsdispatcher.processor

import com.sun.org.apache.xerces.internal.util.DOMUtil.getAnnotation
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class ClassElement(val element: Element) {
    fun getPackage() = element.enclosingElement.toString()
    fun getClassName() = element.simpleName.toString()
    fun getMethodElement(clazz: KClass<*>) = element.enclosedElements.filter {
        it.annotationMirrors.map { it.annotationType }.map { it.toString() }
            .contains(clazz.qualifiedName)
    }.map { MethodElement(it as ExecutableElement) }


    fun sourceFileIsKotlin() = element.getAnnotation(Metadata::class.java) != null

    override fun toString() = "${getPackage()}.${getClassName()}"
}