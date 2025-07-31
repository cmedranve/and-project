package pe.com.scotiabank.blpm.android.client.base.state

class DelegateUiStateHolder(
    override var currentState: UiState = UiState.BLANK,
): UiStateHolder
