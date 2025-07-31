package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics.confirmation

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.cardlesswithdrawal.confirmation.ConfirmationFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.AUTHENTICATION_CHANNEL
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.DESCRIPTION
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.EVENT_LABEL
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics.AnalyticEvent

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: ConfirmationFactory,
) {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterViewEvent),
            InstanceHandler(::handleScreenEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterClickActionEvent),
            InstanceHandler(::handleClickActionEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterConfirmClickActionEvent),
            InstanceHandler(::handleClickConfirmActionEvent)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    fun sendEvent(eventData: AnalyticEventData<*>) {
        selfReceiver.receive(eventData)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        val event: AnalyticsEvent = analyticFactory.whenLoadScreen()
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickActionEvent(eventData: AnalyticEventData<*>) {
        val label: String = eventData.data[EVENT_LABEL] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenClickEvent(label)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickConfirmActionEvent(eventData: AnalyticEventData<*>) {
        val description: String = eventData.data[DESCRIPTION] as? String ?: return
        val authenticationChannel: String = eventData.data[AUTHENTICATION_CHANNEL] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenClickConfirmEvent(
            description = description,
            authenticationChannel = authenticationChannel
        )
        analyticsDataGateway.sendEventV2(event)
    }
}
