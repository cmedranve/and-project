package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.AtmCardAction
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardButton
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.FactoryOfCardEntity
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.debitcard.DebitCard
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class ConverterOfHubDebitCard(
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

    fun toUiEntity(debitCard: DebitCard): UiEntityOfCard<Any> {

        val cardSubtitleEntity: UiEntityOfOneColumnText = createLastDigitsEntity(debitCard.number)
        val doubleEndedImageEntity: UiEntityOfDoubleEndedImage<Any> = createDoubleEndedImageEntity(
            cardSubtitleEntity = cardSubtitleEntity,
            debitCard = debitCard,
        )

        val textButtonEntities: List<UiEntityOfTextButton<Any>> = createTextButtonEntities(debitCard)

        return factoryOfCardEntity.create(doubleEndedImageEntity, textButtonEntities)
    }

    private fun createLastDigitsEntity(cardNumber: String): UiEntityOfOneColumnText {
        val lastFourDigits: String = cardNumber.takeLast(Constant.FOUR)
        return factoryOfOneColumnTextEntity.create(
            paddingEntity = paddingEntityOfSubtitle,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_character_count,
            text = weakResources.get()?.getString(R.string.card_last_digits, lastFourDigits).orEmpty(),
        )
    }

    private fun createDoubleEndedImageEntity(
        cardSubtitleEntity: UiEntityOfOneColumnText,
        debitCard: DebitCard,
    ): UiEntityOfDoubleEndedImage<Any> = converterOfCardInfo.toUiEntity(
        cardIcon = debitCard.cardColor.hubIcon,
        cardSubtitleEntity = cardSubtitleEntity,
        cardName = debitCard.name,
        data = debitCard,
        isClickable = true,
    )

    private fun createTextButtonEntities(debitCard: DebitCard): List<UiEntityOfTextButton<Any>> {

        val showDataAction = AtmCardAction(Action.SHOW_CARD_DATA, debitCard)
        val showDataEntity: UiEntityOfTextButton<Any> = converterOfCardButton.toUiEntity(showDataAction)

        val configureAction = AtmCardAction(Action.CARD_SETTINGS, debitCard)
        val configureEntity: UiEntityOfTextButton<Any> = converterOfCardButton.toUiEntity(configureAction)

        return listOf(showDataEntity, configureEntity)
    }
}
