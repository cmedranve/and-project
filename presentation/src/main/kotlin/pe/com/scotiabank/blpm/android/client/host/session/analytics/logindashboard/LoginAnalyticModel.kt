package pe.com.scotiabank.blpm.android.client.host.session.analytics.logindashboard

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.newlogin.LoginDashboardFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.session.entities.Client
import pe.com.scotiabank.blpm.android.client.util.Constant.HYPHEN_STRING
import pe.com.scotiabank.blpm.android.client.util.Constant.SPACE_WHITE
import pe.com.scotiabank.blpm.android.client.util.analytics.AnalyticsUtil

class LoginAnalyticModel(
    private val appModel: AppModel,
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: LoginDashboardFactory,
) {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterScreenViewEvent),
            InstanceHandler(::handleLoginDashboardViewEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterInClearDefaultParameterGroup),
            InstanceHandler(::handleClearDefaultParameterGroup)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    fun sendEvent(eventData: AnalyticEventData<*>) {
        selfReceiver.receive(eventData)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleLoginDashboardViewEvent(eventData: AnalyticEventData<*>) {
        val personType: String = eventData.data[AnalyticsConstant.PERSON_TYPE] as? String ?: return
        val platformType: String = eventData.data[AnalyticsConstant.PLATFORM_TYPE] as? String ?: return

        val client: Client = appModel.profile.client ?: return
        val customerType: String = AnalyticsUtil.evaluateProfileType(appModel.profile.profileType)
            .replace(SPACE_WHITE, HYPHEN_STRING)

        val event: AnalyticsEvent = analyticFactory.whenCreateLoginDashboardView(
            customerSegment = client.segmentType,
            customerType = customerType,
            customerId = appModel.profile.hash,
            personType = personType,
            platformType = platformType,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClearDefaultParameterGroup(analyticEventData: AnalyticEventData<*>) {
        analyticsDataGateway.clearDefaultEventParameters()
    }
}