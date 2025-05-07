package rk.gac.util

object Logger {
    object Logger {
        private var enabled = true
        fun d(msg: String) {
            if (enabled) println("[DEBUG] $msg")
        }
    }
}