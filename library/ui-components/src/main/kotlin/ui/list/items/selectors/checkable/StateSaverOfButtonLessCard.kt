package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable

import pe.com.scotiabank.blpm.android.ui.databinding.ViewButtonLessCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SaverOfRecyclingState

object StateSaverOfButtonLessCard {

    @JvmStatic
    fun <D: Any> save(
        carrier: UiEntityCarrier<UiEntityOfButtonLessCard<D>, ViewButtonLessCardItemBinding>
    ) {
        val entity: UiEntityOfButtonLessCard<D> = carrier.uiEntity ?: return

        carrier.weakBinding.get()
            ?.rvItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity.recyclerEntity, recyclerView) }
    }
}
