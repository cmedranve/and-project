package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.animation.DisablerOfChangeAnimation
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationRendering
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.RestorerOfRecyclingState

object StockingOfRecycler {

    @JvmStatic
    fun stockWith(
        carrier: UiEntityCarrier<*, *>,
        entity: UiEntityOfRecycler,
        recyclerView: RecyclerView,
    ) {
        attemptInitialize(carrier, entity, recyclerView)

        RecyclerRendering.renderFrom(
            compoundsById = entity.compoundsById,
            scrollingEventHandler = carrier.scrollingEventHandler,
            viewPool = carrier.viewPool,
            recyclerView = recyclerView,
        )
    }

    @JvmStatic
    private fun attemptInitialize(
        carrier: UiEntityCarrier<*, *>,
        entity: UiEntityOfRecycler,
        recyclerView: RecyclerView,
    ) {
        val isInitialized: Boolean = recyclerView.layoutManager is LayoutManager
        if (isInitialized) return

        initialize(carrier, entity, recyclerView)
    }

    @JvmStatic
    private fun initialize(
        carrier: UiEntityCarrier<*, *>,
        entity: UiEntityOfRecycler,
        recyclerView: RecyclerView,
    ) {
        renderDecorations(entity.decorationCompounds, recyclerView)

        recyclerView.clipToPadding = entity.clipToPadding
        recyclerView.isNestedScrollingEnabled = entity.isNestedScrollingEnabled
        DisablerOfChangeAnimation.disable(recyclerView)
        recyclerView.setRecycledViewPool(carrier.viewPool)

        val layoutManagerFactory: LayoutManagerFactory = entity.layoutManagerFactory
        val layoutManager: LayoutManager = layoutManagerFactory.create(
            estimateNumberOfVisibleItems = entity.totalSizeOfEntities,
            recyclerView = recyclerView,
        )

        recyclerView.layoutManager = layoutManager
        RestorerOfRecyclingState.restore(layoutManager, entity.recyclingState)
    }

    @JvmStatic
    private fun renderDecorations(
        compounds: List<DecorationCompound>,
        recyclerView: RecyclerView,
    ) {
        compounds.forEach { compound -> renderDecoration(compound, recyclerView) }
        recyclerView.invalidateItemDecorations()
    }

    @JvmStatic
    private fun renderDecoration(
        compound: DecorationCompound,
        recyclerView: RecyclerView,
    ) {
        val rendering: DecorationRendering = compound.rendering
        rendering.render(recyclerView, compound.resId, compound.positions)
    }
}
