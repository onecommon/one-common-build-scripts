package io.github.hoangbv15.file

import io.github.hoangbv15.logging.Log
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.nio.charset.Charset

class FileSystem(private val log: Log): IFileSystem {
    private val TAG: String = this::class.simpleName!!

    override fun getAllFiles(path: String, recursive: Boolean): List<File> {
        val results = mutableListOf<File>()
        val file = File(path)

        if (!file.exists()) {
            return results
        }

        if (file.isFile) {
            results.add(file)
            return results
        }

        if (file.isDirectory) {
            if (recursive) {
                file.walkTopDown().forEach {
                    if (it.isFile) {
                        results.add(it)
                    }
                }
            }
            else {
                file.listFiles()?.forEach {
                    if (it.isFile) {
                        results.add(it)
                    }
                }
            }
        }

        return results
    }

    override fun searchForFile(root: Any, fileName: String): File? {
        val rootFile = if (root is File) root else File(root as String)

        if (!rootFile.exists() || rootFile.isFile) {
            return null
        }

        val file = File(pathCombine(root as String, fileName))
        if (file.exists()) {
            return file
        }

        var result: File? = null
        rootFile.listFiles()?.forEach {
            val search = searchForFile(it, fileName)
            if (search != null) {
                result = search
            }
        }
        return result
    }

    override fun pathCombine(vararg files: String): String {
        return files.joinToString(File.separator)
    }

    override fun replaceInFiles(
        files: List<Any>,
        regex: Regex,
        replace: (MatchResult, File) -> String,
        encoding: String?
    ) {
        files.forEach {
            val file = if (it is File) it else File(it as String)
            var text = file.readText()
            var anyMatch = false
            text = text.replace(regex) { m ->
                anyMatch = true
                val result = replace(m, file)
                log.info(TAG, "In $file: replacing $m with $result")
                result
            }
            if (!anyMatch) {
                log.warn(TAG, "In $file: no match for $regex")
                return
            }
            if (encoding != null) {
                file.writeText(text, Charset.forName(encoding))
            } else {
                file.writeText(text)
            }
        }
    }

    override fun patchFilesForTasks(
        project: Project,
        files: List<Any>,
        regex: Regex,
        replace: (MatchResult, File) -> String,
        reverseRegex: Regex,
        reverseReplace: (MatchResult, File) -> String,
        tasksToPatch: List<Task>,
        encoding: String?
    ) {
        tasksToPatch.forEach {
            val currentTask = it
            val taskName = currentTask.name
            // Test that the task was already added to the project
            val patchTaskName = "${taskName}Patch"

            if (project.tasks.findByName(patchTaskName) != null) {
                return
            }
            val patchTask = project.task(patchTaskName) { task: Task ->
                task.doLast {
                    replaceInFiles(files, regex, replace, encoding)
                }
            }
            val reverseTask = project.task("${taskName}Reverse") { task: Task ->
                task.doLast {
                    replaceInFiles(files, reverseRegex, reverseReplace, encoding)
                }
            }
            currentTask.dependsOn(patchTask)
            currentTask.finalizedBy(reverseTask)
        }
    }
}