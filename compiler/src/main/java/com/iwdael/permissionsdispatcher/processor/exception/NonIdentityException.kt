package com.iwdael.permissionsdispatcher.processor.exception

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class NonIdentityException(method: String) :
    Exception("Please add a different identity to distinguish the same permission applied for($method)")