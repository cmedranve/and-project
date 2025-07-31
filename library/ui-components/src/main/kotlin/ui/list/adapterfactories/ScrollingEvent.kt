package pe.com.scotiabank.blpm.android.ui.list.adapterfactories

/**
 * Event class listing the scrolling events ether from RecyclerView.Adapter or from LazyListState.
 * https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyListState
 */
enum class ScrollingEvent {
    /**
     * The event is emitted when RecyclerView.Adapter's last time becomes visible or when
     * LazyListState's canScrollForward returns false.
     *
     * https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyListState#canScrollForward()
     * */
    SCROLLED_TO_MAXIMUM_VALUE
}
