package io.github.hoangbv15

import io.github.hoangbv15.commandline.CommandLine
import io.github.hoangbv15.logging.ConsoleLogTarget
import io.github.hoangbv15.logging.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

class OneCommonBuildPlugin: Plugin<Project> {
    private lateinit var git: Git
    private var log: Log = Log()
    init {
        log.targets.add(ConsoleLogTarget())
    }

    override fun apply(project: Project) {
        val commandLine = CommandLine(project.projectDir)
        git = Git(commandLine, log)
        project.extensions.add("git", git)
    }
}