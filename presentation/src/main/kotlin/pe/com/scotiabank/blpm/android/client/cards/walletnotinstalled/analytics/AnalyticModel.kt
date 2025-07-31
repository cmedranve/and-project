package pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled.analytics

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.cards.walletnotinstalled.WalletNotInstalledFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: WalletNotInstalledFactory,
) {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterScreenEvent),
            InstanceHandler(::handleScreenEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterClickEvent),
            InstanceHandler(::handleClickEvent)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    fun sendEvent(eventData: AnalyticEventData<*>) {
        selfReceiver.receive(eventData)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        val event: AnalyticsEvent = analyticFactory.whenLoadingScreen()
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickEvent(eventData: AnalyticEventData<*>) {
        val label = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenTapOnButtons(label)
        analyticsDataGateway.sendEventV2(event)
    }
}
