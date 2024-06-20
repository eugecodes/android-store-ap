@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()

        // ExclusiveContent is used here because this proxies jcenter under the hood!
        exclusiveContent {
            forRepository(::gradlePluginPortal)
            filter {
                includeModule("org.gradle.toolchains", "foojay-resolver")
                includeModule("org.gradle.toolchains.foojay-resolver-convention", "org.gradle.toolchains.foojay-resolver-convention.gradle.plugin")
            }
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

    repositories {
        google()
        mavenCentral()

        // ExclusiveContent is used here because this proxies jcenter under the hood!
        exclusiveContent {
            forRepository(::gradlePluginPortal)
            filter {
                includeModule("com.github.ben-manes", "gradle-versions-plugin")
                includeModule("com.gradle", "gradle-enterprise-gradle-plugin")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.6.0"
}

rootProject.name = "build-logic"
include(
    ":agp-handlers:agp-handler-api",
    ":convention",
)

// https://docs.gradle.org/5.6/userguide/groovy_plugin.html#sec:groovy_compilation_avoidance
enableFeaturePreview("GROOVY_COMPILATION_AVOIDANCE")

// https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
