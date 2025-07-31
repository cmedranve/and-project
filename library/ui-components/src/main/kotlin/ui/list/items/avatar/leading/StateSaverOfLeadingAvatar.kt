package pe.com.scotiabank.blpm.android.ui.list.items.avatar.leading

import pe.com.scotiabank.blpm.android.ui.databinding.ViewLeadingAvatarItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SaverOfRecyclingState

object StateSaverOfLeadingAvatar {

    @JvmStatic
    fun <I: IdentifiableUiEntity<I>> save(
        carrier: UiEntityCarrier<I, ViewLeadingAvatarItemBinding>,
    ) {
        val entity: I = carrier.uiEntity ?: return

        carrier.weakBinding.get()
            ?.rvCenterItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity, recyclerView) }
    }
}
