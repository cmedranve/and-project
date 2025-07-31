package pe.com.scotiabank.blpm.android.ui.list.items.progressindicator.linear

import pe.com.scotiabank.blpm.android.ui.databinding.ViewProgressIndicatorItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfProgressIndicator {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfProgressIndicator<D>, ViewProgressIndicatorItemBinding>,
    ) {
        val entity: UiEntityOfProgressIndicator<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfProgressIndicator<D>,
        binding: ViewProgressIndicatorItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        binding.cpiInfo.setAttributes(
            initialValue = entity.progressValue,
            maxValue = entity.maxValue,
            color = entity.color,
        )
    }
}
