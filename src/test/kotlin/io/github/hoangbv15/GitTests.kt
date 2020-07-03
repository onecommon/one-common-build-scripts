package io.github.hoangbv15

import io.mockk.every
import io.mockk.impl.annotations.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@ExtendWith(MockKExtension::class)
class GitTests {
    lateinit var git: Git
    @MockK lateinit var commandLineMock: CommandLine

    @BeforeEach
    fun setup() {
        git = Git(commandLineMock)
        setGitBranchName("master")
    }

    fun setGitBranchName(name: String) {
        every { commandLineMock.execCommand(cmd = any()) } returns name
    }

    @Test fun getBranchName_ShouldReturnCorrectValue() {
        assertEquals("master", git.getBranchName())
    }

    @ParameterizedTest
    @CsvSource("master,true", "develop,false", "master123,false")
    fun isMaster_ShouldReturnCorrectValue(branchName: String, expected: Boolean) {
        setGitBranchName(branchName)
        assertEquals(expected, git.isMaster())
    }

    @ParameterizedTest
    @CsvSource("develop,true", "master,false", "develop123,false")
    fun isDevelop_ShouldReturnCorrectValue(branchName: String, expected: Boolean) {
        setGitBranchName(branchName)
        assertEquals(expected, git.isDevelop())
    }

    @ParameterizedTest
    @CsvSource("feature/something,true", "feature/,false", "feature,false", "feature123,false", "develop,false")
    fun isFeature_ShouldReturnCorrectValue(branchName: String, expected: Boolean) {
        setGitBranchName(branchName)
        assertEquals(expected, git.isFeature())
    }

    @ParameterizedTest
    @CsvSource("release/something,true", "release/,false", "release,false", "release123,false", "develop,false",
        "hotfix/something,true", "hotfix/,false", "hotfix,false", "hotfix123,false",
        "master,true", "master123,false")
    fun isRelease_ShouldReturnCorrectValue(branchName: String, expected: Boolean) {
        setGitBranchName(branchName)
        assertEquals(expected, git.isRelease())
    }
}