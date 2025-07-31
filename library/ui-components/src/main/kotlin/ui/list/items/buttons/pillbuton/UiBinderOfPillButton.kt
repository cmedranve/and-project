package pe.com.scotiabank.blpm.android.ui.list.items.buttons.pillbutton

import com.scotiabank.canvascore.buttons.PillButton
import pe.com.scotiabank.blpm.android.ui.databinding.ViewPillButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfPillButton {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfPillButton<D>, ViewPillButtonItemBinding>,
    ) {
        val entity: UiEntityOfPillButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfPillButton<D>, binding: ViewPillButtonItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        binding.pButton.setPillType(entity.type)
        bindContent(entity, binding.pButton)
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, binding.pButton)
    }

    @JvmStatic
    private fun <D: Any> bindContent(entity: UiEntityOfPillButton<D>, pButton: PillButton) {
        pButton.isEnabled = entity.isEnabled
        pButton.text = entity.text
    }
}
