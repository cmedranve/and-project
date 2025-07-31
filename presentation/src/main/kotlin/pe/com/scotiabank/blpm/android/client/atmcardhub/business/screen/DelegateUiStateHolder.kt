package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import pe.com.scotiabank.blpm.android.client.base.state.UiState

class DelegateUiStateHolder(
    override var currentState: UiState = UiState.BLANK,
    override var errorType: ErrorType? = null,
): UiStateHolderWithErrorType
