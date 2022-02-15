package com.iwdael.permissionsdispatcher.processor.exception

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class NonPermissionException(method: String) :
    Exception("Please add the permissions you need to apply for($method)")