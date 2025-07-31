package pe.com.scotiabank.blpm.android.ui.list.viewmodel

interface EventHandler {

    fun receiveEvent(event: Any): Boolean
}
