package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

class CardSettings(
    val operationId: String,
    val isTempLock: Boolean,
    val isOverdraft: Boolean,
    val isForeignPurchase: Boolean,
    val isOnlinePurchase: Boolean,
    val isAtmWithdrawal: Boolean,
    val limitList: List<Limit>,
)
