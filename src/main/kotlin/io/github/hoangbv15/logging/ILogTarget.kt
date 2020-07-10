package io.github.hoangbv15.logging

interface ILogTarget {
    fun log(logLevel: LogLevel, msg: String)
}