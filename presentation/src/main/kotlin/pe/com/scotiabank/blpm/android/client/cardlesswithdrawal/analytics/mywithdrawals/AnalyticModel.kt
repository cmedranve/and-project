package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics.mywithdrawals

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.cardlesswithdrawal.mywithdrawals.MyWithdrawalsFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.AMOUNT
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.CURRENCY
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.EVENT_LABEL
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.STATUS
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics.AnalyticEvent

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: MyWithdrawalsFactory,
){

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
        val amount: String = eventData.data[AMOUNT] as? String ?: return
        val currency: String = eventData.data[CURRENCY] as? String ?: return
        val status: String = eventData.data[STATUS] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenClickEvent(label, amount, currency, status)
        analyticsDataGateway.sendEventV2(event)
    }
}
