package io.github.hoangbv15

class Git(private val commandLine: CommandLine) {
    init {
        println("CommandLine: $commandLine")
    }

    fun getBranchName(): String {
        return commandLine.execCommand("git rev-parse --abbrev-ref HEAD")
    }

    fun branchMatches(regex: String): Boolean {
        return getBranchName().replace("\\s", "").matches(Regex(regex))
    }

    fun isMaster(): Boolean {
        return branchMatches("master")
    }

    fun isDevelop(): Boolean {
        return branchMatches("develop")
    }

    fun isFeature(): Boolean {
        return branchMatches("feature/.+")
    }

    fun isRelease(): Boolean {
        return branchMatches("master|release/.+|hotfix/.+")
    }
}