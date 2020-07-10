package io.github.hoangbv15

import io.github.hoangbv15.commandline.ICommandLine
import io.github.hoangbv15.logging.Log
import org.gradle.api.GradleException

class Git(private val commandLine: ICommandLine, private val log: Log) {
    private val TAG: String = this::class.simpleName!!

    fun getBranchName(): String {
        return exec("git rev-parse --abbrev-ref HEAD")
    }

    fun branchMatches(regex: String): Boolean {
        return getBranchName().matches(Regex(regex))
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

    fun getTags(): List<String> {
        val tagStrs = exec("git tag -l --sort=-version:refname")
        if (tagStrs.trim().isEmpty()) {
            return emptyList()
        }
        return tagStrs.lines().map { it.removePrefix("v") }
    }

    fun getLatestTag(): String? {
        val tags = getTags()
        tags.forEach {
            if (it.matches(Regex("""^(\d+\.)?(\d+\.)?(\*|\d+)$"""))) {
                return it
            }
        }
        return null
    }

    fun getReleaseVersion(): String? {
        if (isRelease()) {
            val v = Regex("""(?!.*\/).+""").find(getBranchName())
            return v?.groupValues?.get(0)
        }
        return null
    }

    private fun validateRepo() {
        val status = exec("git status --porcelain")
        if (status.isNotEmpty()) {
            throw GradleException("Branch is not clean: $status")
        }
    }

    private fun checkoutBranch(name: String) {
        exec("git checkout $name")
    }

    private fun createBranch(name: String) {
        exec("git branch $name")
    }

    private fun merge(branchName: String) {
        exec("git merge $branchName")
    }

    private fun reset() {
        exec("git clean -fd")
        exec("git reset --hard")
    }

    private fun push(branchName: String, dryRun: Boolean = false) {
        log.info(TAG, "Checking out $branchName and pushing (dryRun = $dryRun)")
        checkoutBranch(branchName)
        var command = "git push origin HEAD --tags"
        if (dryRun) {
            command += " --dry-run"
        }
        exec(command)
    }

    private fun pushTags(dryRun: Boolean = false) {
        var command = "git push --tags"
        if (dryRun) {
            command += " --dry-run"
        }
        exec(command)
    }

    private fun deleteBranch(branchName: String) {
        try {
            exec("git push origin --delete $branchName")
        }
        catch (e: Throwable) {
            log.warn(TAG, "Cannot remove remote branch '$branchName': $e")
        }
        exec("git branch -D $branchName")
    }

    fun verifyReleaseTag() {
        val releaseVersion = getReleaseVersion()
        val latestTag = getLatestTag()
        val branchName = getBranchName()

        if (releaseVersion != latestTag) {
            throw GradleException("Release branch '$branchName' does not match the latest tag '$latestTag'")
        }

        log.info(TAG, "Release branch '$branchName' matches the latest tag '$latestTag'")
    }

    fun cutRelease(version: String, shouldValidateRepo: Boolean) {
        if (!isDevelop()) {
            checkoutBranch("develop")
        }
        if (shouldValidateRepo) {
            validateRepo()
        }
        val branchName = "release/$version"
        createBranch(branchName)
        checkoutBranch(branchName)
    }

    fun finishRelease() {
        if (!isRelease()) {
            throw GradleException("Not on a release branch")
        }
        verifyReleaseTag()
        val releaseBranchName = getBranchName()
        reset()
        checkoutBranch("develop")
        merge(releaseBranchName)
        checkoutBranch("master")
        merge("develop")
        pushTags(true)
        pushTags()
        push("develop", true)
        push("develop")
        push("master", true)
        push("master")
        deleteBranch(releaseBranchName)
    }

    private fun exec(command: String): String {
        return commandLine.execCommand(command)
    }
}