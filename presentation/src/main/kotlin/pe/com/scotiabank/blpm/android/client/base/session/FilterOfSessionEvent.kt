package pe.com.scotiabank.blpm.android.client.base.session

object FilterOfSessionEvent {

    @JvmStatic
    fun filterInEnding(event: SessionEvent): Boolean = SessionEvent.ENDING == event

    @JvmStatic
    fun filterInLoggedOut(event: SessionEvent): Boolean = SessionEvent.LOGGED_OUT == event
}
