package pe.com.scotiabank.blpm.android.ui.list.items.swipe

enum class SwipeState(
    val isEnabled: Boolean,
    val isRefreshing: Boolean,
) {

    ENABLED(
        isEnabled = true,
        isRefreshing = false,
    ),
    REFRESHING(
        isEnabled = true,
        isRefreshing = true,
    ),
    DISABLED(
        isEnabled = false,
        isRefreshing = false,
    );
}
