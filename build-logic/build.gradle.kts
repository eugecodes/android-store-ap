import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.samWithReceiver) apply false
}


/**
 * See more information about these in
 * - CommonCompilerArguments.kt
 * - K2JVMCompilerArguments.kt
 */
val kotlinCompilerArgs: List<String> = listOf(
    "-progressive",
    "-opt-in=kotlin.contracts.ExperimentalContracts",
    "-opt-in=kotlin.experimental.ExperimentalTypeInference",
    "-opt-in=kotlin.ExperimentalStdlibApi",
    "-opt-in=kotlin.time.ExperimentalTime",
    "-Xproper-ieee754-comparisons",
    // Enhance not null annotated type parameter's types to definitely not null types (@NotNull T
    // => T & Any)
    "-Xenhance-type-parameter-types-to-def-not-null",
    // Support inferring type arguments based on only self upper bounds of the corresponding type
    // parameters
    "-Xself-upper-bound-inference",
)
val kotlinJvmCompilerArgs: List<String> = listOf(
    "-Xjsr305=strict",
    // Match JVM assertion behavior:
    // https://publicobject.com/2019/11/18/kotlins-assert-is-not-like-javas-assert/
    "-Xassertions=jvm",
    // Potentially useful for static analysis tools or annotation processors.
    "-Xemit-jvm-type-annotations",
    // Enable new jvm-default behavior
    // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
    "-Xjvm-default=all",
    "-Xtype-enhancement-improvements-strict-mode",
    // https://kotlinlang.org/docs/whatsnew1520.html#support-for-jspecify-nullness-annotations
    "-Xjspecify-annotations=strict",
)

subprojects {
    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(
                    JavaLanguageVersion.of(libs.versions.jdk.get().removeSuffix("-ea").toInt())
                )
            }
        }

        tasks.withType<JavaCompile>().configureEach { options.release.set(17) }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                val kotlinVersion = KOTLIN_1_8
                languageVersion.set(kotlinVersion)
                apiVersion.set(kotlinVersion)

                // Gradle forces a lower version of kotlin, which results in warnings that prevent use of
                // this sometimes. https://github.com/gradle/gradle/issues/16345
                allWarningsAsErrors.set(false)
                // TODO required due to https://github.com/gradle/gradle/issues/24871
                freeCompilerArgs.add("-Xsam-conversions=class")

                jvmTarget.set(JvmTarget.JVM_17)
                freeCompilerArgs.addAll(
                    kotlinCompilerArgs
                        // -progressive is useless when running on an older language version but new compiler
                        // version. Both Gradle and IntelliJ plugins have this issue 🙃
                        .filter { it != "-progressive" }
                )
                freeCompilerArgs.addAll(kotlinJvmCompilerArgs)
            }
        }

        extensions.configure<KotlinProjectExtension> { explicitApi() }

        // Reimplement kotlin-dsl's application of this function for nice DSLs
        apply(plugin = "kotlin-sam-with-receiver")
        configure<SamWithReceiverExtension> { annotation("org.gradle.api.HasImplicitReceiver") }
    }
}
