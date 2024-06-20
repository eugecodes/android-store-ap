package bagels.brands.gradle

import bagels.brands.gradle.properties.ProjectProperties
import bagels.brands.gradle.tasks.CoreBootstrapTask
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class BasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val globalProperties = ProjectProperties(target)

        if (!target.isRootProject) {
            val bbTools = target.bbTools()
            StandardProjectConfigurations(globalProperties, bbTools).applyTo(target)
            CoreBootstrapTask.configureSubprojectBootstrapTasks(target)
        }
    }
}
