package pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn

import pe.com.scotiabank.blpm.android.ui.databinding.ViewOneColumnTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiBinderOfText
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfOneColumnText {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfOneColumnText, ViewOneColumnTextItemBinding>) {
        val entity: UiEntityOfOneColumnText = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfOneColumnText, binding: ViewOneColumnTextItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        UiBinderOfText.bind(entity.entityOfColumn, binding.tvColumn)
    }
}
