package pe.com.scotiabank.blpm.android.ui.list.items.footer

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import pe.com.scotiabank.blpm.android.ui.databinding.ViewFooterItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.animation.DisablerOfChangeAnimation
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.RestorerOfRecyclingState
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfLinearLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.RecyclerRendering

object StockingOfFooter {

    @JvmStatic
    fun stockWith(
        carrier: UiEntityCarrier<UiEntityOfFooter, ViewFooterItemBinding>,
        entity: UiEntityOfFooter,
    ) {
        attemptInitialize(carrier, entity)
        val rvFooterItems: RecyclerView = carrier.weakBinding.get()
            ?.rvFooterItems
            ?: return

        RecyclerRendering.renderFrom(
            compoundsById = entity.compoundsById,
            scrollingEventHandler = carrier.scrollingEventHandler,
            viewPool = carrier.viewPool,
            recyclerView = rvFooterItems,
        )
    }

    @JvmStatic
    private fun attemptInitialize(
        carrier: UiEntityCarrier<UiEntityOfFooter, ViewFooterItemBinding>,
        entity: UiEntityOfFooter,
    ) {
        val isInitialized: Boolean = carrier.weakBinding.get()
            ?.rvFooterItems
            ?.layoutManager is LayoutManager
        if (isInitialized) return

        initialize(carrier, entity)
    }

    @JvmStatic
    private fun initialize(
        carrier: UiEntityCarrier<UiEntityOfFooter, ViewFooterItemBinding>,
        entity: UiEntityOfFooter,
    ) {
        carrier.weakBinding.get()?.rvFooterItems?.clipToPadding = false
        carrier.weakBinding.get()?.rvFooterItems?.isNestedScrollingEnabled = false
        carrier.weakBinding.get()?.rvFooterItems?.let(DisablerOfChangeAnimation::disable)
        carrier.weakBinding.get()?.rvFooterItems?.setRecycledViewPool(carrier.viewPool)

        val estimateNumberOfVisibleItems: Int = entity.totalSizeOfEntities
        val layoutManagerFactory = FactoryOfLinearLayoutManager()
        val layoutManager: LayoutManager = carrier.weakBinding.get()
            ?.rvFooterItems
            ?.let { recyclerView -> layoutManagerFactory.create(estimateNumberOfVisibleItems, recyclerView) }
            ?: return

        carrier.weakBinding.get()?.rvFooterItems?.layoutManager = layoutManager
        RestorerOfRecyclingState.restore(layoutManager, entity.recyclingState)
    }
}
