package pe.com.scotiabank.blpm.android.ui.list.items.skeleton.doubleended

import pe.com.scotiabank.blpm.android.ui.databinding.ViewDoubleEndedSkeletonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SaverOfRecyclingState

object StateSaverOfDoubleEndedSkeleton {

    @JvmStatic
    fun <I: IdentifiableUiEntity<I>> save(
        carrier: UiEntityCarrier<I, ViewDoubleEndedSkeletonItemBinding>
    ) {
        val entity: I = carrier.uiEntity ?: return

        carrier.weakBinding.get()
            ?.rvCenterItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity, recyclerView) }
    }
}
