package io.github.hoangbv15.file

import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

interface IFileSystem {
    fun getAllFiles(path: String, recursive: Boolean): List<File>
    fun searchForFile(root: Any, fileName: String): File?
    fun pathCombine(vararg files: String): String
    fun replaceInFiles(
        files: List<Any>,
        regex: Regex,
        replace: (MatchResult, File) -> String,
        encoding: String?
    )
    fun patchFilesForTasks(
        project: Project,
        files: List<Any>,
        regex: Regex,
        replace: (MatchResult, File) -> String,
        reverseRegex: Regex,
        reverseReplace: (MatchResult, File) -> String,
        tasksToPatch: List<Task>,
        encoding: String?
    )
}