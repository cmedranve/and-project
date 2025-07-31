package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder

interface UiStateHolderWithErrorType : UiStateHolder {

    var errorType: ErrorType?

    val isEmptyOrErrorVisible: Boolean
        get() = isEmptyVisible || isErrorVisible

    val isRetryableErrorVisible: Boolean
        get() = isEmptyVisible || (isErrorVisible && ErrorType.RETRYABLE == errorType)

    val isBlockingErrorVisible: Boolean
        get() = isErrorVisible && ErrorType.BLOCKING == errorType

    val isAppBarVisible: Boolean
        get() = isBlockingErrorVisible.not()
}
