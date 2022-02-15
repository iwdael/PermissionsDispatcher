package com.iwdael.permissionsdispatcher.processor.exception

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
class NumberOfParametersExceedException(number: Int, method: String) :
    Exception("The number of parameters cannot exceed $number ($method)")