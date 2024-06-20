@file:Suppress("UnstableApiUsage")

import java.net.URI

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI("https://jitpack.io") }
        maven { url = URI("https://clojars.org/repo/") }
        flatDir { dirs("app/libs") }
    }
}

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        // ExclusiveContent is used here because this proxies jcenter under the hood!
        exclusiveContent {
            forRepository(::gradlePluginPortal)
            filter {
                includeModule("org.gradle.toolchains", "foojay-resolver")
                includeModule("org.gradle.toolchains.foojay-resolver-convention", "org.gradle.toolchains.foojay-resolver-convention.gradle.plugin")
                includeModule("org.jlleitschuh.gradle", "ktlint-gradle")
                includeModule("org.jlleitschuh.gradle.ktlint", "org.jlleitschuh.gradle.ktlint.gradle.plugin")
                includeModule("com.gradle", "gradle-enterprise-gradle-plugin")
                includeModule("com.gradle.enterprise", "com.gradle.enterprise.gradle.plugin")
                includeModule("com.github.ben-manes", "gradle-versions-plugin")
                includeModule("com.github.ben-manes.versions", "com.github.ben-manes.versions.gradle.plugin")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.6.0"
    id("com.gradle.enterprise") version "3.14"
}

gradleEnterprise {
    buildScan {
        publishAlways()
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "bagels-android-app"
include(":app")

// https://docs.gradle.org/5.6/userguide/groovy_plugin.html#sec:groovy_compilation_avoidance
enableFeaturePreview("GROOVY_COMPILATION_AVOIDANCE")

// https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
