package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.CardSettings
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.Limit
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.BusinessCardSettingsResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.LimitEntity

class CardSettingsMapper {

    fun toSettings(entity: BusinessCardSettingsResponseEntity): CardSettings {

        val operationId: String = entity.operationId ?: throw IllegalArgumentException(Constant.CANNOT_TRANSFORM)
        val isTempLock: Boolean = entity.tempLock ?: throw IllegalArgumentException(Constant.CANNOT_TRANSFORM)
        val isOverdraft: Boolean = entity.overdraft ?: throw IllegalArgumentException(Constant.CANNOT_TRANSFORM)
        val isForeignPurchase: Boolean = entity.foreignPurchase ?: throw IllegalArgumentException(Constant.CANNOT_TRANSFORM)
        val isOnlinePurchase: Boolean = entity.onlinePurchase ?: throw IllegalArgumentException(Constant.CANNOT_TRANSFORM)
        val isAtmWithdrawal: Boolean = entity.atmWithdrawal ?: throw IllegalArgumentException(Constant.CANNOT_TRANSFORM)
        val limits: List<Limit> = entity.limitList?.mapNotNull(::toLimit).orEmpty()

       return CardSettings(
           operationId = operationId,
           isTempLock = isTempLock,
           isOverdraft = isOverdraft,
           isForeignPurchase = isForeignPurchase,
           isOnlinePurchase = isOnlinePurchase,
           isAtmWithdrawal = isAtmWithdrawal,
           limitList = limits,
        )
    }

    private fun toLimit (entity: LimitEntity?): Limit? {

        val position: Int = entity?.position ?: return null

        return Limit(
            position = position,
            amountMax = entity.amountMax.orEmpty(),
            amountConfig = entity.amountConfig.orEmpty()
        )
    }
}
