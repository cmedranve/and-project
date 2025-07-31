package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv

import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCard

interface StoreOfAtmCard {

    val atmCardReceived: AtmCard?
}

class MutableStoreOfAtmCard : StoreOfAtmCard {

    override var atmCardReceived: AtmCard? = null
}
