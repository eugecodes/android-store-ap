@file:Suppress("PrivatePropertyName")

package bagels.brands.dependencyrake

import bagels.brands.gradle.property
import bagels.brands.gradle.util.convertProjectPathToAccessor
import bagels.brands.gradle.util.mapToBoolean
import com.autonomousapps.AbstractPostProcessingTask
import com.autonomousapps.advice.PluginAdvice
import com.autonomousapps.model.Advice
import com.autonomousapps.model.Coordinates
import com.autonomousapps.model.FlatCoordinates
import com.autonomousapps.model.IncludedBuildCoordinates
import com.autonomousapps.model.ModuleCoordinates
import com.autonomousapps.model.ProjectCoordinates
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.UntrackedTask
import java.io.File
import javax.inject.Inject

private const val IGNORE_COMMENT = "// dependency-rake=ignore"

private val PREFERRED_BUNDLE_IDENTIFIERS =
    mapOf(
        "com.google.android.play:core" to "com.google.android.play:core-ktx",
    )

// These are dependencies we manage directly or other plugins
private val MANAGED_DEPENDENCIES =
    setOf(
        // Managed by AGP
        "androidx.databinding:viewbinding",
        // Managed by KGP
        "org.jetbrains.kotlin:kotlin-stdlib",
        // Managed by the SGP robolectric DSL feature
        "org.robolectric:shadowapi",
        "org.robolectric:shadows-framework",
    )

internal abstract class RakeDependencies @Inject constructor(objects: ObjectFactory, providers: ProviderFactory) : AbstractPostProcessingTask() {

    @get:Input
    abstract val identifierMap: MapProperty<String, String>

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFile
    abstract val buildFileProperty: RegularFileProperty

    @get:Input
    val modes: SetProperty<AnalysisMode> =
        objects
            .setProperty(AnalysisMode::class.java)
            .convention(
                providers
                    .gradleProperty("bb.dependencyrake.modes")
                    .map { it.splitToSequence(",").map(AnalysisMode::valueOf).toSet() }
                    .orElse(
                        setOf(
                            AnalysisMode.COMPILE_ONLY,
                            AnalysisMode.UNUSED,
                            AnalysisMode.MISUSED,
                            AnalysisMode.PLUGINS,
                            AnalysisMode.ABI
                        )
                    )
            )

    @get:Input
    val dryRun: Property<Boolean> =
        objects
            .property<Boolean>()
            .convention(
                providers.gradleProperty("bbgp.dependencyrake.dryRun").mapToBoolean().orElse(false)
            )

    @get:Input
    abstract val noApi: Property<Boolean>

    @get:OutputFile
    abstract val missingIdentifiersFile: RegularFileProperty

    init {
        group = "rake"
    }

    @TaskAction
    fun rake() {
        if (identifierMap.get().isEmpty()) {
            logger.warn("No identifier map found. Skipping rake.")
            return
        }
        val noApi = noApi.get()
        val projectAdvice = projectAdvice()
        val redundantPlugins = projectAdvice.pluginAdvice
        val advices: Set<Advice> = projectAdvice.dependencyAdvice
        val buildFile = buildFileProperty.asFile.get()
        val missingIdentifiers = mutableSetOf<String>()
        logger.lifecycle("🌲 Raking $buildFile ")
        rakeProject(buildFile, advices, redundantPlugins, noApi, missingIdentifiers)
        val identifiersFile = missingIdentifiersFile.asFile.get()
        if (missingIdentifiers.isNotEmpty()) {
            logger.lifecycle("⚠️ Missing identifiers found, written to $identifiersFile")
        }
        identifiersFile.writeText(missingIdentifiers.sorted().joinToString("\n"))
    }

    @Suppress("LongMethod", "ComplexMethod")
    private fun rakeProject(
        buildFile: File,
        advices: Set<Advice>,
        redundantPlugins: Set<PluginAdvice>,
        noApi: Boolean,
        missingIdentifiers: MutableSet<String>,
    ) {
        val resolvedModes = modes.get()
        val abiModeEnabled = AnalysisMode.ABI in resolvedModes

        val unusedDepsToRemove =
            if (AnalysisMode.UNUSED in resolvedModes) {
                advices
                    .filter { it.isRemove() }
                    .filterNot { it.coordinates.identifier in MANAGED_DEPENDENCIES }
                    .associateBy { it.toDependencyString("UNUSED", missingIdentifiers) }
            } else {
                emptyMap()
            }

        val misusedDepsToRemove =
            if (AnalysisMode.MISUSED in resolvedModes) {
                advices
                    .filter { it.isRemove() }
                    .filterNot { it.coordinates.identifier in MANAGED_DEPENDENCIES }
                    .associateBy { it.toDependencyString("MISUSED", missingIdentifiers) }
            } else {
                emptyMap()
            }

        val depsToRemove = (unusedDepsToRemove + misusedDepsToRemove)

        val depsToChange =
            if (AnalysisMode.ABI in resolvedModes) {
                advices
                    .filter { it.isChange() }
                    .filterNot { it.coordinates.identifier in MANAGED_DEPENDENCIES }
                    .associateBy { it.toDependencyString("CHANGE", missingIdentifiers) }
            } else {
                emptyMap()
            }

        val depsToAdd =
            if (AnalysisMode.MISUSED in resolvedModes) {
                advices
                    .filter { it.isAdd() }
                    .filterNot { it.coordinates.identifier in MANAGED_DEPENDENCIES }
                    .associateBy { it.coordinates.identifier }
                    .toMutableMap()
            } else {
                mutableMapOf()
            }

        val compileOnlyDeps =
            if (AnalysisMode.COMPILE_ONLY in resolvedModes) {
                advices
                    .filter { it.isCompileOnly() }
                    .associateBy { it.toDependencyString("ADD-COMPILE-ONLY", missingIdentifiers) }
            } else {
                emptyMap()
            }

        // Now start rewriting the build file
        val newLines = mutableListOf<String>()
        buildFile.useLines { lines ->
            var inDependenciesBlock = false
            var done = false
            var ignoreNext = false
            lines.forEach { line ->
                if (done) {
                    newLines += line
                    return@forEach
                }
                if (!inDependenciesBlock) {
                    if (line.trimStart().startsWith("dependencies {")) {
                        inDependenciesBlock = true
                    }
                    newLines += line
                    return@forEach
                } else {
                    when {
                        line.trimEnd() == "}" -> {
                            done = true
                            // Emit any remaining new dependencies to add
                            depsToAdd.entries
                                .mapNotNull { (_, advice) ->
                                    advice.coordinates.toDependencyNotation("ADD-NEW", missingIdentifiers)?.let { newNotation ->
                                        var newConfiguration = advice.toConfiguration!!
                                        if (noApi && newConfiguration == "api") {
                                            newConfiguration = "implementation"
                                        }
                                        "  $newConfiguration($newNotation)"
                                    }
                                }
                                .sorted()
                                .forEach {
                                    logger.lifecycle("  ➕ Adding '${it.trimStart()}'")
                                    newLines += it
                                }

                            newLines += line
                            return@forEach
                        }

                        IGNORE_COMMENT in line -> {
                            ignoreNext = true
                            newLines += line
                            return@forEach
                        }

                        ignoreNext -> {
                            ignoreNext = false
                            newLines += line
                            return@forEach
                        }

                        depsToRemove.keys.any { it in line } -> {
                            if (" {" in line) {
                                logger.lifecycle("  🤔 Could not remove '$line'")
                                newLines += line
                                return@forEach
                            }
                            logger.lifecycle("  ⛔ Removing '${line.trimStart()}'")

                            // If this is being swapped with used transitives, inline them here
                            // Note we remove from the depsToAdd list on a first-come-first-serve bases (in case
                            // multiple deps pull the same transitives).
                            advices
                                .filter { it.isAdd() }
                                .mapNotNull { depsToAdd.remove(it.coordinates.identifier) }
                                .mapNotNull { advice ->
                                    advice.coordinates.toDependencyNotation("ADD", missingIdentifiers)?.let { newNotation ->
                                        val newConfiguration =
                                            if (!abiModeEnabled) {
                                                "implementation"
                                            } else {
                                                advice.toConfiguration
                                            }
                                        "  $newConfiguration($newNotation)"
                                    }
                                }
                                .sorted()
                                .forEach { newLines += it }
                            return@forEach
                        }

                        depsToChange.keys.any { it in line } || compileOnlyDeps.keys.any { it in line } -> {
                            if (" {" in line) {
                                logger.lifecycle("  🤔 Could not modify '$line'")
                                newLines += line
                                return@forEach
                            }
                            val which =
                                if (depsToChange.keys.any { it in line }) depsToChange else compileOnlyDeps
                            val (_, abiDep) =
                                which.entries.first { (_, v) ->
                                    v.coordinates.toDependencyNotation("ABI", missingIdentifiers)?.let { it in line }
                                        ?: false
                                }
                            val oldConfiguration = abiDep.fromConfiguration!!
                            var newConfiguration = abiDep.toConfiguration!!
                            if (noApi && newConfiguration == "api") {
                                newConfiguration = "implementation"
                            }
                            // Replace the oldConfiguration name with API
                            val newLine = line.replace("$oldConfiguration(", "$newConfiguration(")
                            logger.lifecycle("  ✏️ Modifying configuration")
                            logger.lifecycle("     -${line.trimStart()}")
                            logger.lifecycle("     +${newLine.trimStart()}")
                            newLines += newLine
                            return@forEach
                        }

                        else -> {
                            newLines += line
                            return@forEach
                        }
                    }
                }
            }
        }

        if (AnalysisMode.PLUGINS in resolvedModes) {
            redundantPlugins.forEach { (id, reason) ->
                val lookFor =
                    if (id.startsWith("org.jetbrains.kotlin.")) {
                        "kotlin(\"${id.removePrefix("org.jetbrains.kotlin.")}\")"
                    } else {
                        "id(\"$id\")"
                    }
                val pluginLine = newLines.indexOfFirst { lookFor == it.trim() }
                if (pluginLine != -1) {
                    logger.lifecycle("Removing unused plugin \'$id\' in $buildFile. $reason")
                    newLines.removeAt(pluginLine)
                }
            }
        }

        val fileToWrite =
            if (!dryRun.get()) {
                buildFile
            } else {
                buildFile.parentFile.resolve("new-build.gradle.kts").apply {
                    if (exists()) {
                        delete()
                    }
                    createNewFile()
                }
            }

        fileToWrite.writeText(newLines.cleanLineFormatting().joinToString("\n"))
    }

    /** Remaps a given [Coordinates] to a known toml lib reference or error if [error] is true. */
    private fun Coordinates.mapIdentifier(
        context: String,
        missingIdentifiers: MutableSet<String>
    ): Coordinates? {
        return when (this) {
            is ModuleCoordinates -> {
                val preferredIdentifier = PREFERRED_BUNDLE_IDENTIFIERS.getOrDefault(identifier, identifier)
                val newIdentifier =
                    identifierMap.get()[preferredIdentifier]
                        ?: run {
                            logger.lifecycle("($context) Unknown identifier: $identifier")
                            missingIdentifiers += identifier
                            return null
                        }
                ModuleCoordinates(newIdentifier, resolvedVersion, gradleVariantIdentification)
            }

            is FlatCoordinates,
            is IncludedBuildCoordinates,
            is ProjectCoordinates -> this
        }
    }

    private fun Advice.toDependencyString(
        context: String,
        missingIdentifiers: MutableSet<String>
    ): String {
        return "${fromConfiguration ?: error("Transitive dep $this")}(${coordinates.toDependencyNotation(context, missingIdentifiers)})"
    }

    private fun Coordinates.toDependencyNotation(
        context: String,
        missingIdentifiers: MutableSet<String>
    ): String? {
        return when (this) {
            is ProjectCoordinates -> "projects.${convertProjectPathToAccessor(identifier)}"
            is ModuleCoordinates -> mapIdentifier(context, missingIdentifiers)?.identifier
            is FlatCoordinates -> gav()
            is IncludedBuildCoordinates -> gav()
        }
    }

    enum class AnalysisMode {
        /** Remove unused dependencies. */
        UNUSED,

        /** Modify dependencies that could be `compileOnly`. */
        COMPILE_ONLY,

        /** Fix dependencies that should be `api`. */
        ABI,

        /** Replace misused dependencies with their transitively-used dependencies. */
        MISUSED,

        /** Remove unused or redundant plugins. */
        PLUGINS
    }
}

private fun List<String>.cleanLineFormatting(): List<String> {
    val cleanedBlankLines = mutableListOf<String>()
    var blankLineCount = 0
    for (newLine in this) {
        if (newLine.isBlank()) {
            if (blankLineCount == 1) {
                // Skip this line
            } else {
                blankLineCount++
                cleanedBlankLines += newLine
            }
        } else {
            blankLineCount = 0
            cleanedBlankLines += newLine
        }
    }

    return cleanedBlankLines.padNewline()
}

private fun List<String>.padNewline(): List<String> {
    val noEmpties = dropLastWhile { it.isBlank() }
    return noEmpties + ""
}

@UntrackedTask(because = "Dependency Rake tasks modify build files")
internal abstract class MissingIdentifiersAggregatorTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputFiles: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = "rake"
        description = "Aggregates missing identifiers from all upstream dependency rake tasks."
    }

    @TaskAction
    fun aggregate() {
        val aggregated = inputFiles.flatMap { it.readLines() }.toSortedSet()

        val output = outputFile.asFile.get()
        logger.lifecycle("Writing aggregated missing identifiers to $output")
        output.writeText(aggregated.joinToString("\n"))
    }

    companion object {
        const val NAME = "aggregateMissingIdentifiers"

        fun register(rootProject: Project): TaskProvider<MissingIdentifiersAggregatorTask> {
            return rootProject.tasks.register(NAME, MissingIdentifiersAggregatorTask::class.java) {
                outputFile.set(
                    rootProject.layout.buildDirectory.file("rake/aggregated_missing_identifiers.txt")
                )
            }
        }
    }
}
