package org.unbrokendome.gradle.plugins.testsets

import assertk.assert
import assertk.assertions.isIn
import assertk.assertions.isNotNull
import assertk.assertions.prop
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class GradleVersionsCompatibilityTest {

    private val projectDir = createTempDir("gradle-testsets-plugin-test")


    @BeforeEach
    fun setup() {
        projectDir.resolve("build.gradle.kts")
                .writeText("""
                    plugins {
                       id("org.unbroken-dome.test-sets")
                    }

                    testSets.create("integrationTest")
                    """.trimIndent())
    }


    @ValueSource(strings = ["4.10", "4.10.2"])
    @ParameterizedTest
    @DisplayName("Should work in Gradle version")
    fun shouldWorkInGradleVersion(gradleVersion: String) {
        val result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion)
                .withPluginClasspath()
                .withArguments("integrationTest", "--info", "--stacktrace")
                .forwardOutput()
                .build()

        assert(result, "result")
                .prop("for task integrationTest") { it.task(":integrationTest") }
                .isNotNull {
                    it.prop("outcome", BuildTask::getOutcome)
                            .isIn(TaskOutcome.NO_SOURCE, TaskOutcome.UP_TO_DATE)
                }
    }


    @AfterEach
    fun removeProjectDir() {
        projectDir.deleteRecursively()
    }
}
