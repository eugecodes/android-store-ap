package bagels.brands.gradle

import com.android.builder.model.AndroidProject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.reflect.TypeOf
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

internal val Project.isRootProject: Boolean
    get() = rootProject === this

internal inline fun <reified T : Task> TaskContainer.register(
    name: String,
    configuration: Action<in T>
): TaskProvider<T> = register(name, T::class.java, configuration)

internal inline fun <reified T : Any> Project.configure(action: Action<T>) {
    extensions.getByType<T>().apply(action::execute)
}

internal inline fun <reified T> ExtensionContainer.findByType(): T? {
    // Gradle, Kotlin, and Java all have different notions of what a "type" is.
    // I'm sorry
    return findByType(TypeOf.typeOf(typeOf<T>().javaType))
}

internal inline fun <reified T> ExtensionContainer.getByType(): T {
    // Gradle, Kotlin, and Java all have different notions of what a "type" is.
    // I'm sorry
    return getByType(TypeOf.typeOf(typeOf<T>().javaType))
}

/**
 * Returns true if this execution of Gradle is for an Android Studio Gradle Sync. We're considering
 * both the no-task invocation of Gradle that AS uses to build its model, and the invocation of
 * "generateXSources" for each project that follows it. (We may want to track these in the future
 * too, but for now they're pretty noisy.)
 */
internal val Project.isSyncing: Boolean
    get() =
        invokedFromIde &&
            (findProperty(AndroidProject.PROPERTY_BUILD_MODEL_ONLY) == "true" ||
                findProperty(AndroidProject.PROPERTY_GENERATE_SOURCES_ONLY) == "true")

// Note that we don't reference the AndroidProject property because this constant moved in AGP 7.2
public val Project.invokedFromIde: Boolean
    get() = hasProperty("android.injected.invoked.from.ide")

internal inline fun <reified T : Any> ObjectFactory.property(): Property<T> = property(T::class.java)

internal inline fun <reified E : Any> ObjectFactory.listProperty(): ListProperty<E> = listProperty(E::class.java)
