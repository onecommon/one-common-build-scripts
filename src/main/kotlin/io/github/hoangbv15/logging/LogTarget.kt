package io.github.hoangbv15.logging

interface LogTarget {
    fun log(logLevel: LogLevel, msg: String)
}