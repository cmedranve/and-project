package pe.com.scotiabank.blpm.android.client.newdashboard

object FilterOfDashboardEvent {

    @JvmStatic
    fun filterInSwapping(
        event: DashboardEvent
    ): Boolean = DashboardEvent.SWAPPING == event

    @JvmStatic
    fun filterInBusinessOpening(
        event: DashboardEvent
    ): Boolean = DashboardEvent.BUSINESS_OPENING == event

    @JvmStatic
    fun filterInPersonalOpening(
        event: DashboardEvent
    ): Boolean = DashboardEvent.PERSONAL_OPENING == event
}
