package pe.com.scotiabank.blpm.android.client.base.operation.confirmation

import pe.com.scotiabank.blpm.android.analytics.factories.operation.token.validation.ValidationInformation

enum class ConfirmationOperationType(
    val label: String,
    val analyticInfo: ValidationInformation,
    val modalData: ModalData,
) {

    TRANSFER_OWN(
        label = "Transferir",
        analyticInfo = ValidationInformation.TRANSFER_BETWEEN_OWN_ACCOUNTS,
        modalData = ModalData.CANCEL_TRANSFER,
    ),

    TRANSFER_OTHER_ACCOUNT(
        label = "Transferir",
        analyticInfo = ValidationInformation.TRANSFER_OTHER_ACCOUNT,
        modalData = ModalData.CANCEL_TRANSFER,
    ),

    TRANSFER_OTHER_BANK(
        label = "Transferir",
        analyticInfo = ValidationInformation.TRANSFER_OTHER_BANK,
        modalData = ModalData.CANCEL_TRANSFER,
    ),

    RECHARGE(
        label = "Recargar",
        analyticInfo = ValidationInformation.RECHARGE,
        modalData = ModalData.CANCEL_DEFAULT,
    ),

    PAYMENT(
        label = "Pagar",
        analyticInfo = ValidationInformation.PAYMENT,
        modalData = ModalData.CANCEL_PAYMENT,
    ),

    APPRAISAL_PAYMENT(
        label = "Pagar",
        analyticInfo = ValidationInformation.APPRAISAL_PAYMENT,
        modalData = ModalData.CANCEL_PAYMENT,
    );
}
