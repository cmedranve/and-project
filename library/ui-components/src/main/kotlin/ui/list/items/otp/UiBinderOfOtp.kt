package pe.com.scotiabank.blpm.android.ui.list.items.otp

import pe.com.scotiabank.blpm.android.ui.databinding.ViewOtpItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfOtp {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfOtp, ViewOtpItemBinding>) {
        val entity: UiEntityOfOtp = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfOtp, binding: ViewOtpItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(
            child = binding.root,
            expectedFlexGrow = UiBinderOfWidthParam.FLEX_GROW_AT_ONE,
        )
        bindTexts(entity, binding)
    }

    @JvmStatic
    private fun bindTexts(entity: UiEntityOfOtp, binding: ViewOtpItemBinding) {
        binding.otpComponent.isEnabled = entity.isEnabled
        binding.otpComponent.eth1.setText(entity.text1)
        binding.otpComponent.eth2.setText(entity.text2)
        binding.otpComponent.eth3.setText(entity.text3)
        binding.otpComponent.eth4.setText(entity.text4)
        binding.otpComponent.eth5.setText(entity.text5)
        binding.otpComponent.eth6.setText(entity.text6)
    }
}
