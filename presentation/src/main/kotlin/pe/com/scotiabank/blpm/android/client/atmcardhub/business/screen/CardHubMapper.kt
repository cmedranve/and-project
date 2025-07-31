package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCard
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardBrand
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardColor
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus.ACTIVE
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus.LOCKED
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardSubType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.entity.businesscards.BusinessCardSummaryResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.businesscards.BusinessCardsResponseEntity

class CardHubMapper(
    private val idRegistry: IdRegistry,
    private val credentialDataMapper: CredentialDataMapper,
) {

    fun toCards(entity: BusinessCardsResponseEntity): List<AtmCardInfo> = entity
        .cards
        ?.filterNotNull()
        ?.mapNotNull(::toCard)
        .orEmpty()

    private fun toCard(entity: BusinessCardSummaryResponseEntity): AtmCardInfo? {
        val cardId: String = entity.cardId ?: return null
        val cardName: String = entity.cardName.orEmpty()

        val brand: AtmCardBrand = AtmCardBrand.identifyByNewName(entity.cardBrand)
        val status: AtmCardStatus = AtmCardStatus.identifyByName(entity.status)
        val isActiveOrLocked: Boolean = status == ACTIVE || status == LOCKED
        if (isActiveOrLocked.not()) return null

        val type: AtmCardType = AtmCardType.identifyByName(entity.additionalData?.cardType)
        if (type == AtmCardType.CREDIT) return null

        val cardColor: AtmCardColor = AtmCardColor.getCardColorByName(entity.additionalData?.color)

        val atmCard = AtmCard(
            type = type,
            subType = AtmCardSubType.NONE,
            credentialData = credentialDataMapper.toDataToMask(entity.cardNumber.orEmpty()),
            number = entity.cardNumber.orEmpty(),
            brand = brand,
            color = cardColor,
            status = status,
            uiId = idRegistry.atmCardId,
        )
        return AtmCardInfo(
            cardId = cardId,
            authId = String.EMPTY,
            authTracking = String.EMPTY,
            operationId = Constant.EMPTY_STRING,
            cardName = cardName,
            atmCard = atmCard,
            isMainHolder = true,
            isCardLocked = false,
            isPurchasesDisabled = false,
            isCardAvailable = true,
        )
    }
}
