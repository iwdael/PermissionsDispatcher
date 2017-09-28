package com.iwdael.permissionsdispatcher.processor

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */

class ParameterExp(method: String) :
    Exception("The parameters of this method are incorrect, and it is recommended that you modify them as follows: $method(result: Map<String, Boolean>)")

class OutOfParameterExp(number: Int, method: String) :
    Exception("The number of parameters cannot exceed $number ($method)")

class NoPublicExp(method: String) :
    Exception("Use non-public methods(${method}), should use public method.")

class NoPermissionExp(method: String) :
    Exception("Please add the permissions you need to apply for($method)")

class NoIdExp(method: String) :
    Exception("Please add a different identity to distinguish the same permission applied for($method)")

class NoMatchNeedExp(method: String) :
    Exception("Unable to find the appropriate PermissionsDispatcherNeeds in method(${method})")

class NoParamRationaleExp(method: String) :
    Exception("Can only have one parameter, and it is recommended that you modify them as follows: $method(rationale: PermissionsRationale)")

class NoMatchTarget(set: List<String>, method: String) :
    Exception(
        "The Annotation(target) can only choose one in the set" +
                set.joinToString(prefix = "[", separator = ", ", postfix = "]") +
                ".(${method})"
    )

class SameAnnotationExp(method: String) :
    Exception("The same group of the same permission can only be intercepted once.(${method})")

class NumberSameExp(method: String) : Exception("The number of target and value must be the same on PermissionDispatcherRationale.(${method}) ")