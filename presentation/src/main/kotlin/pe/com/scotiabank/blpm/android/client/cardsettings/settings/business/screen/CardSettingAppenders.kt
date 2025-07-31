package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import androidx.annotation.StringRes
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType

@StringRes
fun getDescriptionResIdForEnablingLocking(type: AtmCardType): Int {
    if (type == AtmCardType.CREDIT) return R.string.card_settings_temporarily_lock_description
    return R.string.card_settings_temporarily_lock_description_debit_on
}

@StringRes
@Suppress("UNUSED_PARAMETER")
fun getDescriptionResIdForDisablingLocking(type: AtmCardType): Int = R.string.card_settings_temporarily_lock_description

@StringRes
@Suppress("UNUSED_PARAMETER")
fun getDescriptionResIdForOnlineShopping(type: AtmCardType): Int = R.string.card_settings_online_shopping_description

@StringRes
@Suppress("UNUSED_PARAMETER")
fun getDescriptionResIdForPurchaseAbroad(type: AtmCardType): Int = R.string.card_settings_purchases_abroad_description

@StringRes
@Suppress("UNUSED_PARAMETER")
fun getDescriptionResIdForAtmWithdrawal(type: AtmCardType): Int = R.string.card_settings_atm_withdrawals_description

@StringRes
@Suppress("UNUSED_PARAMETER")
fun getDescriptionResIdForCashDisposition(type: AtmCardType): Int = R.string.card_settings_cash_disposition_description

@StringRes
@Suppress("UNUSED_PARAMETER")
fun getDescriptionResIdForOverdraft(type: AtmCardType): Int = R.string.card_settings_overdraft_description