package bagels.brands.gradle

import bagels.brands.gradle.properties.ProjectProperties
import org.gradle.api.Project

/** Registry of global configuration info. */
public class GlobalConfig private constructor(internal val kotlinDaemonArgs: List<String>) {

    internal companion object {
        operator fun invoke(project: Project): GlobalConfig {
            check(project == project.rootProject) { "Project is not root project!" }
            val globalProperties = ProjectProperties(project)
            return GlobalConfig(
                kotlinDaemonArgs = globalProperties.kotlinDaemonArgs.split(" "),
            )
        }
    }
}
