package pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn

import pe.com.scotiabank.blpm.android.ui.databinding.ViewTwoColumnTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiBinderOfText
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfTwoColumnText {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfTwoColumnText, ViewTwoColumnTextItemBinding>) {
        val entity: UiEntityOfTwoColumnText = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfTwoColumnText, binding: ViewTwoColumnTextItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(binding.root)
        binding.guideline.setGuidelinePercent(entity.guidelinePercent)
        UiBinderOfText.bind(entity.entityOfColumn1, binding.tvColumn1)
        UiBinderOfText.bind(entity.entityOfColumn2, binding.tvColumn2)
    }
}
