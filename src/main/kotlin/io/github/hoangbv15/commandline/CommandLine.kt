package io.github.hoangbv15.commandline

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class CommandLine(private val workingDir: File): ICommandLine {
    override fun execCommand(cmd: String): String {
        return try {
            val parts = cmd.split("\\s".toRegex())
            val process = ProcessBuilder(*parts.toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            process.waitFor(60, TimeUnit.MINUTES)
            process.inputStream.bufferedReader().readText().trim()
        } catch(e: IOException) {
            e.toString()
        }
    }
}