package pe.com.scotiabank.blpm.android.ui.list.items.inputs.pintext

import pe.com.scotiabank.blpm.android.ui.databinding.ViewPinTextInputViewBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfPinText {

    @JvmStatic
    fun <D: Any> delegateBinding(carrier: UiEntityCarrier<UiEntityOfPinText<D>, ViewPinTextInputViewBinding>) {
        val entity: UiEntityOfPinText<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfPinText<D>, binding: ViewPinTextInputViewBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(
            child = binding.root,
            expectedFlexGrow = UiBinderOfWidthParam.FLEX_GROW_AT_ONE,
        )
        binding.ptiv.setTitle(entity.titleText)
        binding.ptiv.setPinCount(entity.pinCount)
        binding.ptiv.setPin(entity.pin)
    }
}
