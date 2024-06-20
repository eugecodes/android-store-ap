plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.guava)

    compileOnly(gradleApi())
    compileOnly(libs.agp)
}
