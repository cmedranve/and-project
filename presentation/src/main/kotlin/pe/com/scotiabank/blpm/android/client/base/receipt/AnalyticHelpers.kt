package pe.com.scotiabank.blpm.android.client.base.receipt

enum class ReceiptEvent {
    SCREEN,
    CLICK,
    POPUP,
    CLICK_POPUP,
}

enum class AnalyticLabel(val value: String) {

    BACK("atras"),
    GO_TO_HOME("ir-al-inicio"),
    ADD_TO_MY_LIST("agregar-a-mi-lista"),
    SHARE("compartir-constancia"),
    ADD_AS_FREQUENT("agregar-como-frecuente"),
    YES_ADD("si-agregar"),
    CARD_WAS_ADDED_AS_FREQUENT("la-tarjeta-fue-agregada-como-frecuente"),
    ADD_AS_FREQUENT_CARD("agregar-como-tarjeta-frecuente"),
}

enum class AnalyticPopup(val popupName: String, val label: String) {
    ADD_TRANSFER_TO_MY_LIST(
        popupName = "agregar-a-mi-lista-transferencias",
        label = "popup-agregar-a-mi-lista-transferencias"
    ),
    ADD_TRANSFER_TO_MY_LIST_SUCCESS(
        popupName = "agregar-a-mi-lista-transferencias-exitoso",
        label = "snackbar-agregar-a-mi-lista-transferencias-exitoso"
    ),
}
