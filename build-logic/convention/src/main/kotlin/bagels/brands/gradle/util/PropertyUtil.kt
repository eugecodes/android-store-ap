package bagels.brands.gradle.util

import org.gradle.api.provider.Provider

internal fun Provider<String>.mapToBoolean(): Provider<Boolean> {
    return map(String::toBoolean)
}
