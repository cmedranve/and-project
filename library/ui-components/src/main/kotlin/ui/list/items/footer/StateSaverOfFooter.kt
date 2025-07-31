package pe.com.scotiabank.blpm.android.ui.list.items.footer

import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.view.updateLayoutParams
import pe.com.scotiabank.blpm.android.ui.databinding.ViewFooterItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.SaverOfRecyclingState

object StateSaverOfFooter {

    @JvmStatic
    fun <I: IdentifiableUiEntity<I>> save(
        carrier: UiEntityCarrier<I, ViewFooterItemBinding>
    ) {
        val entity: I = carrier.uiEntity ?: return

        carrier.weakBinding.get()?.sFooter?.updateLayoutParams<LayoutParams> {
            width = LayoutParams.MATCH_CONSTRAINT
            height = 0
        }
        carrier.weakBinding.get()
            ?.rvFooterItems
            ?.let { recyclerView -> SaverOfRecyclingState.save(entity, recyclerView) }
    }
}
