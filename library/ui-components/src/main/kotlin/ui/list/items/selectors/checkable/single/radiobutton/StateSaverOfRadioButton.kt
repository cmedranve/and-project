package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.radiobutton

import pe.com.scotiabank.blpm.android.ui.databinding.ViewRadioButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SaverOfRecyclingState

object StateSaverOfRadioButton {

    @JvmStatic
    fun <D: Any> save(
        carrier: UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonItemBinding>
    ) {
        val entity: UiEntityOfCheckableButton<D> = carrier.uiEntity ?: return

        carrier.weakBinding.get()
            ?.rvSideItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity.sideRecyclerEntity, recyclerView) }
        carrier.weakBinding.get()
            ?.rvBottomItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity.bottomRecyclerEntity, recyclerView) }
    }
}
