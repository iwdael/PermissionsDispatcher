package com.iwdael.permissiondispatcher.compiler2.maker

import javax.annotation.processing.Filer

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
interface Maker {
    fun make(filer: Filer)
}