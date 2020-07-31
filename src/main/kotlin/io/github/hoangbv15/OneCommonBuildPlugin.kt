package io.github.hoangbv15

import io.github.hoangbv15.commandline.CommandLine
import io.github.hoangbv15.file.Files
import io.github.hoangbv15.file.IFiles
import io.github.hoangbv15.logging.ConsoleLogTarget
import io.github.hoangbv15.logging.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

class OneCommonBuildPlugin: Plugin<Project> {
    private lateinit var git: Git
    private lateinit var files: IFiles
    private var log: Log = Log()
    init {
        log.targets.add(ConsoleLogTarget())
    }

    override fun apply(project: Project) {
        val commandLine = CommandLine(project.projectDir)
        git = Git(commandLine, log)
        files = Files(log, project)

        project.extensions.add("Git", git)
        project.extensions.add("Files", files)
    }
}