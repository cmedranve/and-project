package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.AtmCardAction
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardButton
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.FactoryOfCardEntity
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.debitcard.PendingCard
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class ConverterOfHubPendingDebitCard(
    private val weakResources: WeakReference<Resources?>,
    private val factoryOfCardEntity: FactoryOfCardEntity,
    private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity,
    private val converterOfCardInfo: ConverterOfCardInfo,
    private val converterOfCardButton: ConverterOfCardButton,
) {

    private val paddingEntityOfSubtitle: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_2,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_22,
        )
    }

    fun toUiEntity(pendingCard: PendingCard): UiEntityOfCard<Any> {

        val cardSubtitleEntity: UiEntityOfOneColumnText = createPendingActivationEntity()
        val atmCardAction = AtmCardAction(Action.ACTIVATE_CARD, pendingCard)
        val doubleEndedImageEntity: UiEntityOfDoubleEndedImage<Any> = createDoubleEndedImageEntity(
            cardSubtitleEntity = cardSubtitleEntity,
            data = atmCardAction,
        )

        val textButtonEntity: UiEntityOfTextButton<Any> = converterOfCardButton.toUiEntity(
            atmCardAction = atmCardAction,
        )
        val textButtonEntities: List<UiEntityOfTextButton<Any>> = listOf(textButtonEntity)

        return factoryOfCardEntity.create(doubleEndedImageEntity, textButtonEntities)
    }

    private fun createPendingActivationEntity(): UiEntityOfOneColumnText = factoryOfOneColumnTextEntity.create(
        paddingEntity = paddingEntityOfSubtitle,
        appearance = R.style.canvascore_style_character_count_orange,
        text = weakResources.get()?.getString(R.string.pending_activation).orEmpty(),
    )

    private fun createDoubleEndedImageEntity(
        cardSubtitleEntity: UiEntityOfOneColumnText,
        data: Any,
    ): UiEntityOfDoubleEndedImage<Any> = converterOfCardInfo.toUiEntity(
        cardIcon = R.drawable.ic_card_pending,
        cardSubtitleEntity = cardSubtitleEntity,
        cardName = Constant.DIGITAL_DEBIT_MASTERCARD,
        data = data,
        isClickable = true,
    )
}
