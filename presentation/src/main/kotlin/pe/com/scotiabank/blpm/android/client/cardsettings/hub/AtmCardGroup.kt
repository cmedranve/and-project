package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardOwnerType

class AtmCardGroup(
    val ownerType: AtmCardOwnerType,
    val cards: List<Card>,
)
