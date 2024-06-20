package bagels.brands.gradle.tasks

import org.gradle.api.tasks.UntrackedTask

/**
 * Downloads the Sort Dependencies binary from maven central.
 *
 * Usage:
 * ```
 *     ./gradlew updateSortDependencies
 * ```
 */
@UntrackedTask(because = "These are one-off, on-demand download tasks")
internal abstract class SortDependenciesDownloadTask :
    BaseDownloadTask(
        targetName = "Sort Dependencies",
        addExecPrefix = true,
        urlTemplate = { version ->
            "https://repo1.maven.org/maven2/com/squareup/sort-gradle-dependencies-app/$version/sort-gradle-dependencies-app-$version-all.jar"
        }
    ) {
    init {
        description = "Downloads the Sort Dependencies binary from maven central."
    }
}
