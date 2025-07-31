package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import pe.com.scotiabank.blpm.android.ui.databinding.ViewDynamicChipsComponentItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.horizontalscrollview.ScrollOffset

object SaverOfOffsetFromStart {

    @JvmStatic
    fun <D: Any> delegateSave(
        carrier: UiEntityCarrier<UiEntityOfDynamicChipsComponent<D>, ViewDynamicChipsComponentItemBinding>
    ) {
        val entity: UiEntityOfDynamicChipsComponent<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding ->
            ScrollOffset.saveOffsetFromStart(entity, binding.dccChoices)
        }
    }
}
