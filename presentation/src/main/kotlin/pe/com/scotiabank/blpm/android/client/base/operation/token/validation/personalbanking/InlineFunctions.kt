package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking

import pe.com.scotiabank.blpm.android.client.util.Constant

inline fun createDialogDataFromServiceError(
    helper: HelperForAuthErrorDialog,
    errorCode: String,
    errorMessage: String,
    clearCallback: () -> Unit,
): DataOfErrorDialog = when (errorCode) {
    // Create DialogData for a common error message,
    Constant.AUTH_ERR_025,
    Constant.AUTH_ERR_012 -> DataOfAuthErrorDialog(
        title = Constant.EMPTY_STRING,
        message = errorMessage,
        textForPositiveButton = helper.textForPositiveButton,
    )
    Constant.AUTH_ERR_003,
    Constant.AUTH_ERR_004,
    Constant.AUTH_ERR_005,
    Constant.AUTH_ERR_006,
    Constant.AUTH_ERR_007,
    Constant.AUTH_ERR_020,
    Constant.AUTH_ERR_021 -> DataOfAuthErrorDialog(
        title = Constant.EMPTY_STRING,
        message = Constant.OTP_EXPIRED_MESSAGE,
        textForPositiveButton = helper.textForPositiveButton,
    )
    // Create DialogData in order to close the operation-validation screen.
    Constant.AUTH_ERR_022,
    Constant.AUTH_ERR_023,
    Constant.AUTH_ERR_026,
    Constant.AUTH_ERR_013,
    Constant.AUTH_ERR_014,
    Constant.AUTH_ERR_008,
    Constant.AUTH_ERR_009,
    Constant.AUTH_ERR_010,
    Constant.AUTH_ERR_011,
    Constant.AUTH_ERR_015,
    Constant.AUTH_ERR_016,
    Constant.ERROR_THIRD_TRY -> DataOfAuthErrorDialog(
        title = Constant.EMPTY_STRING,
        message = errorMessage,
        textForPositiveButton = helper.textForPositiveButton,
        errorCode = Constant.FINISH_CODE,
    )
    // Clear sensitive pok information such as authId, deviceId and keys,
    // then create DialogData in order to close the operation-validation screen
    // after showing the security error message.
    Constant.AUTH_ERR_001,
    Constant.AUTH_ERR_002,
    Constant.AUTH_ERR_028,
    Constant.AUTH_ERR_029 -> {
        clearCallback.invoke()
        DataOfAuthErrorDialog(
            title = helper.title,
            message = errorMessage,
            textForPositiveButton = helper.textForPositiveButton,
            errorCode = Constant.FINISH_CODE,
        )
    }
    else -> EmptyDataOfErrorDialog
}
