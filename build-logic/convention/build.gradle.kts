plugins {
    kotlin("jvm")
    `java-gradle-plugin`
}

gradlePlugin {
    plugins.create("bagels-root") {
        id = "bagels.brands.gradle.root"
        implementationClass = "bagels.brands.gradle.RootPlugin"
    }
    plugins.create("bagels-base") {
        id = "bagels.brands.gradle.base"
        implementationClass = "bagels.brands.gradle.BasePlugin"
    }
}

dependencies {
    api(platform(libs.okhttp.bom))
    api(libs.okhttp)
    api(libs.okio)

    api(projects.agpHandlers.agpHandlerApi)

    implementation(libs.kotlinCliUtil)
    implementation(libs.oshi) { because("To read hardware information") }

    implementation(platform(libs.kotlin.bom))
    implementation(gradleApi())

    compileOnly(libs.agp)

    compileOnly(libs.gradlePlugins.dependencyAnalysis)
    compileOnly(libs.gradlePlugins.enterprise)
    compileOnly(libs.gradlePlugins.kgp)
    compileOnly(libs.gradlePlugins.versions)
}
