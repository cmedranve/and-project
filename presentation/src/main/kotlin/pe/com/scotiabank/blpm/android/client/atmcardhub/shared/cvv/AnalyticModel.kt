package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.atmcardhub.atmcard.AtmCardFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData

class AnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: AtmCardFactory,
) {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent.Companion::filterPopupEvent),
            InstanceHandler(::handleLoadScreenEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent.Companion::filterInClickEvent),
            InstanceHandler(::handleClickEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent.Companion::filterInformativePopupEvent),
            InstanceHandler(::handleShowInformativePopupEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent.Companion::filterInClickInformativePopupEvent),
            InstanceHandler(::handleClickInformativePopupEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent.Companion::filterSnackBarEvent),
            InstanceHandler(::handleShowSnackBarEvent)
        )
        .build()
    private val instanceReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    var isFullError: Boolean = false
    var isCvvError: Boolean = false
    val isError: Boolean
        get() = isFullError || isCvvError

    fun sendEvent(event: AnalyticEventData<*>) {
        instanceReceiver.receive(event)
    }

    private fun handleLoadScreenEvent(eventData: AnalyticEventData<*>) {
        if (isError.not()) {
            handleLoadScreenSuccessEvent(eventData)
            return
        }
        handleLoadScreenErrorEvent()
    }

    private fun handleLoadScreenSuccessEvent(eventData: AnalyticEventData<*>) {
        val subprocessType = eventData.data[AnalyticsConstant.TYPE_SUBPROCESS] as? String ?: return
        val questionOne = eventData.data[AnalyticsConstant.QUESTION_ONE] as? String ?: return
        val questionTwo = eventData.data[AnalyticsConstant.QUESTION_TWO] as? String ?: return
        val questionThree = eventData.data[AnalyticsConstant.QUESTION_THREE] as? String ?: return
        val screenCategory = pickScreenCategory(questionTwo, questionThree)
        val event: AnalyticsEvent = analyticFactory.whenLoadScreenEvent(
            screenCategory = screenCategory,
            subprocessType = subprocessType,
            questionOne = questionOne,
            questionTwo = questionTwo,
            questionThree = questionThree,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleLoadScreenErrorEvent() {
        val popupName = pickPopupName()
        val errorMessage = pickErrorMessage()
        val event: AnalyticsEvent = analyticFactory.whenLoadScreenErrorEvent(
            popupName = popupName,
            errorMessage = errorMessage
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun pickPopupName(): String {
        if (isFullError) return FULL_ERROR_POPUP_NAME
        if (isCvvError) return CVV_ERROR_POPUP_NAME
        return AnalyticsConstant.EMPTY
    }

    private fun pickErrorMessage(): String {
        if (isFullError) return FULL_ERROR_MESSAGE
        if (isCvvError) return CVV_ERROR_MESSAGE
        return AnalyticsConstant.EMPTY
    }

    private fun pickScreenCategory(questionTwo: String, questionThree: String): String {
        if (AnalyticsConstant.TRUE == questionTwo) return AnalyticsConstant.ALTERNATIVE
        if (AnalyticsConstant.TRUE == questionThree) return AnalyticsConstant.ALTERNATIVE
        return AnalyticsConstant.HAPPY_PATH
    }

    private fun handleClickEvent(eventData: AnalyticEventData<*>) {
        if (isError.not()) {
            handleClickSuccessEvent(eventData)
            return
        }
        handleClickErrorEvent(eventData)
    }

    private fun handleClickSuccessEvent(eventData: AnalyticEventData<*>) {
        val subprocessType = eventData.data[AnalyticsConstant.TYPE_SUBPROCESS] as? String ?: return
        val questionOne = eventData.data[AnalyticsConstant.QUESTION_ONE] as? String ?: return
        val questionTwo = eventData.data[AnalyticsConstant.QUESTION_TWO] as? String ?: return
        val questionThree = eventData.data[AnalyticsConstant.QUESTION_THREE] as? String ?: return
        val screenCategory = pickScreenCategory(questionTwo, questionThree)
        val eventLabel = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenCLickEvent(
            screenCategory = screenCategory,
            subprocessType = subprocessType,
            questionOne = questionOne,
            questionTwo = questionTwo,
            questionThree = questionThree,
            eventLabel = eventLabel,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickErrorEvent(eventData: AnalyticEventData<*>) {
        val label = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val popupName = pickPopupName()
        val errorMessage = pickErrorMessage()
        val event: AnalyticsEvent = analyticFactory.whenCLickErrorEvent(
            popupName = popupName,
            errorMessage = errorMessage,
            eventLabel = label
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleShowInformativePopupEvent(eventData: AnalyticEventData<*>) {
        val subprocessType = eventData.data[AnalyticsConstant.TYPE_SUBPROCESS] as? String ?: return
        val questionOne = eventData.data[AnalyticsConstant.QUESTION_ONE] as? String ?: return
        val questionTwo = eventData.data[AnalyticsConstant.QUESTION_TWO] as? String ?: return
        val questionThree = eventData.data[AnalyticsConstant.QUESTION_THREE] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenShowInformativePopupEvent(
            subprocessType = subprocessType,
            questionOne = questionOne,
            questionTwo = questionTwo,
            questionThree = questionThree,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleClickInformativePopupEvent(eventData: AnalyticEventData<*>) {
        val subprocessType = eventData.data[AnalyticsConstant.TYPE_SUBPROCESS] as? String ?: return
        val questionOne = eventData.data[AnalyticsConstant.QUESTION_ONE] as? String ?: return
        val questionTwo = eventData.data[AnalyticsConstant.QUESTION_TWO] as? String ?: return
        val questionThree = eventData.data[AnalyticsConstant.QUESTION_THREE] as? String ?: return
        val eventLabel = eventData.data[AnalyticsConstant.EVENT_LABEL] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenCLickInformativePopupEvent(
            subprocessType = subprocessType,
            questionOne = questionOne,
            questionTwo = questionTwo,
            questionThree = questionThree,
            eventLabel = eventLabel,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    private fun handleShowSnackBarEvent(eventData: AnalyticEventData<*>) {
        val subprocessType = eventData.data[AnalyticsConstant.TYPE_SUBPROCESS] as? String ?: return
        val questionOne = eventData.data[AnalyticsConstant.QUESTION_ONE] as? String ?: return
        val questionTwo = eventData.data[AnalyticsConstant.QUESTION_TWO] as? String ?: return
        val questionThree = eventData.data[AnalyticsConstant.QUESTION_THREE] as? String ?: return
        val event: AnalyticsEvent = analyticFactory.whenShowSnackBarEvent(
            subprocessType = subprocessType,
            questionOne = questionOne,
            questionTwo = questionTwo,
            questionThree = questionThree,
        )
        analyticsDataGateway.sendEventV2(event)
    }

    companion object {
        private const val FULL_ERROR_MESSAGE = "no-se-puedo-generar-los-datos-de-tu-tarjeta"
        private const val CVV_ERROR_MESSAGE = "no-se-puede-generar-el-cvv-dinamico"
        private const val FULL_ERROR_POPUP_NAME = "error-credenciales"
        private const val CVV_ERROR_POPUP_NAME = "error-cvv-dinamico"
    }
}
