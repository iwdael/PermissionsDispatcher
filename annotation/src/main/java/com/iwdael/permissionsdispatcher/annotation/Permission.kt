package com.iwdael.permissionsdispatcher.annotation

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Permission(val value: String, val min: Int = 23, val max: Int = Int.MAX_VALUE)
