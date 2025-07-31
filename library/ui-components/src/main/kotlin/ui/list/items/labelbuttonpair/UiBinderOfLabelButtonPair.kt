package pe.com.scotiabank.blpm.android.ui.list.items.labelbuttonpair

import pe.com.scotiabank.blpm.android.ui.databinding.ViewLabelButtonPairItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiBinderOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiBinderOfText
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfLabelButtonPair {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfLabelButtonPair<D>, ViewLabelButtonPairItemBinding>,
    ) {
        val entity: UiEntityOfLabelButtonPair<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfLabelButtonPair<D>, binding: ViewLabelButtonPairItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(binding.root)
        UiBinderOfText.bind(entity.labelEntity, binding.tvColumn1)
        UiBinderOfTextButton.updateMinimumDimensionsOf(binding.tbColumn2)
        UiBinderOfTextButton.bindClickableContent(entity.buttonEntity, binding.tbColumn2)
    }
}
