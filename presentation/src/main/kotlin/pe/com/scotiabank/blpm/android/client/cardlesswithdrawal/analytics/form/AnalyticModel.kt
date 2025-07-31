package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics.form

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.cardlesswithdrawal.form.FormAnalyticsParams
import pe.com.scotiabank.blpm.android.analytics.factories.cardlesswithdrawal.form.FormFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.AMOUNT
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.CURRENCY
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.EVENT_LABEL
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.ORIGIN_CURRENCY
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.TYPE_OF_ORIGIN_ACCOUNT
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsUtil
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.util.Constant

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: FormFactory,
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
            InstancePredicate(AnalyticEvent::filterSendClickActionEvent),
            InstanceHandler(::handleSendClickActionEvent)
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

    @Suppress("UNUSED_PARAMETER")
    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        val event: AnalyticsEvent = analyticFactory.whenLoadScreen()
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickActionEvent(eventData: AnalyticEventData<*>) {
        val label: String = eventData.data[EVENT_LABEL] as? String ?: return
        val currency: String = eventData.data[CURRENCY] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenClickEvent(label, currency)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleSendClickActionEvent(eventData: AnalyticEventData<*>) {
        val amount: String = eventData.data[AMOUNT] as? String ?: return
        val currency: String = eventData.data[CURRENCY] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenClickSendEvent(amount, currency)
        analyticsDataGateway.sendEventV2(event)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handlePopupScreenEvent(eventData: AnalyticEventData<*>) {
        val event: AnalyticsEvent = analyticFactory.whenPopUpLoadEvent()
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handlePopupClickActionEvent(eventData: AnalyticEventData<*>) {
        val label: String = eventData.data[EVENT_LABEL] as? String ?: return
        val originAccountType: String = eventData.data[TYPE_OF_ORIGIN_ACCOUNT] as? String ?: return
        val originCurrency: String = eventData.data[ORIGIN_CURRENCY] as? String ?: return
        val currency: String = eventData.data[CURRENCY] as? String ?: return
        val amount: String = eventData.data[AMOUNT] as? String ?: return
        val originAccountNormalized: String = AnalyticsUtil.normalizeHyphenText(originAccountType)
            .replace(Constant.DOT, Constant.EMPTY_STRING)

        val analyticsParams = FormAnalyticsParams(
            originAccountType = originAccountNormalized,
            originCurrency = originCurrency,
            currency = currency,
            amount = amount,
        )

        val event: AnalyticsEvent = analyticFactory.whenClickOnPopUpOptions(label, analyticsParams)
        analyticsDataGateway.sendEventV2(event)
    }
}
