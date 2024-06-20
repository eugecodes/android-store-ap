package bagels.brands.gradle

import bagels.brands.gradle.util.tomlKey
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionConstraint
import java.util.Optional

/**
 * A set of properties corresponding to *version* aliases in a [catalog]. The keys should be written
 * as they appear in the toml file.
 */
internal class ProjectVersions(private val catalog: VersionCatalog) {

    val jdk: Int
        get() = getValue("jdk").toInt()

    val sortDependencies: String?
        get() = getOptionalValue("sortDependencies").orElse(null)

    internal fun getValue(key: String): String {
        return getOptionalValue(key).orElseThrow {
            IllegalStateException("No catalog version found for ${tomlKey(key)}")
        }
    }

    private fun getOptionalValue(key: String): Optional<String> = catalog.findVersion(key).map(VersionConstraint::toString)
}
