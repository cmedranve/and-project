package pe.com.scotiabank.blpm.android.client.base.operation.token.validation

enum class ValidationEvent {
    SCREEN,
    CLICK,
    CONFIRM,
}

enum class AnalyticLabel(val value: String) {

    BACK("atras"),
    SEND_AGAIN("volver-a-enviar"),
    SEND_BY_SMS("enviar-por-sms"),
    SEND_BY_EMAIL("enviar-por-correo"),
    DESCRIPTION("descripcion"),
}
