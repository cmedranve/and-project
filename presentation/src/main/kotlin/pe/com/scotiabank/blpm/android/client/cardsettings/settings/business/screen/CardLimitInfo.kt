package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import androidx.annotation.StringRes
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.debitcard.pending.setting.ToolTipInfo
import pe.com.scotiabank.blpm.android.client.util.Constant

enum class CardLimitInfo(
    @StringRes val titleResId: Int,
    @StringRes val hintResId: Int,
    val currency: Currency,
    val positionFromNetworkCall: Int,
) {

    ONLINE_SHOPPING(
        titleResId = R.string.cards_settings_online_shopping_limit,
        hintResId = R.string.card_settings_limit_hint,
        currency = Currency.USD,
        positionFromNetworkCall = 0,
    ) {

        override val toolTipInfo: ToolTipInfo? = null
    },

    ATM_WITHDRAWAL_AT_SCOTIABANK(
        titleResId = R.string.card_settings_limit_atm_peru,
        hintResId = R.string.card_settings_limit_hint,
        currency = Currency.USD,
        positionFromNetworkCall = 1,
    ) {

        override val toolTipInfo: ToolTipInfo? = null
    },

    ATM_WITHDRAWAL_AT_OTHERS(
        titleResId = R.string.card_settings_limit_atm_abroad,
        hintResId = R.string.card_settings_limit_hint,
        currency = Currency.USD,
        positionFromNetworkCall = 2,
    ) {

        override val toolTipInfo: ToolTipInfo? = null
    },

    PIN_LESS_IN_PERSON(
        titleResId = R.string.card_settings_limit_pin_less_purchase,
        hintResId = R.string.card_settings_limit_hint,
        currency = Currency.USD,
        positionFromNetworkCall = 3,
    ) {

        override val toolTipInfo: ToolTipInfo = ToolTipInfo(
            id = randomLong(),
            accessibilityTextRes = R.string.accessibility_important_notice,
            headlineTextRes = R.string.card_settings_purchase_abroad_tooltip_title,
            contentTextRes = R.string.card_settings_purchase_abroad_tooltip_body,
            buttonTextRes = R.string.understood,
            analyticValue = Constant.EMPTY_STRING,
        )
    };

    abstract val toolTipInfo: ToolTipInfo?
}