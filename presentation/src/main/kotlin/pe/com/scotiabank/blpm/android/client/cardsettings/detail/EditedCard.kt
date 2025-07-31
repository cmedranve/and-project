package pe.com.scotiabank.blpm.android.client.cardsettings.detail

import pe.com.scotiabank.blpm.android.client.model.CardDetailModel

class EditedCard(
    val cardDetail: CardDetailModel,
    val walletState: String,
    val isCashDispositionEnabled: String,
    val isTemporarilyLockChecked: String,
    val isOnlineShoppingChecked: String,
    val isPurchasesAbroadChecked: String,
    val isAtmWithdrawalsEnabled: String,
    val isOverdraftChecked: String,
)
