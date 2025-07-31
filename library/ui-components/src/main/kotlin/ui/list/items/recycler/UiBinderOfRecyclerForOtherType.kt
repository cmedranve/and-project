package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import androidx.recyclerview.widget.RecyclerView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding

object UiBinderOfRecyclerForOtherType {

    @JvmStatic
    fun bind(
        carrier: UiEntityCarrier<*, *>,
        entity: UiEntityOfRecycler,
        recyclerView: RecyclerView,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, recyclerView)
        StockingOfRecycler.stockWith(carrier, entity, recyclerView)
    }
}
