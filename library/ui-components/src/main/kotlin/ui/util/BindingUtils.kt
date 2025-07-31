package pe.com.scotiabank.blpm.android.ui.util

inline fun <T: Any> bindIfDifferent(
    expectedValue: T,
    getter: () -> T,
    setter: (T) -> Unit
) {
    if (expectedValue != getter.invoke()) {
        setter.invoke(expectedValue)
    }
}
