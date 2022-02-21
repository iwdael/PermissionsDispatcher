package com.iwdael.permissionsdispatcher.annotation
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class PermissionDispatcherRationale(vararg val value: String, val target:String, val identity: Int = 0)