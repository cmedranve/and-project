package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardBrand
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardOwnerType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.data.entity.cards.CardEntity
import pe.com.scotiabank.blpm.android.data.entity.cards.CardWrapper
import pe.com.scotiabank.blpm.android.data.entity.cards.CardsEntity

class CardSettingHubMapper {

    fun toCardSettingHub(entity: CardsEntity?): CardSettingHub {
        val groups: List<AtmCardGroup> = entity?.cardWrappers?.mapNotNull(::toAtmCardGroup).orEmpty()
        return CardSettingHub(groups)
    }

    private fun toAtmCardGroup(entity: CardWrapper?): AtmCardGroup? {
        val ownerType: AtmCardOwnerType = AtmCardOwnerType.identifyByLabel(entity?.type)
        if (AtmCardOwnerType.NONE == ownerType) return null

        val cardEntities: List<CardEntity?> = entity?.cards ?: return null
        val cards: List<Card> = cardEntities.mapNotNull(::toCard)
        if (cards.isEmpty()) return null

        return AtmCardGroup(ownerType, cards)
    }

    private fun toCard(entity: CardEntity?): Card? {
        val cardEntity: CardEntity = entity ?: return null
        return toCardEntity(cardEntity)
    }

    private fun toCardEntity(
        entity: CardEntity,
    ): Card = Card(
        id = entity.id,
        brand = AtmCardBrand.identifyByOldName(entity.brand),
        name = getName(entity),
        number = entity.number,
        cardType = AtmCardType.identifyBy(entity.cardType),
        status = AtmCardStatus.identifyByName(entity.statusDesc),
        ownerType = AtmCardOwnerType.identifyBy(entity.ownerType),
    )

    private fun getName(entity: CardEntity): String {
        if (entity.cardType == AtmCardType.DEBIT.id) return AtmCardType.DEBIT.displayText
        return entity.name
    }
}
