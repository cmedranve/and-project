package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardBrand
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn.UiEntityOfOneColumnImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

class ConverterOfCardImage(private val brand: AtmCardBrand, private val idRegistry: IdRegistry) {

    fun toUiEntity(isLocked: Boolean) = UiEntityOfOneColumnImage(
        paddingEntity = UiEntityOfPadding(),
        entityOfColumn = UiEntityOfImage(),
        drawableRes = getBrandImage(isLocked),
        id = idRegistry.idOfCardImage,
    )

    @DrawableRes
    private fun getBrandImage(isLocked: Boolean): Int = when {
        isLocked -> R.drawable.ic_card_locked_filled_gray_78
        brand == AtmCardBrand.VISA -> R.drawable.ic_card_visa_filled_red_71
        brand == AtmCardBrand.MASTERCARD -> R.drawable.ic_card_matercard_filled_red_71
        else -> ResourcesCompat.ID_NULL
    }
}