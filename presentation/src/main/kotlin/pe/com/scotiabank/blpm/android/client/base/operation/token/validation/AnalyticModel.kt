package pe.com.scotiabank.blpm.android.client.base.operation.token.validation

import androidx.core.util.Consumer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.*
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.operation.token.validation.ValidationFactory
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.util.consumer.tryAccepting

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: ValidationFactory,
) : Consumer<AnalyticEventData<*>> {

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
            InstancePredicate(::filterInConfirmEvent),
            InstanceHandler(::handleConfirmEvent)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    val validationInformationName: String
        get() = analyticFactory.validationInformationName

    override fun accept(value: AnalyticEventData<*>) {
        tryAccepting(value, selfReceiver, FirebaseCrashlytics.getInstance()::recordException)
    }

    private fun filterInScreenEvent(
        eventData: AnalyticEventData<*>,
    ): Boolean = ValidationEvent.SCREEN == eventData.event

    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        analyticsDataGateway.setCurrentScreen(analyticFactory.screenName)

        val authMode: String = eventData.data[AnalyticsBaseConstant.AUTH_MODE] as? String
            ?: AnalyticsBaseConstant.HYPHEN_STRING

        val event: AnalyticsEvent = analyticFactory.createScreenEvent(
            authMode = authMode.ifBlank { AnalyticsBaseConstant.HYPHEN_STRING },
        )

        analyticsDataGateway.sendEventV2(event)
    }

    private fun filterInClickEvent(
        eventData: AnalyticEventData<*>,
    ): Boolean = ValidationEvent.CLICK == eventData.event

    private fun handleClickEvent(eventData: AnalyticEventData<*>) {
        val authMode: String = eventData.data[AnalyticsBaseConstant.AUTH_MODE] as? String
            ?: AnalyticsBaseConstant.HYPHEN_STRING

        val eventLabel = eventData.data[AnalyticsBaseConstant.EVENT_LABEL] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.createClickEvent(
            authMode = authMode.ifBlank { AnalyticsBaseConstant.HYPHEN_STRING },
            eventLabel = eventLabel.ifBlank { AnalyticsBaseConstant.HYPHEN_STRING },
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun filterInConfirmEvent(
        eventData: AnalyticEventData<*>
    ): Boolean = ValidationEvent.CONFIRM == eventData.event

    private fun handleConfirmEvent(eventData: AnalyticEventData<*>) {
        val authMode: String = eventData.data[AnalyticsBaseConstant.AUTH_MODE] as? String
            ?: AnalyticsBaseConstant.HYPHEN_STRING
        val description: String = eventData.data[AnalyticsBaseConstant.DESCRIPTION] as? String
            ?: AnalyticsBaseConstant.HYPHEN_STRING

        val event: AnalyticsEvent = analyticFactory.createConfirmEvent(
            authMode = authMode.ifBlank { AnalyticsBaseConstant.HYPHEN_STRING },
            description = if (description.isBlank()) AnalyticsBaseConstant.FALSE else AnalyticsBaseConstant.TRUE,
        )

        analyticsDataGateway.sendEventV2(event)
    }
}
