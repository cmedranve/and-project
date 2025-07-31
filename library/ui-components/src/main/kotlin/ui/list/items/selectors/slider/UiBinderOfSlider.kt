package pe.com.scotiabank.blpm.android.ui.list.items.selectors.slider

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.databinding.ViewSliderItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiBinderOfText
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfSlider {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfSlider, ViewSliderItemBinding>) {
        val entity: UiEntityOfSlider = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfSlider, binding: ViewSliderItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        UiBinderOfText.bindTextProperties(entity.entityOfTitle, binding.cs.tvTitle)
        binding.cs.setAttributes(
            entity.formattingCallback,
            entity.maxValueLabel,
            entity.maxValue,
            entity.stepSize,
            entity.minValue,
            entity.initialValue,
            entity.currentValue
        ) { value, isFromUser -> notifyChange(entity, value, isFromUser) }
    }

    @JvmStatic
    private fun notifyChange(entity: UiEntityOfSlider, value: Float, isFromUser: Boolean) {
        entity.currentValue = value
        entity.isValueFromUser = isFromUser
        val receiver: InstanceReceiver = entity.receiver ?: return
        receiver.receive(entity)
    }
}
