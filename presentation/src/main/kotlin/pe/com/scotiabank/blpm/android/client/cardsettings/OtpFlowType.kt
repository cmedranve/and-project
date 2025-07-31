package pe.com.scotiabank.blpm.android.client.cardsettings

enum class OtpFlowType(val analyticsValue: String) {
    CARD_SETTING("configurar-tarjeta"),
    GOOGLE_WALLET("activar-google-pay"),
    NONE(""),
}
