package bagels.brands.gradle.util

import bagels.brands.gradle.getByType
import bagels.brands.gradle.properties.ProjectProperties
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.internal.impldep.com.google.common.base.CaseFormat

/** If true, this is currently running on GitHub Actions CI. */
public val Project.isCircleCI: Boolean
    get() = providers.environmentVariable("CIRCLECI").mapToBoolean().getOrElse(false)

/** If true, this is currently running on any CI. */
public val Project.isCi: Boolean
    get() = isCircleCI

internal fun Project.jdkVersion(): Int = ProjectProperties(this).jdkVersion

internal fun Project.getVersionsCatalog(name: String = "libs"): VersionCatalog = getVersionsCatalogOrNull(name) ?: error("No versions catalog found!")

internal fun Project.getVersionsCatalogOrNull(name: String = "libs"): VersionCatalog? = try {
    project.extensions.getByType<VersionCatalogsExtension>().named(name)
} catch (ignored: Exception) {
    null
}

/**
 * We want the following conversions:
 * - `bugsnag-gradle` -> `bugsnagGradle`
 * - `bugsnag_gradle` -> `bugsnagGradle`
 * - `bugsnag.gradle` -> `bugsnag-gradle`
 *
 * This is because `bugsnag-gradle` is converted to a nesting `bugsnag.gradle` in version accessors
 * and `bugsnag.gradle` is converted to `bugsnagGradle`. We've historically done the opposite with
 * gradle property versions though and used -/_ as separators in a continuous word and `.` for
 * nesting.
 */
internal fun tomlKey(key: String): String =
    key.replace("-", "%").replace(".", "-").replace("%", ".").replace("_", ".").snakeToCamel()

internal fun String.snakeToCamel(upper: Boolean = false): String {
    return buildString {
        var capNext = upper
        for (c in this@snakeToCamel) {
            if (c == '_' || c == '-' || c == '.') {
                capNext = true
                continue
            } else {
                if (capNext) {
                    append(c.uppercaseChar())
                    capNext = false
                } else {
                    append(c)
                }
            }
        }
    }
}


private fun kebabCaseToCamelCase(s: String): String = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, s)

/**
 * Returns a project accessor representation of the given [projectPath].
 *
 * Example: `:libraries:foundation` -> `libraries.foundation`.
 */
internal fun convertProjectPathToAccessor(projectPath: String): String = projectPath.removePrefix(":").split(":").joinToString(separator = ".") { segment ->
    kebabCaseToCamelCase(segment)
}

/** Returns a map of module identifiers to toml library reference aliases */
internal fun VersionCatalog.identifierMap(): Map<String, String> = libraryAliases.associateBy { findLibrary(it).get().get().module.toString() }
