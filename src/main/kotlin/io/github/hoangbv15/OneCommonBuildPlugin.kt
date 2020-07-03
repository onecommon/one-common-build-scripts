package io.github.hoangbv15

import org.gradle.api.Plugin
import org.gradle.api.Project

class OneCommonBuildPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("crawlFacebookUser") {
            it.doLast {
                println("Crawl")
            }
        }
    }
}