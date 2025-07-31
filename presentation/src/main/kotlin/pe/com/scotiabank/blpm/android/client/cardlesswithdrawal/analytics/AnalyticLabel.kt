package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics

enum class AnalyticLabel(val value: String) {

    BACK_TO_HOME("volver-al-inicio"),
    CANCELED("anulado"),
    CHARGED("cobrado"),
    CHOOSE_CURRENCY ("seleccionar-moneda"),
    CHOOSE_ORIGIN_ACCOUNT("seleccionar-cuenta-origen"),
    DESCRIPTION_OF_OPERATION("descripcion-de-la-operacion"),
    FOR_ANOTHER_PERSON("para-otra-persona"),
    FOR_ME("para-mi"),
    HOW_MUCH_DO_YOU_WANT_TO_SEND("cuanto-quieres-enviar"),
    READY_TO_WITHDRAW("listo-para-retirar"),
    SEE_ALL_MY_WITHDRAWALS("ver-todos-mis-retiros"),
    SEE_MY_WITHDRAWALS("ver-mis-retiros"),
    SEE_WITHDRAWAL_DETAIL("ver-detalle-de-retiro"),
    SHARE("compartir"),
    SHARE_KEY("compartir-clave"),
    YOUR_LAST_NAME("su-apellido"),
    YOUR_NAME("su-nombre"),
    YOUR_PHONE_NUMBER("su-numero-de-celular"),
}
