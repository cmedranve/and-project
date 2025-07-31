package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.CardSettings
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType

class InfoByCheckingMapper(private val cardType: AtmCardType) {

    fun toInfoByChecking(
        cardSettings: CardSettings,
    ): LinkedHashMap<CardSettingInfo, Boolean> = when (cardType) {
        AtmCardType.DEBIT -> getInfoByCheckingForDebit(cardSettings)
        AtmCardType.CREDIT -> getInfoByCheckingForCredit(cardSettings)
        AtmCardType.NONE -> linkedMapOf()
    }

    private fun getInfoByCheckingForDebit(
        cardSettings: CardSettings,
    ): LinkedHashMap<CardSettingInfo, Boolean> = linkedMapOf(
        CardSettingInfo.TEMPORARILY_LOCKING to cardSettings.isTempLock,
        CardSettingInfo.ONLINE_SHOPPING to cardSettings.isOnlinePurchase,
        CardSettingInfo.PURCHASE_ABROAD to cardSettings.isForeignPurchase,
        CardSettingInfo.ATM_WITHDRAWAL to cardSettings.isAtmWithdrawal,
    )

    private fun getInfoByCheckingForCredit(
        cardSettings: CardSettings,
    ): LinkedHashMap<CardSettingInfo, Boolean> = linkedMapOf(
        CardSettingInfo.TEMPORARILY_LOCKING to cardSettings.isTempLock,
        CardSettingInfo.ONLINE_SHOPPING to cardSettings.isOnlinePurchase,
        CardSettingInfo.PURCHASE_ABROAD to cardSettings.isForeignPurchase,
        CardSettingInfo.CASH_DISPOSITION to cardSettings.isAtmWithdrawal,
        CardSettingInfo.OVERDRAFT to cardSettings.isOverdraft,
    )
}
