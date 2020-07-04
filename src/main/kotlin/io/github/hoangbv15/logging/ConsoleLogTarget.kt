package io.github.hoangbv15.logging

class ConsoleLogTarget: LogTarget {
    override fun log(logLevel: LogLevel, msg: String) {
        println(msg)
    }
}