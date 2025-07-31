package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import android.view.Gravity
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText

class CollectorOfOneColumnText(
    private val card: AtmCardInfo,
    private val factory: FactoryOfOneColumnTextEntity,
    private val horizontalPaddingEntity: UiEntityOfPadding,
): CollectorOfOneColumnText {

    private val paddingEntityForTitle: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_16,
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
        )
    }

    private val paddingEntityForDescription: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
        )
    }

    override fun collect(): List<UiEntityOfOneColumnText> {
        val titleEntity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForTitle,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_headline_18,
            text = card.cardName,
            gravity = Gravity.CENTER,
            id = randomLong(),
        )

        val descriptionEntity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForDescription,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_body1_alternate,
            text = createMaskedCardNumber(),
            gravity = Gravity.CENTER,
            id = randomLong(),
        )

        return listOf(titleEntity, descriptionEntity)
    }

    private fun createMaskedCardNumber(): String {
        val asterisks: String = Constant.ASTERIX.repeat(Constant.FOUR)
        val lastDigits: String = card.atmCard.number.takeLast(Constant.FOUR)

        return asterisks + Constant.SPACE_WHITE + lastDigits
    }
}
