package io.github.hoangbv15

import io.github.hoangbv15.logging.ConsoleLogTarget
import io.github.hoangbv15.logging.Log
import io.mockk.every
import io.mockk.impl.annotations.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ExtendWith(MockKExtension::class)
class GitTests {
    lateinit var git: Git
    @MockK lateinit var commandLineMock: CommandLine

    @BeforeEach
    fun setup() {
        val log = Log()
        log.targets.add(ConsoleLogTarget())
        git = Git(commandLineMock, log)
        setCommandLineResult("master")
    }

    fun setCommandLineResult(result: String) {
        every { commandLineMock.execCommand(cmd = any()) } returns result
    }

    @Test fun getBranchName_ShouldReturnCorrectValue() {
        assertEquals("master", git.getBranchName())
    }

    @ParameterizedTest
    @CsvSource("master,true", "develop,false", "master123,false")
    fun isMaster_ShouldReturnCorrectValue(branchName: String, expected: Boolean) {
        setCommandLineResult(branchName)
        assertEquals(expected, git.isMaster())
    }

    @ParameterizedTest
    @CsvSource("develop,true", "master,false", "develop123,false")
    fun isDevelop_ShouldReturnCorrectValue(branchName: String, expected: Boolean) {
        setCommandLineResult(branchName)
        assertEquals(expected, git.isDevelop())
    }

    @ParameterizedTest
    @CsvSource("feature/something,true", "feature/,false", "feature,false", "feature123,false", "develop,false")
    fun isFeature_ShouldReturnCorrectValue(branchName: String, expected: Boolean) {
        setCommandLineResult(branchName)
        assertEquals(expected, git.isFeature())
    }

    @ParameterizedTest
    @CsvSource("release/something,true", "release/,false", "release,false", "release123,false", "develop,false",
        "hotfix/something,true", "hotfix/,false", "hotfix,false", "hotfix123,false",
        "master,true", "master123,false")
    fun isRelease_ShouldReturnCorrectValue(branchName: String, expected: Boolean) {
        setCommandLineResult(branchName)
        assertEquals(expected, git.isRelease())
    }

    @Test fun getTags_GivenTags_ShouldReturnCorrectValue() {
        setCommandLineResult("1.0.0\n1.1.0\n2.0.0")
        assertIterableEquals(listOf("1.0.0", "1.1.0", "2.0.0"), git.getTags())
    }

    @Test fun getTags_GivenNoTags_ShouldReturnEmpty() {
        setCommandLineResult("")
        assertIterableEquals(emptyList<String>(), git.getTags())
    }

    @Test fun getLatestTag_GivenTags_ShouldReturnFirstOne() {
        setCommandLineResult("2.0.0\n1.5.0\n1.1.0")
        assertEquals("2.0.0", git.getLatestTag())
    }

    @Test fun getLatestTag_GivenNoTags_ShouldReturnNull() {
        setCommandLineResult("")
        assertNull(git.getLatestTag())
    }

    @Test fun getReleaseVersion_GivenReleaseBranchName_ShouldReturnCorrectValue() {
        setCommandLineResult("release/1.1.0")
        assertEquals("1.1.0", git.getReleaseVersion())
    }

    @Test fun getReleaseVersion_GivenNoReleaseBranch_ShouldReturnNull() {
        setCommandLineResult("release123/1.1.0")
        assertNull(git.getReleaseVersion())
    }
}