package rk.powermilk.gac.util

import android.util.Log

class AppLogger(private val tag: String) {
    fun debug(message: String) = Log.d(tag, "[DEBUG] $message")
    fun error(message: String) = Log.e(tag, "[ERROR] $message")
    fun error(message: String, throwable: Throwable) = Log.e(tag, "[ERROR] $message", throwable)
}
