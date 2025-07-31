package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics.detail

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.cardlesswithdrawal.detail.DetailFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsUtil
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics.AnalyticEvent

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: DetailFactory,
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
            InstancePredicate(AnalyticEvent::filterPopupViewEvent),
            InstanceHandler(::handlePopupScreenEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterPopupClickActionEvent),
            InstanceHandler(::handlePopupClickActionEvent)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    fun sendEvent(eventData: AnalyticEventData<*>) {
        selfReceiver.receive(eventData)
    }

    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        val status: String = eventData.data[AnalyticsConstant.STATUS] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenLoadScreen(status)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickActionEvent(eventData: AnalyticEventData<*>) {
        val label: String = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val status: String = eventData.data[AnalyticsConstant.STATUS] as? String ?: return
        val labelNormalized: String = AnalyticsUtil.normalizeHyphenText(label)

        val event: AnalyticsEvent = analyticFactory.whenClickEvent(labelNormalized, status)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handlePopupScreenEvent(eventData: AnalyticEventData<*>) {
        val status: String = eventData.data[AnalyticsConstant.STATUS] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenPopUpLoadEvent(status)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handlePopupClickActionEvent(eventData: AnalyticEventData<*>) {
        val label: String = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val status: String = eventData.data[AnalyticsConstant.STATUS] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenClickOnPopUpOptions(label, status)
        analyticsDataGateway.sendEventV2(event)
    }
}
