package bagels.brands.gradle

import bagels.brands.gradle.BBTools.Companion.SERVICE_NAME
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.services.BuildServiceRegistration

/** Misc tools for BB Gradle projects, usable in tasks as a [BuildService] too. */
public abstract class BBTools : BuildService<BBTools.Parameters>, AutoCloseable {
    // I really really wish we could do this the "correct" way but Gradle is problematic with its
    // inconsistent expectations of Serializability. Specifically - it seems that `@Nested` does not
    // work for BuildService parameters
    public lateinit var globalConfig: GlobalConfig

    private val logger = Logging.getLogger("BBTools")

    init {
        logger.debug("BBTools created")
    }

    override fun close() {
    }

    public companion object {
        public const val SERVICE_NAME: String = "BBTools"

        internal fun register(
            project: Project,
        ): Provider<BBTools> = project.gradle.sharedServices
            .registerIfAbsent(SERVICE_NAME, BBTools::class.java) {}
            .apply { get().apply { globalConfig = GlobalConfig(project) } }
    }

    public interface Parameters : BuildServiceParameters
}

public fun Project.bbTools(): BBTools = bbToolsProvider().get()

@Suppress("UNCHECKED_CAST")
public fun Project.bbToolsProvider(): Provider<BBTools> = (project.gradle.sharedServices.registrations.getByName(SERVICE_NAME)
    as BuildServiceRegistration<BBTools, BBTools.Parameters>)
    .service
