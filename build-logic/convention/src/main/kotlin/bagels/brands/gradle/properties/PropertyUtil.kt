package bagels.brands.gradle.properties

import bagels.brands.gradle.util.mapToBoolean
import com.android.build.gradle.internal.utils.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.provider.ValueSourceSpec
import java.util.Properties
import kotlin.contracts.contract

// Gradle's map {} APIs sometimes are interpreted by Kotlin to be non-null only but legally allow
// null returns. This
// abuses kotlin contracts to safe cast without a null check.
// https://github.com/gradle/gradle/issues/12388
internal fun <T> sneakyNull(value: T? = null): T {
    markAsNonNullForGradle(value)
    return value
}

// Gradle's map {} APIs sometimes are interpreted by Kotlin to be non-null only but legally allow
// null returns. This
// abuses kotlin contracts to safe cast without a null check.
internal fun <T> markAsNonNullForGradle(value: T?) {
    contract { returns() implies (value != null) }
}

/** Implementation of provider holding a start parameter's parsed project properties. */
internal abstract class StartParameterProperties : ValueSource<Map<String, String>, StartParameterProperties.Parameters> {
    interface Parameters : ValueSourceParameters {
        val properties: MapProperty<String, String>
    }

    override fun obtain(): Map<String, String>? {
        return parameters.properties.getOrElse(emptyMap())
    }
}

/** Implementation of provider holding a local properties file's parsed [Properties]. */
internal abstract class LocalProperties : ValueSource<Properties, LocalProperties.Parameters> {
    interface Parameters : ValueSourceParameters {
        val propertiesFile: RegularFileProperty
    }

    override fun obtain(): Properties? {
        val provider = parameters.propertiesFile
        if (!provider.isPresent) {
            return null
        }
        val propertiesFile = provider.asFile.get()
        if (!propertiesFile.exists()) {
            return null
        }
        return Properties().apply { propertiesFile.inputStream().use(::load) }
    }
}

/** Gets or creates a cached extra property. */
internal fun <T> Project.getOrCreateExtra(key: String, body: (Project) -> T): T {
    with(project.extensions.extraProperties) {
        if (!has(key)) {
            set(key, body(project))
        }
        @Suppress("UNCHECKED_CAST")
        return get(key) as? T ?: body(project) // Fallback if multiple class loaders are involved
    }
}

/**
 * Abstraction for loading a [Map] provider that handles caching automatically per root project.
 * This way properties are only ever parsed at most once per root project. The goal for this is to
 * build on top of [LocalProperties] and provide a more convenient API for accessing properties from
 * multiple sources in a configuration-caching-compatible way.
 */
private fun Project.startParameterProperties(key: String): Provider<String> {
    val provider = project.rootProject.getOrCreateExtra("bagels.properties.provider.start-properties") {
        val startParameters = project.gradle.startParameter.projectProperties
        it.providers.of(StartParameterProperties::class.java) {
            parameters.properties.setDisallowChanges(startParameters)
        }
    }
    return provider.map { sneakyNull(it[key]) }
}

/**
 * Abstraction for loading a [Properties] provider that handles caching automatically per-project.
 * This way files are only ever parsed at most once per project. The goal for this is to build on
 * top of [LocalProperties] and provide a more convenient API for accessing properties from multiple
 * sources in a configuration-caching-compatible way.
 */
private fun Project.localPropertiesProvider(
    key: String,
    cacheKey: String,
    valueSourceSpec: ValueSourceSpec<LocalProperties.Parameters>.() -> Unit
): Provider<String> {
    val provider = project.getOrCreateExtra(cacheKey) {
        it.providers.of(LocalProperties::class.java, valueSourceSpec)
    }
    return provider.map { it.getProperty(key) }
}

/** Returns a provider of a property _only_ contained in this project's local.properties. */
internal fun Project.localProperty(key: String): Provider<String> = localPropertiesProvider(key, "bagels.properties.provider.local-properties") {
    parameters.propertiesFile.setDisallowChanges(
        project.layout.projectDirectory.file("local.properties")
    )
}

/** Returns a provider of a property _only_ contained in this project's local gradle.properties. */
// Local gradle properties are not compatible with configuration caching and thus not accessible
// from
// providers.gradleProperty -_-. https://github.com/gradle/gradle/issues/13302
internal fun Project.localGradleProperty(key: String): Provider<String> = localPropertiesProvider(key, "bagels.properties.provider.local-gradle-properties") {
    parameters.propertiesFile.setDisallowChanges(
        project.layout.projectDirectory.file("gradle.properties")
    )
}

/**
 * A "safe" property access mechanism that handles multiple property sources.
 *
 * This checks in the following order of priority
 * - project-local `local.properties`
 * - project-local `gradle.properties`
 * - root-project `local.properties`
 * - root-project/global `gradle.properties`
 */
public fun Project.safeProperty(key: String): Provider<String> = rootProject
    .startParameterProperties(key) // start parameters
    .orElse(localProperty(key)) // project-local `local.properties`
    .orElse(localGradleProperty(key)) // project-local `gradle.properties`
    .orElse(rootProject.localProperty(key)) // root-project `local.properties`
    .orElse(providers.gradleProperty(key)) // root-project/global `gradle.properties`

internal fun Provider<String>.mapToInt(): Provider<Int> = map(String::toInt)

internal fun Project.booleanValue(key: String, defaultValue: Boolean = false): Boolean = booleanProvider(key, defaultValue).get()

internal fun Project.booleanProvider(
    key: String,
    defaultValue: Boolean = false
): Provider<Boolean> = booleanProvider(key, provider { defaultValue })

internal fun Project.booleanProvider(
    key: String,
    defaultValue: Provider<Boolean>
): Provider<Boolean> = booleanProvider(key).orElse(defaultValue)

internal fun Project.booleanProvider(
    key: String,
): Provider<Boolean> = safeProperty(key).mapToBoolean()

internal fun Project.optionalStringProperty(key: String, defaultValue: String? = null): String? = safeProperty(key).orNull ?: defaultValue

internal fun Project.optionalStringProvider(key: String): Provider<String> = optionalStringProvider(key, null)

internal fun Project.optionalStringProvider(
    key: String,
    defaultValue: String? = null
): Provider<String> = safeProperty(key).let { defaultValue?.let { provider { defaultValue } } ?: it }
