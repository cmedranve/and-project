package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardBrand
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardOwnerType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.data.entity.cardsettings.CardSummaryResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.cardsettings.CardsResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.cardsettings.CardsSettingResponseEntity

class NewCardSettingHubMapper {

    fun toCardSettingHub(entity: CardsSettingResponseEntity?): CardSettingHub {
        val groups: List<AtmCardGroup> = entity?.data?.mapNotNull(::toAtmCardGroup).orEmpty()
        return CardSettingHub(groups)
    }

    private fun toAtmCardGroup(entity: CardsResponseEntity?): AtmCardGroup? {
        val ownerType: AtmCardOwnerType = AtmCardOwnerType.identifyByLabel(entity?.type)
        if (AtmCardOwnerType.NONE == ownerType) return null

        val cardEntities: List<CardSummaryResponseEntity?> = entity?.cards ?: return null
        val cards: List<Card> = cardEntities.mapNotNull(::toCard)
        if (cards.isEmpty()) return null

        return AtmCardGroup(ownerType, cards)
    }

    private fun toCard(entity: CardSummaryResponseEntity?): Card? {
        val cardEntity: CardSummaryResponseEntity = entity ?: return null
        return toCardEntity(cardEntity)
    }

    private fun toCardEntity(
        entity: CardSummaryResponseEntity,
    ): Card = Card(
        id = entity.id,
        brand = AtmCardBrand.identifyByOldName(entity.brand),
        name = getName(entity),
        number = entity.number,
        cardType = AtmCardType.identifyBy(entity.cardType),
        status = AtmCardStatus.identifyByName(entity.statusDesc),
        ownerType = AtmCardOwnerType.identifyBy(entity.ownerType),
    )

    private fun getName(entity: CardSummaryResponseEntity): String {
        if (entity.cardType == AtmCardType.DEBIT.id) return AtmCardType.DEBIT.displayText
        return entity.name
    }
}
