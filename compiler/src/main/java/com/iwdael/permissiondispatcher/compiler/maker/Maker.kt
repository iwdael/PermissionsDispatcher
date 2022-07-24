package com.iwdael.permissiondispatcher.compiler.maker

import javax.annotation.processing.Filer

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
interface Maker {
    fun make(filer: Filer)
}