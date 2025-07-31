package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import android.content.res.Resources
import androidx.annotation.StyleRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.AtmCardAction
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardButton
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.ConverterOfCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.FactoryOfCardEntity
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.newdashboard.products.StatusProductType
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.card.UiEntityOfCard
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class ConverterOfHubCreditCard(
    private val weakResources: WeakReference<Resources?>,
    private val factoryOfCardEntity: FactoryOfCardEntity,
    private val factoryOfOneColumnTextEntity: FactoryOfOneColumnTextEntity,
    private val converterOfCardInfo: ConverterOfCardInfo,
    private val converterOfCardButton: ConverterOfCardButton,
    private val optionTemplateOfDataButton: OptionTemplate,
) {

    private val paddingEntityOfSubtitle: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_2,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_22,
        )
    }

    fun toUiEntity(product: NewProductModel): UiEntityOfCard<Any> {
        if (product.isInactive) return createEntityForInactiveCard(product)
        return createEntityForActiveCard(product)
    }

    private fun createEntityForInactiveCard(product: NewProductModel): UiEntityOfCard<Any> {

        val cardSubtitleEntity: UiEntityOfOneColumnText = createPendingActivationEntity()
        val atmCardAction = AtmCardAction(Action.ACTIVATE_CARD, product)
        val doubleEndedImageEntity: UiEntityOfDoubleEndedImage<Any> = createDoubleEndedImageEntity(
            cardSubtitleEntity = cardSubtitleEntity,
            product = product,
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
        product: NewProductModel,
        data: Any,
    ): UiEntityOfDoubleEndedImage<Any> = converterOfCardInfo.toUiEntity(
        cardIcon = product.amountColor,
        cardSubtitleEntity = cardSubtitleEntity,
        cardName = product.name,
        data = data,
        isClickable = true,
    )

    private fun createEntityForActiveCard(product: NewProductModel): UiEntityOfCard<Any> {

        val cardSubtitleEntity: UiEntityOfOneColumnText = createCardSubtitleEntity(product)
        val doubleEndedImageEntity: UiEntityOfDoubleEndedImage<Any> = createDoubleEndedImageEntity(
            cardSubtitleEntity = cardSubtitleEntity,
            product = product,
            data = product,
        )

        val textButtonEntities: List<UiEntityOfTextButton<Any>> = createTextButtonEntities(product)

        return factoryOfCardEntity.create(doubleEndedImageEntity, textButtonEntities)
    }

    private fun createCardSubtitleEntity(product: NewProductModel): UiEntityOfOneColumnText {
        if (product.expirationDateDescription.isNullOrEmpty()) {
            return createLastDigitsEntity(product.customerProductNumber)
        }
        return createDebtDescriptionEntity(product)
    }

    private fun createLastDigitsEntity(cardNumber: String): UiEntityOfOneColumnText {
        val lastFourDigits: String = cardNumber.takeLast(Constant.FOUR)
        return factoryOfOneColumnTextEntity.create(
            paddingEntity = paddingEntityOfSubtitle,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_character_count,
            text = weakResources.get()?.getString(R.string.card_last_digits, lastFourDigits).orEmpty(),
        )
    }

    private fun createDebtDescriptionEntity(product: NewProductModel): UiEntityOfOneColumnText {
        @StyleRes val appearance: Int = when (product.statusProductType) {
            StatusProductType.OVERDUE.name -> com.scotiabank.canvascore.R.style.canvascore_style_text_field_error
            else -> pe.com.scotiabank.blpm.android.ui.R.style.canvascore_style_text_field_warning
        }
        return factoryOfOneColumnTextEntity.create(
            paddingEntity = paddingEntityOfSubtitle,
            appearance = appearance,
            text = product.expirationDateDescription,
        )
    }

    private fun createTextButtonEntities(
        product: NewProductModel,
    ): List<UiEntityOfTextButton<Any>> = when {
        isExtraLine(product) -> emptyList()
        else -> createTextButtonEntitiesForActive(product)
    }

    private fun isExtraLine(
        product: NewProductModel
    ): Boolean = Constant.EL.equals(product.subProductType, true)

    private fun createTextButtonEntitiesForActive(
        product: NewProductModel
    ): List<UiEntityOfTextButton<Any>> {

        val entities: MutableList<UiEntityOfTextButton<Any>> = mutableListOf()

        if (optionTemplateOfDataButton.isVisible) {
            val showDataEntity = createShowDataEntity(product)
            entities.add(showDataEntity)
        }

        val configureEntity = createConfigureEntity(product)
        entities.add(configureEntity)

        return entities
    }

    private fun createShowDataEntity(
        product: NewProductModel
    ): UiEntityOfTextButton<Any> = converterOfCardButton.toUiEntity(
        atmCardAction = AtmCardAction(Action.SHOW_CARD_DATA, product)
    )

    private fun createConfigureEntity(
        product: NewProductModel
    ): UiEntityOfTextButton<Any> = converterOfCardButton.toUiEntity(
        atmCardAction = AtmCardAction(Action.CARD_SETTINGS, product),
    )
}
