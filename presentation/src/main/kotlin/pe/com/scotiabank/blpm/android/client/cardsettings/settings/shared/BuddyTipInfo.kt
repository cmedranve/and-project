package pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class BuddyTipInfo(
    val id: Long,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val textToBeClickable: Int,
)