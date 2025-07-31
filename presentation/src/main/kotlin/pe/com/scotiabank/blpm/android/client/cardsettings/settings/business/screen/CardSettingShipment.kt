package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.BusinessCardSettingsRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.LimitEntity

class CardSettingShipment private constructor(val requestEntity: BusinessCardSettingsRequestEntity) {

    class Factory(
        private val card: AtmCardInfo,
        private val store: CardSettingStore,
    ) : SettingsByInfoHolder by store {

        private val typeFromNetworkCall: String by lazy {
            card.atmCard.type.nameFromNetworkCall
        }

        private val tempLockSetting: Setting?
            get() = settingByInfo[CardSettingInfo.TEMPORARILY_LOCKING]

        private val overdraftSetting: Setting?
            get() = settingByInfo[CardSettingInfo.OVERDRAFT]

        private val onlinePurchaseSetting: Setting?
            get() = settingByInfo[CardSettingInfo.ONLINE_SHOPPING]

        private val foreignPurchaseSetting: Setting?
            get() = settingByInfo[CardSettingInfo.PURCHASE_ABROAD]

        private val atmWithdrawalSetting: Setting?
            get() = settingByInfo[CardSettingInfo.ATM_WITHDRAWAL]

        fun attemptCreate(operationId: String): CardSettingShipment {

            val limitList: MutableList<LimitEntity> = mutableListOf()
            limitList.addAll(onlinePurchaseSetting?.limitDataEntities.orEmpty())
            limitList.addAll(foreignPurchaseSetting?.limitDataEntities.orEmpty())
            limitList.addAll(atmWithdrawalSetting?.limitDataEntities.orEmpty())

            val requestEntity = BusinessCardSettingsRequestEntity(
                cardType = typeFromNetworkCall,
                operationId = operationId,
                tempLock = tempLockSetting?.asDataForRequestEntity,
                overdraft = overdraftSetting?.asDataForRequestEntity,
                onlinePurchase = onlinePurchaseSetting?.asDataForRequestEntity,
                foreignPurchase = foreignPurchaseSetting?.asDataForRequestEntity,
                atmWithdrawal = atmWithdrawalSetting?.asDataForRequestEntity,
                limitList = if (limitList.isEmpty()) null else limitList,
            )

            return CardSettingShipment(requestEntity = requestEntity)
        }
    }
}
