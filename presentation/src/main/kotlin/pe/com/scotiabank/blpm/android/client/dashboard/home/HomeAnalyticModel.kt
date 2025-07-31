package pe.com.scotiabank.blpm.android.client.dashboard.home

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.newdashboard.NewDashboardFactory
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.dashboard.DashboardFactory
import pe.com.scotiabank.blpm.android.client.products.dashboard.HomeEvent

class HomeAnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: DashboardFactory,
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
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    fun sendEvent(eventData: AnalyticEventData<*>) {
        selfReceiver.receive(eventData)
    }

    private fun filterInScreenEvent(
        eventData: AnalyticEventData<*>
    ): Boolean = HomeEvent.SCREEN == eventData.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        analyticsDataGateway.setCurrentScreen(NewDashboardFactory.SCREEN_NAME)

    }

    private fun filterInClickEvent(
        eventData: AnalyticEventData<*>
    ): Boolean = HomeEvent.CLICK == eventData.event

    private fun handleClickEvent(eventData: AnalyticEventData<*>) {
        val label = eventData.data[AnalyticsBaseConstant.EVENT_LABEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.createTopMenuEvent(label)
        analyticsDataGateway.sendEventV2(event)
    }
}
