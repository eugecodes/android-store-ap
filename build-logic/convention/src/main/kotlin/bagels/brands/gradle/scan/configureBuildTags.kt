package bagels.brands.gradle.scan

import bagels.brands.gradle.invokedFromIde
import bagels.brands.gradle.isSyncing
import bagels.brands.gradle.util.gitExecProvider
import bagels.brands.gradle.util.gitVersionProvider
import bagels.brands.gradle.util.isCi
import bagels.brands.gradle.util.isCircleCI
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.testing.Test
import java.io.IOException
import java.net.URLEncoder

private val GITHUB_ORIGIN_REGEX = Regex("(.*)github\\.com[/|:](.*)")

internal fun Project.configureBuildScanMetadata(scanApi: ScanApi) {
    if (invokedFromIde) {
        scanApi.tag("ide")
        if (isSyncing) {
            scanApi.tag("studio-sync")
        }
    }
    scanApi.value("java-version", JavaVersion.current().toString())
    scanApi.value("Architecture", System.getProperty("os.arch"))
    val isCi = isCi

    scanApi.tagOs()
    scanApi.tagIde(project, isCi)
    scanApi.tagCiOrLocal(isCi)
    if (isCi) {
        scanApi.addCiMetadata(project)
    }
    scanApi.addGitMetadata(project)
    scanApi.addTestParallelization(project)
    scanApi.addGradleEnterpriseVersion()
}

private fun ScanApi.tagOs() {
    tag(System.getProperty("os.name"))
}

private fun ScanApi.tagIde(project: Project, isCi: Boolean) {
    if (project.hasProperty("android.injected.invoked.from.ide")) {
        tag("Android Studio")
        project.findProperty("android.injected.studio.version")?.let {
            value("Android Studio version", it.toString())
        }
    } else if (System.getProperty("idea.version") != null) {
        tag("IntelliJ IDEA")
    } else if (!isCi) {
        tag("Cmd Line")
    }
}

private fun ScanApi.tagCiOrLocal(isCi: Boolean) {
    tag(if (isCi) "CI" else "LOCAL")
}

private fun ScanApi.addCiMetadata(project: Project) {
    if (project.isCircleCI) {
        if (System.getenv("CIRCLE_BUILD_URL") != null) {
            link("CircleCI build", System.getenv("CIRCLE_BUILD_URL"))
        }
        if (System.getenv("CIRCLE_JOB") != null) {
            val workflowNameLabel = "CircleCI job"
            val workflowName = System.getenv("CIRCLE_JOB")
            value(workflowNameLabel, workflowName)
            addCustomLinkWithSearchTerms(
                "CircleCI job build scans",
                mapOf(workflowNameLabel to workflowName)
            )
        }
    }
}

private fun ScanApi.addGitMetadata(project: Project) {
    val providers = project.providers
    background {
        if (!isGitInstalled(providers)) {
            return@background
        }

        val gitCommitId = providers.gitExecProvider("git", "rev-parse", "--short=8", "--verify", "HEAD").orNull
        val gitBranchName = providers.gitExecProvider("git", "rev-parse", "--abbrev-ref", "HEAD").orNull

        val gitStatus = providers.gitExecProvider("git", "status", "--porcelain").orNull

        if (gitCommitId != null) {
            val gitCommitIdLabel = "Git commit id"
            value(gitCommitIdLabel, gitCommitId)
            addCustomLinkWithSearchTerms(
                "Git commit id build scans",
                mapOf(gitCommitIdLabel to gitCommitId)
            )

            val originUrl =
                providers.gitExecProvider("git", "config", "--get", "remote.origin.url").orNull
            if (originUrl != null) {
                if ("github.com/" in originUrl || "github.com:" in originUrl) {
                    GITHUB_ORIGIN_REGEX.find(originUrl)?.groups?.get(2)?.value?.removeSuffix(".git")?.let { repoPath ->
                        link("Github Source", "https://github.com/$repoPath/tree/$gitCommitId")
                    }
                }
            }
        }
        if (gitBranchName != null) {
            tag(gitBranchName)
            value("Git branch", gitBranchName)
        }
        if (gitStatus != null) {
            tag("Dirty")
            value("Git status", gitStatus)
        }
    }
}

internal fun ScanApi.addTestParallelization(project: Project) {
    project.tasks.withType(Test::class.java).configureEach {
        doFirst { value("$identityPath#maxParallelForks", maxParallelForks.toString()) }
    }
}

private fun ScanApi.addGradleEnterpriseVersion() {
    javaClass.classLoader
        .getResource("com.gradle.scan.plugin.internal.meta.buildAgentVersion.txt")
        ?.readText()
        ?.let { buildAgentVersion -> value("GE Gradle plugin version", buildAgentVersion) }
}

private fun ScanApi.addCustomLinkWithSearchTerms(title: String, search: Map<String, String>) {
    server?.let {
        val searchParams = customValueSearchParams(search)
        val url =
            "${it.appendIfMissing("/")}scans?$searchParams#selection.buildScanB=${urlEncode("{SCAN_ID}")}"
        link(title, url)
    }
}

private fun customValueSearchParams(search: Map<String, String>): String {
    return search.entries.joinToString("&") { (name, value) ->
        "search.names=${urlEncode(name)}&search.values=${urlEncode(value)}"
    }
}

private fun String.appendIfMissing(suffix: String): String {
    return if (endsWith(suffix)) this else this + suffix
}

private fun urlEncode(url: String): String {
    return URLEncoder.encode(url, Charsets.UTF_8.name())
}

private fun isGitInstalled(providers: ProviderFactory): Boolean {
    return try {
        providers.gitVersionProvider().isPresent
        true
    } catch (ignored: IOException) {
        false
    }
}
