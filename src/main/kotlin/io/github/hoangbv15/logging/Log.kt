package io.github.hoangbv15.logging

class Log {
    val targets: MutableList<ILogTarget> = mutableListOf()
    var logLevel: LogLevel = LogLevel.DEBUG

    fun debug(tag: String, msg: String) {
        log(LogLevel.DEBUG, tag, msg)
    }

    fun info(tag: String, msg: String) {
        log(LogLevel.INFO, tag, msg)
    }

    fun warn(tag: String, msg: String) {
        log(LogLevel.WARN, tag, msg)
    }

    fun error(tag: String, msg: String) {
        log(LogLevel.ERROR, tag, msg)
    }

    private fun log(logLevel: LogLevel, tag: String, msg: String) {
        if (logLevel > this.logLevel) {
            return
        }
        targets.forEach {
            it.log(logLevel, "[$logLevel][$tag] $msg")
        }
    }
}