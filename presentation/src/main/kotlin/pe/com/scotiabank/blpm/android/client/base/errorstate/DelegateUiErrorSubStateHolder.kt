package pe.com.scotiabank.blpm.android.client.base.errorstate

import androidx.core.util.Supplier

class DelegateUiErrorSubStateHolder(
    private val visibilitySupplier: Supplier<Boolean>,
    override var currentErrorSubState: UiErrorSubState = UiErrorSubState.IDLE,
): UiErrorSubStateHolder {

    override val isErrorIdleVisible: Boolean
        get() = visibilitySupplier.get() && UiErrorSubState.IDLE == currentErrorSubState
    override val isErrorLoadingVisible: Boolean
        get() = visibilitySupplier.get() &&  UiErrorSubState.LOADING == currentErrorSubState
}
