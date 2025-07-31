package pe.com.scotiabank.blpm.android.client.cardsettings.analytics.detail

import androidx.core.util.Consumer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.cardsettings.ChangesSavedAnalyticsParams
import pe.com.scotiabank.blpm.android.analytics.factories.cardsettings.detail.DetailFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.cardsettings.OtpFlowType
import pe.com.scotiabank.blpm.android.client.util.consumer.tryAccepting

class DetailAnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: DetailFactory,
): Consumer<AnalyticEventData<*>> {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterScreenViewEvent),
            InstanceHandler(::handleScreenEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterClickEvent),
            InstanceHandler(::handleClickActionEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterInHstLoadedEvent),
            InstanceHandler(::handleDigitalWalletLoadEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterContinueEvent),
            InstanceHandler(::handleSaveActionEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterPushOtpEvent),
            InstanceHandler(::handlePushOtpEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterPushOtpClickEvent),
            InstanceHandler(::handlePushOtpClickEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterPushOtpErrorEvent),
            InstanceHandler(::handlePushOtpErrorEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterPushOtpErrorClickEvent),
            InstanceHandler(::handlePushOtpErrorClickEvent)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)
    var otpFlowType = OtpFlowType.NONE

    override fun accept(value: AnalyticEventData<*>) {
        tryAccepting(value, selfReceiver, FirebaseCrashlytics.getInstance()::recordException)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleScreenEvent(eventData: AnalyticEventData<*>) {
        val event: AnalyticsEvent = analyticFactory.whenLoadScreen()
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickActionEvent(eventData: AnalyticEventData<*>) {
        val action: String = eventData.data[AnalyticsConstant.EVENT_ACTION] as? String ?: return
        val label: String = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val questionEight: String = eventData.data[AnalyticsConstant.QUESTION_EIGHT] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenClickActionEvent(
            action = action,
            label = label,
            questionEight = questionEight,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleDigitalWalletLoadEvent(eventData: AnalyticEventData<*>) {
        val questionEight: String = eventData.data[AnalyticsConstant.QUESTION_EIGHT] as? String ?: return

        val event: AnalyticsEvent = analyticFactory.whenDigitalWalletLoadEvent(questionEight)
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleSaveActionEvent(eventData: AnalyticEventData<*>) {
        val questionEight: String = eventData.data[AnalyticsConstant.QUESTION_EIGHT] as? String ?: return
        val questionOne: String = eventData.data[AnalyticsConstant.QUESTION_ONE] as? String ?: return
        val questionThree: String = eventData.data[AnalyticsConstant.QUESTION_THREE] as? String ?: return
        val questionFour: String = eventData.data[AnalyticsConstant.QUESTION_FOUR] as? String ?: return
        val questionFive: String = eventData.data[AnalyticsConstant.QUESTION_FIVE] as? String ?: return
        val questionSix: String = eventData.data[AnalyticsConstant.QUESTION_SIX] as? String ?: return
        val questionSeven: String = eventData.data[AnalyticsConstant.QUESTION_SEVEN] as? String ?: return

        val analyticsParams = ChangesSavedAnalyticsParams(
            questionOne = questionOne,
            questionThree = questionThree,
            questionFour = questionFour,
            questionFive = questionFive,
            questionSix = questionSix,
            questionSeven = questionSeven
        )

        val event: AnalyticsEvent = analyticFactory.whenClickSaveEvent(
            questionEight = questionEight,
            analyticsParams = analyticsParams
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handlePushOtpEvent(eventData: AnalyticEventData<*>) {
        val authenticationChannel = eventData.data[AnalyticsConstant.AUTHENTICATION_CHANNEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenShowPopUpPushOtp(
            productName = otpFlowType.analyticsValue,
            authenticationChannel = authenticationChannel,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handlePushOtpClickEvent(eventData: AnalyticEventData<*>) {
        val authenticationChannel = eventData.data[AnalyticsConstant.AUTHENTICATION_CHANNEL] as? String ?: return
        val eventLabel = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenClickPopUpPushOtp(
            productName = otpFlowType.analyticsValue,
            eventLabel = eventLabel,
            authenticationChannel = authenticationChannel,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handlePushOtpErrorEvent(eventData: AnalyticEventData<*>) {
        val screenType = eventData.data[AnalyticsConstant.SCREEN_TYPE] as? String ?: return
        val errorCode = eventData.data[AnalyticsConstant.ERROR_CODE] as? String ?: return
        val errorMessage = eventData.data[AnalyticsConstant.ERROR_MESSAGE] as? String ?: return
        val authenticationChannel = eventData.data[AnalyticsConstant.AUTHENTICATION_CHANNEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenErrorPushOtp(
            productName = otpFlowType.analyticsValue,
            screenType = screenType,
            errorCode = errorCode,
            errorMessage = errorMessage,
            authenticationChannel = authenticationChannel,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handlePushOtpErrorClickEvent(eventData: AnalyticEventData<*>) {
        val screenType = eventData.data[AnalyticsConstant.SCREEN_TYPE] as? String ?: return
        val errorCode = eventData.data[AnalyticsConstant.ERROR_CODE] as? String ?: return
        val errorMessage = eventData.data[AnalyticsConstant.ERROR_MESSAGE] as? String ?: return
        val authenticationChannel = eventData.data[AnalyticsConstant.AUTHENTICATION_CHANNEL] as? String ?: return
        val eventLabel = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenClickErrorPushOtp(
            productName = otpFlowType.analyticsValue,
            screenType = screenType,
            errorCode = errorCode,
            errorMessage = errorMessage,
            eventLabel = eventLabel,
            authenticationChannel = authenticationChannel,
        )
        analyticsDataGateway.sendEventV2(event)
    }
}
