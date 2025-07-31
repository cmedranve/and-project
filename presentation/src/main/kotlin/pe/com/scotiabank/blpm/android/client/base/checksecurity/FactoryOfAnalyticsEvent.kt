package pe.com.scotiabank.blpm.android.client.base.checksecurity

import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent

fun interface FactoryOfAnalyticsEvent {

    fun createAnalyticsEvent(eventLabel: String): AnalyticsEvent
}
