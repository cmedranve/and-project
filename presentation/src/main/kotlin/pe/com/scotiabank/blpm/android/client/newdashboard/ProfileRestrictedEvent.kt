package pe.com.scotiabank.blpm.android.client.newdashboard

enum class ProfileRestrictedEvent {

    SHOW_RESTRICTED,
    SHOW_OPEN_MARKET;

    companion object{
        @JvmStatic
        fun filterInShowRestricted(
            event: ProfileRestrictedEvent
        ): Boolean = SHOW_RESTRICTED == event

        @JvmStatic
        fun filterInShowOpenMarket(
            event: ProfileRestrictedEvent
        ): Boolean = SHOW_OPEN_MARKET == event
    }
}