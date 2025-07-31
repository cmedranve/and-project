package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro

import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

enum class AnalyticEvent {
    VIEW,
    CLICK;

    companion object {

        @JvmStatic
        fun filterViewEvent(
            eventData: AnalyticEventData<*>
        ): Boolean = VIEW == eventData.event

        @JvmStatic
        fun filterInClickEvent(
            eventData: AnalyticEventData<*>
        ): Boolean = CLICK == eventData.event
    }
}
