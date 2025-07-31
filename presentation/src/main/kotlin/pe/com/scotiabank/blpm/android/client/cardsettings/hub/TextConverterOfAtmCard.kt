package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.content.res.Resources
import android.text.TextUtils.TruncateAt
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.model.CardModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import java.lang.ref.WeakReference

class TextConverterOfAtmCard(
    private val weakResources: WeakReference<Resources?>,
    private val factory: FactoryOfOneColumnTextEntity,
    @StyleRes private val cardNameAppearance: Int,
    @StyleRes private val cardNumberAppearance: Int,
    @DimenRes private val bottomPaddingOfCardNumber: Int,
) {

    private val paddingEntityOfCardName: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_22,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_2,
        )
    }

    private val paddingEntityOfCardNumber: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_2,
            bottom = bottomPaddingOfCardNumber,
        )
    }

    fun toUiEntities(card: Card): List<UiEntityOfOneColumnText> {
        val cardNameEntity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityOfCardName,
            appearance = cardNameAppearance,
            text = card.name,
            maxLines = Constant.ONE,
            whereToEllipsize = TruncateAt.END,
        )

        val lastFourDigits: String = card.number.takeLast(Constant.FOUR)
        val cardNumberEntity: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityOfCardNumber,
            appearance = cardNumberAppearance,
            text = weakResources.get()?.getString(R.string.card_last_digits, lastFourDigits).orEmpty(),
        )

        return listOf(cardNameEntity, cardNumberEntity)
    }
}
