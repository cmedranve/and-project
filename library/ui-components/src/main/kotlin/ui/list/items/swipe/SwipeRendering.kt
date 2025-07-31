package pe.com.scotiabank.blpm.android.ui.list.items.swipe

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle

object SwipeRendering {

    @JvmStatic
    fun renderSingleFrom(
        compounds: List<UiCompoundOfSingle<UiEntityOfSwipe>>,
        swipe: SwipeRefreshLayout,
    ) {
        val compound: UiCompoundOfSingle<UiEntityOfSwipe> = compounds
            .firstOrNull() ?: return clearSwipe(swipe)

        bindSwipe(swipe, compound)
    }

    @JvmStatic
    private fun bindSwipe(
        swipe: SwipeRefreshLayout,
        compound: UiCompoundOfSingle<UiEntityOfSwipe>,
    ) {
        val isGoingToBeVisible: Boolean = compound.visibilitySupplier.get()
        if (isGoingToBeVisible.not()) return clearSwipe(swipe)

        UiBinderOfSwipe.bind(compound.entity, swipe)
    }

    @JvmStatic
    private fun clearSwipe(swipe: SwipeRefreshLayout) {
        swipe.isEnabled = false
        swipe.isRefreshing = false
    }
}
