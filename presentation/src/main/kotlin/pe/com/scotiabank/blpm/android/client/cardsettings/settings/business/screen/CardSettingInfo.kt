package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import androidx.annotation.StringRes
import androidx.arch.core.util.Function
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared.BuddyTipInfo
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared.CardSettingSnackbar

enum class CardSettingInfo(
    val cardId: Long,
    val switchId: Long,
    @StringRes val titleResId: Int,
    val descriptionResIdForEnabling: Function<AtmCardType, Int>,
    val descriptionResIdForDisabling: Function<AtmCardType, Int>,
    val isOtpRequiredForEnabling: Boolean,
    val isOtpRequiredForDisabling: Boolean,
    val snackbarForSaving: CardSettingSnackbar,
) {

    TEMPORARILY_LOCKING(
        cardId = randomLong(),
        switchId = randomLong(),
        titleResId = R.string.cards_settings_temporarily_lock,
        descriptionResIdForEnabling = Function(::getDescriptionResIdForEnablingLocking),
        descriptionResIdForDisabling = Function(::getDescriptionResIdForDisablingLocking),
        isOtpRequiredForEnabling = false,
        isOtpRequiredForDisabling = true,
        snackbarForSaving = CardSettingSnackbar.TEMPORARILY_LOCK,
    ) {

        override val buddyTipInfo: BuddyTipInfo = BuddyTipInfo(
            id = randomLong(),
            descriptionRes = R.string.card_settings_buddy_tip_description,
            iconRes = com.scotiabank.icons.illustrative.R.drawable.ic_security_outlined_black_30,
            textToBeClickable = R.string.cards_settings_call_now,
        )

        override fun getLimitInfo(type: AtmCardType): List<CardLimitInfo> = emptyList()
    },

    ONLINE_SHOPPING(
        cardId = randomLong(),
        switchId = randomLong(),
        titleResId = R.string.cards_settings_online_shopping,
        descriptionResIdForEnabling = Function(::getDescriptionResIdForOnlineShopping),
        descriptionResIdForDisabling = Function(::getDescriptionResIdForOnlineShopping),
        isOtpRequiredForEnabling = true,
        isOtpRequiredForDisabling = false,
        snackbarForSaving = CardSettingSnackbar.ONLINE_SHOPPING,
    ) {

        override val buddyTipInfo: BuddyTipInfo? = null

        override fun getLimitInfo(type: AtmCardType): List<CardLimitInfo> {
            if (type == AtmCardType.CREDIT) return emptyList()
            return listOf(CardLimitInfo.ONLINE_SHOPPING)
        }
    },

    PURCHASE_ABROAD(
        cardId = randomLong(),
        switchId = randomLong(),
        titleResId = R.string.cards_settings_purchases_abroad,
        descriptionResIdForEnabling = Function(::getDescriptionResIdForPurchaseAbroad),
        descriptionResIdForDisabling = Function(::getDescriptionResIdForPurchaseAbroad),
        isOtpRequiredForEnabling = true,
        isOtpRequiredForDisabling = false,
        snackbarForSaving = CardSettingSnackbar.PURCHASES_ABROAD,
    ) {

        override val buddyTipInfo: BuddyTipInfo? = null

        override fun getLimitInfo(type: AtmCardType): List<CardLimitInfo> {
            if (type == AtmCardType.CREDIT) return emptyList()
            return listOf(CardLimitInfo.PIN_LESS_IN_PERSON)
        }
    },

    ATM_WITHDRAWAL(
        cardId = randomLong(),
        switchId = randomLong(),
        titleResId = R.string.cards_settings_atm_withdrawals_title,
        descriptionResIdForEnabling = Function(::getDescriptionResIdForAtmWithdrawal),
        descriptionResIdForDisabling = Function(::getDescriptionResIdForAtmWithdrawal),
        isOtpRequiredForEnabling = true,
        isOtpRequiredForDisabling = false,
        snackbarForSaving = CardSettingSnackbar.ATM_WITHDRAWALS,
    ) {

        override val buddyTipInfo: BuddyTipInfo? = null

        override fun getLimitInfo(type: AtmCardType): List<CardLimitInfo> {
            if (type == AtmCardType.CREDIT) return emptyList()
            return listOf(CardLimitInfo.ATM_WITHDRAWAL_AT_SCOTIABANK, CardLimitInfo.ATM_WITHDRAWAL_AT_OTHERS)
        }
    },

    CASH_DISPOSITION(
        cardId = randomLong(),
        switchId = randomLong(),
        titleResId = R.string.cards_settings_disposition_of_cash_title,
        descriptionResIdForEnabling = Function(::getDescriptionResIdForCashDisposition),
        descriptionResIdForDisabling = Function(::getDescriptionResIdForCashDisposition),
        isOtpRequiredForEnabling = true,
        isOtpRequiredForDisabling = false,
        snackbarForSaving = CardSettingSnackbar.DISPOSITION_OF_CASH,
    ) {

        override val buddyTipInfo: BuddyTipInfo? = null

        override fun getLimitInfo(type: AtmCardType): List<CardLimitInfo> = emptyList()
    },

    OVERDRAFT(
        cardId = randomLong(),
        switchId = randomLong(),
        titleResId = R.string.txt_credit_card_overdraft,
        descriptionResIdForEnabling = Function(::getDescriptionResIdForOverdraft),
        descriptionResIdForDisabling = Function(::getDescriptionResIdForOverdraft),
        isOtpRequiredForEnabling = true,
        isOtpRequiredForDisabling = false,
        snackbarForSaving = CardSettingSnackbar.OVERDRAFT,
    ) {

        override val buddyTipInfo: BuddyTipInfo? = null

        override fun getLimitInfo(type: AtmCardType): List<CardLimitInfo> = emptyList()
    };

    abstract val buddyTipInfo: BuddyTipInfo?

    abstract fun getLimitInfo(type: AtmCardType): List<CardLimitInfo>
}