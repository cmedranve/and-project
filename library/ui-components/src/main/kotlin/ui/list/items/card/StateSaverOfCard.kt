package pe.com.scotiabank.blpm.android.ui.list.items.card

import pe.com.scotiabank.blpm.android.ui.databinding.ViewCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SaverOfRecyclingState

object StateSaverOfCard {

    @JvmStatic
    fun <D: Any> save(
        carrier: UiEntityCarrier<UiEntityOfCard<D>, ViewCardItemBinding>
    ) {
        val entity: UiEntityOfCard<D> = carrier.uiEntity ?: return

        carrier.weakBinding.get()
            ?.rvItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity.recyclerEntity, recyclerView) }
    }
}
