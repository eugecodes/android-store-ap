package bagels.brands.gradle

import bagels.brands.dependencyrake.MissingIdentifiersAggregatorTask
import bagel.brands.gradle.agp.VersionNumber
import bagels.brands.gradle.properties.ProjectProperties
import bagels.brands.gradle.scan.ScanApi
import bagels.brands.gradle.scan.configureBuildScanMetadata
import bagels.brands.gradle.tasks.CoreBootstrapTask
import bagels.brands.gradle.tasks.SortDependenciesDownloadTask
import com.android.build.gradle.internal.utils.setDisallowChanges
import com.autonomousapps.DependencyAnalysisExtension
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JvmVendorSpec
import java.util.Locale

internal class RootPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        require(project == project.rootProject) {
            "Root plugin should only be applied on the root project!"
        }

        val properties = ProjectProperties(project)
        BBTools.register(
            project = project,
        )
        configureRootProject(project, properties)
    }

    private fun configureRootProject(
        project: Project,
        properties: ProjectProperties,
    ) {
        project.configureRootBuildscript(
            properties.jvmVendor.map(JvmVendorSpec::matching).orNull
        )
        val scanApi = ScanApi(project)
        if (properties.applyCommonBuildTags) {
            project.configureBuildScanMetadata(scanApi)
        }

        // Add sortDependencies download task
        properties.versions.sortDependencies?.let { sortDependenciesVersion ->
            project.tasks.register<SortDependenciesDownloadTask>("updateSortDependencies") {
                version.setDisallowChanges(sortDependenciesVersion)
                outputFile.setDisallowChanges(
                    project.layout.projectDirectory.file("config/bin/sort-dependencies")
                )
            }
        }

        // Dependency analysis plugin for build health
        // Usage: ./gradlew clean buildHealth
        project.pluginManager.withPlugin("com.autonomousapps.dependency-analysis") {
            // Register the missing identifiers aggregator
            if (properties.enableAnalysisPlugin) {
                MissingIdentifiersAggregatorTask.register(project)
            }
            project.configure<DependencyAnalysisExtension> {
                issues { all { onAny { ignoreKtx(true) } } }
                abi {
                    exclusions {
                        ignoreGeneratedCode()
                        ignoreInternalPackages()
                    }
                }
                dependencies {
                    bundle("androidx-camera") {
                        primary("androidx.camera:camera-camera2")
                        includeGroup("androidx.camera")
                    }
                    bundle("androidx-paging") {
                        primary("androidx.paging:paging-runtime")
                        includeGroup("androidx.paging")
                    }
                    bundle("androidx-lifecycle") {
                        primary("androidx.lifecycle:lifecycle-runtime")
                        includeGroup("androidx.lifecycle")
                        includeGroup("androidx.arch.core")
                    }
                    bundle("bugsnag") { includeGroup("com.bugsnag") }
                    bundle("clikt") {
                        primary("com.github.ajalt.clikt:clikt")
                        includeGroup("com.github.ajalt.clikt")
                    }
                    bundle("compose-animation") {
                        primary("androidx.compose.animation:animation")
                        includeGroup("androidx.compose.animation")
                    }
                    bundle("compose-foundation") {
                        primary("androidx.compose.foundation:foundation")
                        includeGroup("androidx.compose.foundation")
                    }
                    bundle("compose-runtime") {
                        primary("androidx.compose.runtime:runtime")
                        includeGroup("androidx.compose.runtime")
                    }
                    bundle("dagger") {
                        includeGroup("com.google.dagger")
                        includeDependency("javax.inject:javax.inject")
                    }
                    bundle("exoplayer") { includeGroup("com.google.android.exoplayer") }
                    bundle("kotlin-stdlib") { includeGroup("org.jetbrains.kotlin") }
                    bundle("leakcanary") {
                        primary("com.squareup.leakcanary:leakcanary-android")
                        includeGroup("com.squareup.leakcanary")
                    }
                    bundle("lint-tools") { includeGroup("com.android.tools.lint") }
                    bundle("okhttp") {
                        primary("com.squareup.okhttp3:okhttp")
                        includeGroup("com.squareup.okhttp3")
                    }
                    bundle("paging") { includeGroup("androidx.paging") }
                    bundle("robolectric") { includeGroup("org.robolectric") }
                    bundle("rxjava") { includeGroup("io.reactivex.rxjava3") }
                }
            }
        }

        project.pluginManager.withPlugin("com.github.ben-manes.versions") {
            project.tasks.withType(DependencyUpdatesTask::class.java).configureEach {
                // Disallow updating to unstable candidates from stable versions, but do allow suggesting newer unstable candidates if we're already on an unstable
                // version. Note that we won't suggest a newer unstable version if it has a different base version.
                rejectVersionIf {
                    when {
                        candidate.moduleIdentifier.toString() == "com.google.guava:guava" -> {
                            // Guava has special rules because it adds -jre or -android as a suffix. These are
                            // misunderstood by the VersionNumber API as suffixes because it will use their
                            // natural order. We just use -jre every time so we reject all -android versions.
                            return@rejectVersionIf "-android" in candidate.version
                        }
                        candidate.group.startsWith("androidx.test") -> {
                            // We do allow non-stable test dependencies because they're
                            // - Not shipped in prod, we can immediately mitigate if something is wrong
                            // - About as reliable in alphas releases as they are in stable.
                            //   - Alphas tend to have critical bugfixes introduced by the previous stable 🤦‍
                            return@rejectVersionIf false
                        }
                        !properties.versionsPluginAllowUnstable -> {
                            val currentIsStable = isStable(currentVersion)
                            val candidateIsStable = isStable(candidate.version)
                            if (!currentIsStable) {
                                if (candidateIsStable) {
                                    // Always prefer stable candidates newer than a current unstable version
                                    return@rejectVersionIf false
                                } else {
                                    val candidateVersion = VersionNumber.parse(candidate.version)
                                    val currentVersion = VersionNumber.parse(currentVersion)

                                    @Suppress("ReplaceCallWithBinaryOperator") // Bug in groovy interop
                                    val bothAreUnstable =
                                        !candidateVersion.equals(VersionNumber.UNKNOWN) &&
                                            !currentVersion.equals(VersionNumber.UNKNOWN)
                                    if (bothAreUnstable) {
                                        // Both are unstable. Only accept a newer unstable version if it's the same
                                        // maj.min.patch. This is so we don't accidentally skip a more stable version in
                                        // between.
                                        // Example:
                                        //   - Current: 1.1.0-alpha01
                                        //   - Candidate: 1.2.0-alpha01
                                        //   - Other available: 1.1.0-alpha02, 1.1.1
                                        // In this case we want 1.1.1 and to reject the newer 1.2.0-alpha01
                                        val shouldReject = candidateVersion.baseVersion > currentVersion.baseVersion
                                        if (shouldReject) {
                                            project.logger.debug(
                                                "Rejecting unstable $candidate because its base version " +
                                                    "is greater than $currentVersion."
                                            )
                                        }
                                        return@rejectVersionIf shouldReject
                                    }
                                }
                            }
                            return@rejectVersionIf !candidateIsStable && currentIsStable
                        }
                        else -> return@rejectVersionIf false
                    }
                }
            }
        }
    }

    private fun isStable(version: String): Boolean {
        val stableKeyword =
            listOf("RELEASE", "FINAL", "GA").any { version.uppercase(Locale.US).contains(it) }
        return stableKeyword || STABLE_REGEX.matches(version)
    }

    private companion object {
        private val STABLE_REGEX = "^[0-9,.v-]+(-android)?(-r)?$".toRegex()
    }
}

private fun Project.configureRootBuildscript(jvmVendor: JvmVendorSpec?) {
    // Only register bootstrap if explicitly requested for now
    if (CoreBootstrapTask.isBootstrapEnabled(this)) {
        CoreBootstrapTask.register(this, jvmVendor)
    }
}
