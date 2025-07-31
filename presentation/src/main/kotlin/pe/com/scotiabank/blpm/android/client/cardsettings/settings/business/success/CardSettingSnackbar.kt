package pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared

import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R

enum class CardSettingSnackbar(
    @StringRes val textResForEnabling: Int,
    @StringRes val textResForDisabling: Int,
) {

    TEMPORARILY_LOCK(
        textResForEnabling = R.string.card_settings_temporarily_lock_snackbar_on,
        textResForDisabling = R.string.card_settings_temporarily_lock_snackbar_off,
    ),

    ONLINE_SHOPPING(
        textResForEnabling = R.string.card_settings_online_shopping_snackbar_on,
        textResForDisabling = R.string.card_settings_online_shopping_snackbar_off,
    ),

    PURCHASES_ABROAD(
        textResForEnabling = R.string.card_settings_purchases_abroad_snackbar_on,
        textResForDisabling = R.string.card_settings_purchases_abroad_snackbar_off,
    ),

    ATM_WITHDRAWALS(
        textResForEnabling = R.string.card_settings_atm_withdrawals_snackbar_on,
        textResForDisabling = R.string.card_settings_atm_withdrawals_snackbar_off,
    ),

    DISPOSITION_OF_CASH(
        textResForEnabling = R.string.card_settings_disposition_of_cash_snackbar_on,
        textResForDisabling = R.string.card_settings_disposition_of_cash_snackbar_off,
    ),

    OVERDRAFT(
        textResForEnabling = R.string.card_settings_overdraft_snackbar_on,
        textResForDisabling = R.string.card_settings_overdraft_snackbar_off,
    );
}