package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R

enum class CardSettingAction(
    @StringRes val labelRes: Int,
    val analyticLabel: String,
) {

    CALL_NOW(
        labelRes = R.string.cards_settings_call_now,
        analyticLabel = "llamar-ahora",
    ),
    REGISTER_TRAVEL(
        labelRes = R.string.card_settings_warn_travel,
        analyticLabel = "registrarme",
    ),
    WHY_DO_I_HAVE_TO_REGISTER_TRAVEL(
        labelRes = R.string.card_settings_why_warn_travel,
        analyticLabel = "por-que-tengo-que-hacer-esto",
    ),
}
