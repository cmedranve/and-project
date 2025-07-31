package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv

import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

enum class AnalyticEvent {
    POPUP,
    CLICK,
    INFORMATIVE_POPUP,
    CLICK_INFORMATIVE_POPUP,
    SNACK_BAR;

    companion object {

        @JvmStatic
        fun filterPopupEvent(
            eventData: AnalyticEventData<*>
        ): Boolean = POPUP == eventData.event

        @JvmStatic
        fun filterInClickEvent(
            eventData: AnalyticEventData<*>
        ): Boolean = CLICK == eventData.event

        @JvmStatic
        fun filterInformativePopupEvent(
            eventData: AnalyticEventData<*>
        ): Boolean = INFORMATIVE_POPUP == eventData.event

        @JvmStatic
        fun filterInClickInformativePopupEvent(
            eventData: AnalyticEventData<*>
        ): Boolean = CLICK_INFORMATIVE_POPUP == eventData.event

        @JvmStatic
        fun filterSnackBarEvent(
            eventData: AnalyticEventData<*>
        ): Boolean = SNACK_BAR == eventData.event
    }
}
