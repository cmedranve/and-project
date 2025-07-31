package pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled.analytics

import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

enum class AnalyticEvent {
    SCREEN,
    CLICK;

    companion object {

        fun filterScreenEvent(eventData: AnalyticEventData<*>) =
            SCREEN == eventData.event

        fun filterClickEvent(eventData: AnalyticEventData<*>) =
            CLICK == eventData.event
    }
}
