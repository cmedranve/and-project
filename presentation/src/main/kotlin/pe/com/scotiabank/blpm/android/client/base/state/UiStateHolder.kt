package pe.com.scotiabank.blpm.android.client.base.state

interface UiStateHolder {

    var currentState: UiState

    val isDisabledVisible: Boolean
        get() = UiState.DISABLED == currentState
    val isLoadingVisible: Boolean
        get() = UiState.LOADING == currentState
    val isErrorVisible: Boolean
        get() = UiState.ERROR == currentState
    val isEmptyVisible: Boolean
        get() = UiState.EMPTY == currentState
    val isSuccessVisible: Boolean
        get() = UiState.SUCCESS == currentState
}
