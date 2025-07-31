package pe.com.scotiabank.blpm.android.ui.list.items.atmcard

import pe.com.scotiabank.blpm.android.ui.atmcard.AtmCard
import pe.com.scotiabank.blpm.android.ui.databinding.ViewAtmCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.UiBinderOfButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.UiEntityOfButton
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiBinderOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfAtmCard {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfAtmCard<D>, ViewAtmCardItemBinding>
    ) {
        val entity: UiEntityOfAtmCard<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfAtmCard<D>, binding: ViewAtmCardItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindCardView(entity, binding.atmCard)
        bindAtmCardContent(entity, binding.atmCard)
        bindRightActionButton(entity, binding.atmCard)
    }

    @JvmStatic
    private fun <D: Any> bindCardView(entity: UiEntityOfAtmCard<D>, atmCard: AtmCard) {
        UiBinderOfCard.bindBackgroundDrawable(entity.backgroundDrawableRes, atmCard)
        UiBinderOfClickCallback.bindNonClickableOrClickable(entity, entity.receiver, atmCard)
    }

    @JvmStatic
    private fun <D: Any> bindAtmCardContent(entity: UiEntityOfAtmCard<D>, atmCard: AtmCard) {
        atmCard.setBankLogo(entity.bankLogoRes)
        atmCard.setCardName(entity.cardName)
        atmCard.setCardNumber(entity.cardNumber)
        atmCard.setExpiryDate(entity.expiryDateLabel, entity.expiryDateValue)
        atmCard.setCode(entity.codeLabel, entity.codeValue)
        atmCard.setBrandLogo(entity.brandLogoRes)
    }

    @JvmStatic
    private fun <D: Any> bindRightActionButton(entity: UiEntityOfAtmCard<D>, atmCard: AtmCard) {
        atmCard.showRightActionButton(entity.isRightActionGoingToBeVisible)

        val rightActionEntity: UiEntityOfButton<D> = entity.rightActionEntity ?: return
        atmCard.setRightActionButton(rightActionEntity.drawableStartId, rightActionEntity.text)
        UiBinderOfButton.bind(rightActionEntity, atmCard.rightActionButton)
    }
}
