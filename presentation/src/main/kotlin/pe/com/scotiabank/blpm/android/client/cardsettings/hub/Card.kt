package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardBrand
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardOwnerType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType

class Card(
    val id: String,
    val brand: AtmCardBrand,
    val name: String,
    val number: String,
    val cardType: AtmCardType,
    val status: AtmCardStatus,
    val ownerType: AtmCardOwnerType,
)
