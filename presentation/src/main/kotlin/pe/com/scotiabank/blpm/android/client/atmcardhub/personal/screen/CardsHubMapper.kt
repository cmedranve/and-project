package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCard
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardBrand
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardColor
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardSubType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.debitcard.DebitCard
import pe.com.scotiabank.blpm.android.client.debitcard.PendingCard
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.debitcard.CardSummaryResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.debitcard.CardsResponseEntity

class CardsHubMapper(
    private val idRegistry: IdRegistry,
    private val credentialDataMapper: CredentialDataMapper,
) {

    fun toDebitCardHub(entity: CardsResponseEntity): DebitCardHub {

        val cards: List<DebitCard> = entity
            .cards
            ?.filterNotNull()
            ?.mapNotNull(::attemptToDebitCard)
            .orEmpty()

        val pendingCard: PendingCard? = attemptToPendingCard(entity)

        return DebitCardHub(cards = cards, pendingCard = pendingCard)
    }

    private fun attemptToDebitCard(entity: CardSummaryResponseEntity): DebitCard? {
        val cardId: String = entity.cardId ?: return null
        val cardName: String = entity.cardName.orEmpty()
        return DebitCard(
            cardId = cardId,
            name = cardName,
            number = entity.cardNumber.orEmpty(),
            type = AtmCardSubType.identifyBy(entity.additionalData?.cardType),
            cardColor = AtmCardColor.getCardColorByName(entity.additionalData?.color)
        )
    }

    private fun attemptToPendingCard(
        cardsResponseEntity: CardsResponseEntity
    ): PendingCard? {
        val pendingCard = cardsResponseEntity.pendingCard ?: return null
        val operationId = pendingCard.operationId ?: return null
        val accountName = pendingCard.accountName ?: return null

        return PendingCard(
            isCardCreationPending = pendingCard.digitalCardPendingCreation ?: false,
            operationId = operationId,
            accountName = accountName,
            cardName = Constant.DIGITAL_DEBIT_MASTERCARD,
        )
    }

    fun toAtmCardInfo(
        debitCard: DebitCard,
        authId: String,
        authTracking: String,
        operationId: String,
        cardSettingFlags: CardSettingFlags,
    ): AtmCardInfo {
        val atmCard = AtmCard(
            type = AtmCardType.DEBIT,
            subType = debitCard.type,
            credentialData = credentialDataMapper.toDataToMask(debitCard.number),
            number = debitCard.number,
            brand = getCardBrand(debitCard.name),
            color = debitCard.cardColor,
            uiId = idRegistry.atmCardId,
        )
        return AtmCardInfo(
            cardId = debitCard.cardId,
            authId = authId,
            authTracking = authTracking,
            operationId = operationId,
            cardName = debitCard.name,
            atmCard = atmCard,
            isMainHolder = cardSettingFlags.isMainHolder,
            isCardLocked = cardSettingFlags.isCardLocked,
            isPurchasesDisabled = cardSettingFlags.isPurchasesDisabled,
            isCardAvailable = true,
        )
    }

    fun toAtmCardInfo(
        product: NewProductModel,
        authId: String,
        authTracking: String,
        operationId: String,
        cardSettingFlags: CardSettingFlags,
    ): AtmCardInfo {
        val atmCard = AtmCard(
            type = AtmCardType.CREDIT,
            subType = AtmCardSubType.PHYSICAL_CREDIT,
            credentialData = credentialDataMapper.toDataToMask(product.customerProductNumber),
            number = product.customerProductNumber,
            brand = getCardBrand(product.name),
            color = product.cardColor,
            uiId = idRegistry.atmCardId,
        )
        return AtmCardInfo(
            cardId = product.cardId,
            authId = authId,
            authTracking = authTracking,
            operationId = operationId,
            cardName = product.name,
            atmCard = atmCard,
            isMainHolder = cardSettingFlags.isMainHolder,
            isCardLocked = cardSettingFlags.isCardLocked,
            isPurchasesDisabled = cardSettingFlags.isPurchasesDisabled,
            isCardAvailable = product.isAvailable,
        )
    }

    private fun getCardBrand(cardName: String): AtmCardBrand = when {
        cardName.contains(Constant.VISA, true) -> AtmCardBrand.VISA
        cardName.contains(Constant.MASTERCARD, true) -> AtmCardBrand.MASTERCARD
        else -> AtmCardBrand.AMERICAN_EXPRESS
    }
}
