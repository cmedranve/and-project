package pe.com.scotiabank.blpm.android.ui.list.items.loading

import pe.com.scotiabank.blpm.android.ui.databinding.ViewLoadingIndicatorItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfLoadingIndicator {

    @JvmStatic
    fun delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfLoadingIndicator, ViewLoadingIndicatorItemBinding>,
    ) {
        val entity: UiEntityOfLoadingIndicator = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfLoadingIndicator, binding: ViewLoadingIndicatorItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)

        binding.cli.setLoadingIndicatorType(entity.loadingType)
    }
}
