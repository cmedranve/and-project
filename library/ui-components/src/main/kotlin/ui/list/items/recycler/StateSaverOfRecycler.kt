package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import pe.com.scotiabank.blpm.android.ui.databinding.ViewRecyclerItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SaverOfRecyclingState

object StateSaverOfRecycler {

    @JvmStatic
    fun save(carrier: UiEntityCarrier<UiEntityOfRecycler, ViewRecyclerItemBinding>) {
        val entity: UiEntityOfRecycler = carrier.uiEntity ?: return

        carrier.weakBinding.get()
            ?.rvItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity, recyclerView) }
    }
}
