// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        // We have to declare this here in order for kotlin-facets to be generated in iml files
        // https://youtrack.jetbrains.com/issue/KT-36331
        classpath(libs.gradlePlugins.kgp)
        classpath(libs.newrelic.gradlePlugin)
    }
}

plugins {
    id("bagels.brands.gradle.root")
    alias(libs.plugins.agp) apply false
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.ksp)
    alias(libs.plugins.sortDependencies) apply false
    alias(libs.plugins.versionsPlugin)
}
