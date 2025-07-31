package pe.com.scotiabank.blpm.android.client.host.session.analytics

import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

enum class AnalyticEvent {

    VIEW;

    companion object {

        @JvmStatic
        fun filterViewEvent(
            eventData: AnalyticEventData<*>,
        ): Boolean = VIEW == eventData.event
    }
}