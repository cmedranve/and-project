package pe.com.scotiabank.blpm.android.client.base.receipt

import com.scotiabank.enhancements.handling.*
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.receipt.ReceiptFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: ReceiptFactory,
) {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(::filterInScreenEvent),
            InstanceHandler(::handleScreenEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(::filterInClickEvent),
            InstanceHandler(::handleClickEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(::filterInPopupEvent),
            InstanceHandler(::handlePopupEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(::filterInClickPopupEvent),
            InstanceHandler(::handleClickPopupEvent)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    fun sendEvent(eventData: AnalyticEventData<*>) {
        selfReceiver.receive(eventData)
    }

    private fun filterInScreenEvent(
        eventData: AnalyticEventData<*>,
    ): Boolean = ReceiptEvent.SCREEN == eventData.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        analyticsDataGateway.setCurrentScreen(analyticFactory.screenName)
        val event: AnalyticsEvent = analyticFactory.createScreenEvent()
        analyticsDataGateway.sendEventV2(event)
    }

    private fun filterInClickEvent(
        eventData: AnalyticEventData<*>,
    ): Boolean = ReceiptEvent.CLICK == eventData.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickEvent(eventData: AnalyticEventData<*>) {
        val eventLabel = eventData.data[AnalyticsBaseConstant.EVENT_LABEL] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.createClickEvent(eventLabel)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun filterInPopupEvent(
        eventData: AnalyticEventData<*>,
    ): Boolean = ReceiptEvent.POPUP == eventData.event

    private fun handlePopupEvent(eventData: AnalyticEventData<*>) {
        val popupName: String = eventData.data[AnalyticsConstant.POPUP_NAME] as? String ?: return
        val eventLabel: String = eventData.data[AnalyticsBaseConstant.EVENT_LABEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.createPopupEvent(popupName, eventLabel)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun filterInClickPopupEvent(
        eventData: AnalyticEventData<*>,
    ): Boolean = ReceiptEvent.CLICK_POPUP == eventData.event

    private fun handleClickPopupEvent(eventData: AnalyticEventData<*>) {
        val popupName: String = eventData.data[AnalyticsConstant.POPUP_NAME] as? String ?: return
        val eventLabel: String = eventData.data[AnalyticsBaseConstant.EVENT_LABEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.createClickPopupEvent(popupName, eventLabel)
        analyticsDataGateway.sendEventV2(event)
    }
}
