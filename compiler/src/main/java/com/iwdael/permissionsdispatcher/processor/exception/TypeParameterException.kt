package com.iwdael.permissionsdispatcher.processor.exception

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class TypeParameterException(type1: String, type2: String, method: String) :
    Exception("The parameter type must be $type1 or $type2 ($method)")