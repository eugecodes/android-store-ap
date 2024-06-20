package bagels.brands.gradle.properties

import bagels.brands.gradle.ProjectVersions
import bagels.brands.gradle.util.getVersionsCatalog
import org.gradle.api.Project
import org.gradle.api.provider.Provider

public class ProjectProperties private constructor(private val project: Project) {

    private fun booleanProperty(key: String, defaultValue: Boolean = false): Boolean =
        project.booleanValue(key, defaultValue = defaultValue)

    private fun stringProperty(key: String, defaultValue: String): String =
        optionalStringProperty(key, defaultValue)!!

    private fun optionalStringProperty(key: String, defaultValue: String? = null): String? =
        project.optionalStringProperty(key, defaultValue = defaultValue)

    internal val versions: ProjectVersions by lazy {
        project.rootProject.getOrCreateExtra("bbgp-versions") {
            ProjectVersions(project.rootProject.getVersionsCatalog())
        }
    }

    /**
     * Indicates that the gradle versions plugin should allow unstable versions. By default unstable
     * versions are excluded due to the frequent androidx alpha/beta/rc cycle noise. Flag-only, value
     * is ignored.
     */
    public val versionsPluginAllowUnstable: Boolean
        get() = booleanProperty("bbgp.gradle.config.versionsPluginAllowUnstable")

    /**
     * Flag to enable the Gradle Dependency Analysis Plugin
     */
    public val enableAnalysisPlugin: Boolean
        get() = booleanProperty("bbgp.gradle.config.enableAnalysisPlugin")

    /**
     * The BB-specific kotlin.daemon.jvmargs computed by bootstrap.
     *
     * We don't just blanket use `kotlin.daemon.jvmargs` alone because we don't want to pollute other
     * projects.
     */
    public val kotlinDaemonArgs: String
        get() = stringProperty(KOTLIN_DAEMON_ARGS_KEY, defaultValue = "")

    /** The JDK version to use for compilations. */
    public val jdkVersion: Int
        get() = versions.jdk


    public val autoApplySortDependencies: Boolean
        get() = booleanProperty("bbgp.auto-apply.sort-dependencies", defaultValue = true)

    /** Comma-separated set of projects to ignore in sorting dependencies. */
    public val sortDependenciesIgnore: String?
        get() = optionalStringProperty("bbgp.sortDependencies.ignore")

    /**
     * Enables applying common build tags.first-party plugin.
     */
    public val applyCommonBuildTags: Boolean
        get() = project.booleanValue("bbgp.ge.apply-common-build-tags", defaultValue = true)

    /** Defines a required vendor for JDK toolchains. */
    public val jvmVendor: Provider<String>
        get() =
            project.optionalStringProvider("bbgp.config.jvmVendor").map {
                if (jvmVendorOptOut) {
                    sneakyNull()
                } else {
                    it
                }
            }

    /** Flag to disable JVM vendor setting locally. */
    private val jvmVendorOptOut: Boolean
        get() = booleanProperty("bbgp.config.jvmVendor.optOut", defaultValue = false)

    public companion object {
        /**
         * The BB-specific kotlin.daemon.jvmargs computed by bootstrap.
         *
         * We don't just blanket use `kotlin.daemon.jvmargs` alone because we don't want to pollute
         * other projects.
         */
        public const val KOTLIN_DAEMON_ARGS_KEY: String = "bagels.kotlin.daemon.jvmargs"

        /** Minimum xmx value for the Gradle daemon. Value is an integer and unit is gigabytes. */
        // Key-only because it's used in a task init without a project instance
        public const val MIN_GRADLE_XMX: String = "bagels.bootstrap.minGradleXmx"

        private const val CACHED_PROVIDER_EXT_NAME = "bagels.properties.provider"

        public operator fun invoke(project: Project): ProjectProperties =
            project.getOrCreateExtra(CACHED_PROVIDER_EXT_NAME, ::ProjectProperties)
    }
}
