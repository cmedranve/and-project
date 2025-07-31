package pe.com.scotiabank.blpm.android.ui.util

inline fun <T: CharSequence> bindTextIfNotBlank(
    getter: () -> T,
    setter: (T) -> Unit
) {
    if (getter.invoke().isNotBlank()) {
        setter.invoke(
            getter.invoke()
        )
    }
}
