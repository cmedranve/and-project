package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.atmcardhub.cvvonboarding.CvvOnboardingFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: CvvOnboardingFactory,
) {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent.Companion::filterViewEvent),
            InstanceHandler(::handleLoadScreenEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent.Companion::filterInClickEvent),
            InstanceHandler(::handleClickEvent)
        )
        .build()
    private val instanceReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    fun sendEvent(eventData: AnalyticEventData<*>) {
        instanceReceiver.receive(eventData)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleLoadScreenEvent(eventData: AnalyticEventData<*>) {
        val event: AnalyticsEvent = analyticFactory.whenLoadScreenEvent()
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickEvent(eventData: AnalyticEventData<*>) {
        val eventLabel = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenCLickEvent(eventLabel)

        analyticsDataGateway.sendEventV2(event)
    }
}
