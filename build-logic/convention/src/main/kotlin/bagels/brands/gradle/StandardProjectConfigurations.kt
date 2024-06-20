package bagels.brands.gradle

import bagels.brands.dependencyrake.MissingIdentifiersAggregatorTask
import bagels.brands.dependencyrake.RakeDependencies
import bagels.brands.gradle.properties.ProjectProperties
import bagels.brands.gradle.util.getVersionsCatalog
import bagels.brands.gradle.util.identifierMap
import com.android.build.gradle.internal.utils.setDisallowChanges
import com.autonomousapps.DependencyAnalysisSubExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import org.jetbrains.kotlin.gradle.utils.named

/**
 * Standard [Project] configurations. This class will be iterated on over time as we grow out our
 * bootstrapping options for Gradle subprojects.
 *
 * Principles:
 * - Avoid duplicating work and allocations. This runs at configuration time and should be as low
 *   overhead as possible.
 * - Do not resolve dependencies at configuration-time. Use appropriate callback APIs!
 * - Support Kotlin, Android, and Java projects.
 * - One-off configuration should be left to individual projects to declare.
 * - Use debug logging.
 */
internal class StandardProjectConfigurations(
    private val globalProperties: ProjectProperties,
    private val bbTools: BBTools,
) {
    fun applyTo(project: Project) {
        val properties = ProjectProperties(project)
        project.applyCommonConfigurations()
        project.applyJvmConfigurations(properties)
        project.configureKotlinProjects()
    }

    private fun Project.applyCommonConfigurations() {
        if (globalProperties.autoApplySortDependencies) {
            if (project.buildFile.exists()) {
                val sortDependenciesIgnoreSet =
                    globalProperties.sortDependenciesIgnore?.splitToSequence(',')?.toSet().orEmpty()
                if (project.path !in sortDependenciesIgnoreSet) {
                    pluginManager.apply("com.squareup.sort-dependencies")
                }
            }
        }
    }

    private fun Project.applyJvmConfigurations(
        projectProperties: ProjectProperties,
    ) {
        if (projectProperties.enableAnalysisPlugin) {
            val buildFile = project.buildFile
            // This can run on some intermediate middle directories, like `carbonite` in
            // `carbonite:carbonite`
            if (buildFile.exists()) {
                // Configure rake
                plugins.withId("com.autonomousapps.dependency-analysis") {
                    if (project.pluginManager.hasPlugin("com.android.test")) {
                        // Not supported yet in DAGP
                        // https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin/issues/797
                        return@withId
                    }
                    val catalogNames = extensions.findByType<VersionCatalogsExtension>()?.catalogNames ?: return@withId
                    val catalogs = catalogNames.map { catalogName -> project.getVersionsCatalog(catalogName) }
                    val rakeDependencies =
                        tasks.register<RakeDependencies>("rakeDependencies") {
                            // TODO https://github.com/gradle/gradle/issues/25014
                            buildFileProperty.set(project.buildFile)
                            noApi.setDisallowChanges(false)
                            identifierMap.setDisallowChanges(
                                project.provider {
                                    buildMap {
                                        for (catalog in catalogs) {
                                            putAll(catalog.identifierMap().mapValues { (_, v) -> "${catalog.name}.$v" })
                                        }
                                    }
                                }
                            )
                            missingIdentifiersFile.set(
                                project.layout.buildDirectory.file("rake/missing_identifiers.txt")
                            )
                        }
                    configure<DependencyAnalysisSubExtension> { registerPostProcessingTask(rakeDependencies) }
                    val aggregator = project.rootProject.tasks.named<MissingIdentifiersAggregatorTask>(
                        MissingIdentifiersAggregatorTask.NAME
                    )
                    aggregator.configure {
                        inputFiles.from(rakeDependencies.flatMap { it.missingIdentifiersFile })
                    }
                }
            }
        }
    }

    private fun Project.configureKotlinProjects() {
        plugins.withType(KotlinBasePlugin::class.java).configureEach {
            project.kotlinExtension.apply {
                kotlinDaemonJvmArgs = bbTools.globalConfig.kotlinDaemonArgs
            }
        }
    }
}
