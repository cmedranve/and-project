package pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended

import pe.com.scotiabank.blpm.android.ui.databinding.ViewDoubleEndedImageItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SaverOfRecyclingState

object StateSaverOfDoubleEndedImage {

    @JvmStatic
    fun <I: IdentifiableUiEntity<I>> save(
        carrier: UiEntityCarrier<I, ViewDoubleEndedImageItemBinding>
    ) {
        val entity: I = carrier.uiEntity ?: return

        carrier.weakBinding.get()
            ?.rvCenterItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity, recyclerView) }
    }
}
