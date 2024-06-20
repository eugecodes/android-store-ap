package bagels.brands.gradle.util

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import javax.inject.Inject

internal fun ProviderFactory.gitVersionProvider(): Provider<String> {
    return gitExecProvider("git", "--version")
}

internal fun ProviderFactory.gitExecProvider(vararg args: String): Provider<String> {
    require(args.isNotEmpty()) { "Args list is empty" }
    return of(GitExecValueSource::class.java) { parameters.args.addAll(*args) }
}

// Adapted from
// https://docs.gradle.org/8.1/userguide/configuration_cache.html#config_cache:requirements:external_processes
internal abstract class GitExecValueSource : ValueSource<String, GitExecValueSource.Parameters> {
    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val args = parameters.args.get()
        check(args.isNotEmpty()) { "Args list is empty" }
        val output = ByteArrayOutputStream()
        execOperations.exec {
            commandLine(args)
            standardOutput = output
        }
        return String(output.toByteArray(), Charset.defaultCharset()).trim { it <= ' ' }
    }

    interface Parameters : ValueSourceParameters {
        val args: ListProperty<String>
    }
}
