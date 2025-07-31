package pe.com.scotiabank.blpm.android.ui.slider

fun interface CallbackOfValueChange {

    fun onValueChange(value: Float, isFromUser: Boolean)
}
