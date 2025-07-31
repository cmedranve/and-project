package pe.com.scotiabank.blpm.android.client.host

enum class GooglePlayEvent {

    ATTEMPT_TO_REMEDIATE,
    NOT_AVAILABLE;

    companion object {

        @JvmStatic
        fun filterInAttemptToRemediateGooglePlay(
            event: GooglePlayEvent,
        ): Boolean = ATTEMPT_TO_REMEDIATE == event

        @JvmStatic
        fun filterInGooglePlayNotAvailable(event: GooglePlayEvent): Boolean = NOT_AVAILABLE == event
    }
}