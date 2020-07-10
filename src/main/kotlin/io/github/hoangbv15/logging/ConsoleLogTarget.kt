package io.github.hoangbv15.logging

class ConsoleLogTarget: ILogTarget {
    override fun log(logLevel: LogLevel, msg: String) {
        println(msg)
    }
}