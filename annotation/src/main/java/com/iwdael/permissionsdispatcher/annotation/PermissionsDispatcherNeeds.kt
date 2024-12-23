package com.iwdael.permissionsdispatcher.annotation
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class PermissionsDispatcherNeeds(vararg val value: Permission, val identity: Int = 0)