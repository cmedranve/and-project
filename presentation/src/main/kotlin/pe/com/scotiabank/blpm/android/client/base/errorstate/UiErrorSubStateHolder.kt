package pe.com.scotiabank.blpm.android.client.base.errorstate

interface UiErrorSubStateHolder {

    var currentErrorSubState: UiErrorSubState

    val isErrorIdleVisible: Boolean
    val isErrorLoadingVisible: Boolean
}
