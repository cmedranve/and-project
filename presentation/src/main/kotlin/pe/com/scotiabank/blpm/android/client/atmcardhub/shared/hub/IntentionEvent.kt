package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub

enum class IntentionEvent {
    CARD_CREATED,
    CREDIT_CARD_CREATED,
    ON_GOOGLE_WALLET_UNAVAILABLE,
    GO_TO_CVV_INTRO,
    GO_TO_RESTRICTED_PROFILE_ALERT,
    GO_TO_CARD_SETTING_HUB,
    SHOW_CARD_DATA_AGAIN,
    CANCEL_SCOPE,
    REFRESH_CARD_HUB,
}
