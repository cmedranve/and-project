package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics

import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

enum class AnalyticEvent {

    VIEW,
    CLICK_ACTION,
    SEND_CLICK_ACTION,
    CONFIRM_CLICK_ACTION,
    POPUP_VIEW,
    POPUP_CLICK_ACTION;

    companion object {

        @JvmStatic
        fun filterViewEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = VIEW == eventData.event

        @JvmStatic
        fun filterClickActionEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = CLICK_ACTION == eventData.event

        @JvmStatic
        fun filterSendClickActionEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = SEND_CLICK_ACTION == eventData.event

        @JvmStatic
        fun filterConfirmClickActionEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = CONFIRM_CLICK_ACTION == eventData.event

        @JvmStatic
        fun filterPopupViewEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = POPUP_VIEW == eventData.event

        @JvmStatic
        fun filterPopupClickActionEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = POPUP_CLICK_ACTION == eventData.event
    }
}
