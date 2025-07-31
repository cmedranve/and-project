package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import android.content.res.Resources
import androidx.annotation.DrawableRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.AtmCardAction
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardButton
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.FactoryOfCardEntity
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus.LOCKED
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class ConverterOfAnyCard(
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

    fun toUiEntity(card: AtmCardInfo): UiEntityOfCard<Any> {

        val cardSubtitleEntity: UiEntityOfOneColumnText = createLastDigitsEntity(card.atmCard.number)
        val doubleEndedImageEntity: UiEntityOfDoubleEndedImage<Any> = createDoubleEndedImageEntity(
            cardSubtitleEntity = cardSubtitleEntity,
            card = card,
        )

        val textButtonEntities: List<UiEntityOfTextButton<Any>> = createTextButtonEntities(card)

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
        card: AtmCardInfo,
    ): UiEntityOfDoubleEndedImage<Any> {

        val isLocked: Boolean = card.atmCard.status == LOCKED
        @DrawableRes val iconCard: Int = if (isLocked) R.drawable.ic_card_locked else card.atmCard.color.hubIcon

        return converterOfCardInfo.toUiEntity(
            cardIcon = iconCard,
            cardSubtitleEntity = cardSubtitleEntity,
            cardName = card.cardName,
            data = card,
            isClickable = false,
        )
    }

    private fun createTextButtonEntities(card: AtmCardInfo): List<UiEntityOfTextButton<Any>> {

        val entities: MutableList<UiEntityOfTextButton<Any>> = mutableListOf()

        if (card.atmCard.status != LOCKED) {
            val showDataAction = AtmCardAction(Action.SHOW_CARD_DATA, card)
            val showDataEntity: UiEntityOfTextButton<Any> = converterOfCardButton.toUiEntity(showDataAction)
            entities.add(showDataEntity)
        }

        val configureAction = AtmCardAction(Action.CARD_SETTINGS, card)
        val configureEntity: UiEntityOfTextButton<Any> = converterOfCardButton.toUiEntity(configureAction)
        entities.add(configureEntity)

        return entities
    }
}
