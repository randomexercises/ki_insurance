package com.ki

import java.io.File
import java.io.IOException

object Fixture {
    fun getPath(filename: String): String {
        var selfPath: String? = null
        try {
            selfPath = File(".").canonicalPath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "$selfPath/src/test/fixtures/$filename"
    }
}