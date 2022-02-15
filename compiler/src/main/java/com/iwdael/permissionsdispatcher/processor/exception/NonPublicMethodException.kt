package com.iwdael.permissionsdispatcher.processor.exception

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class NonPublicMethodException(method: String) : Exception("Use non-public methods(${method}), should use public method.")