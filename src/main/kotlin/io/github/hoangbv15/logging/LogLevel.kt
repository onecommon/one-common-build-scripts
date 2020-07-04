package io.github.hoangbv15.logging

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR;

    override fun toString(): String {
        return when(this) {
            DEBUG -> "D"
            INFO -> "I"
            WARN -> "W"
            ERROR -> "E"
        }
    }
}