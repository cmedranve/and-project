package pe.com.scotiabank.blpm.android.ui.list.items.swipe

import androidx.core.content.res.ResourcesCompat

interface SwipeController {

    fun editSwipe(
        state: SwipeState,
        backgroundColorRes: Int = ResourcesCompat.ID_NULL,
        colorSchemaRes: List<Int> = emptyList(),
    )

    fun editSwipeState(state: SwipeState)
}
