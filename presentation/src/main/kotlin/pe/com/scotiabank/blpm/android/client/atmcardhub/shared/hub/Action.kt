package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R

enum class Action(
    val id: Long,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val eventLabel: String
) {
    SHOW_CARD_DATA(
        id = randomLong(),
        titleRes = R.string.show_data,
        iconRes = com.scotiabank.icons.illustrative.R.drawable.ic_show_hide_show_outlined_black_24,
        eventLabel = "ver-datos"
    ),
    CARD_SETTINGS(
        id = randomLong(),
        titleRes = R.string.configure,
        iconRes = com.scotiabank.icons.illustrative.R.drawable.ic_settings_outlined_black_24,
        eventLabel = "configurar"
    ),
    ACTIVATE_CARD(
        id = randomLong(),
        titleRes = R.string.activate_now,
        iconRes = com.scotiabank.icons.illustrative.R.drawable.ic_cvv_outlined_black_24,
        eventLabel = "activar-ahora"
    ),
    CALL_NOW(
        id = randomLong(),
        titleRes = R.string.cards_settings_call_now,
        iconRes = ResourcesCompat.ID_NULL,
        eventLabel = "llamar-ahora"
    ),
}
