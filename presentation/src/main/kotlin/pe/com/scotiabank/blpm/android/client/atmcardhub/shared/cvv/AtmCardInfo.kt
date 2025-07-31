package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv

import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCard

class AtmCardInfo(
    val cardId: String,
    var authId: String,
    var authTracking: String,
    var operationId: String,
    val cardName: String,
    val atmCard: AtmCard,
    var isMainHolder: Boolean,
    var isCardLocked: Boolean,
    var isPurchasesDisabled: Boolean,
    val isCardAvailable: Boolean,
)
