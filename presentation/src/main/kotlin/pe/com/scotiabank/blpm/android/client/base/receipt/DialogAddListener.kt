package pe.com.scotiabank.blpm.android.client.base.receipt

interface DialogAddListener {

    fun onTextEntered(operationName: String?)
    fun onDismissDialog()
}
