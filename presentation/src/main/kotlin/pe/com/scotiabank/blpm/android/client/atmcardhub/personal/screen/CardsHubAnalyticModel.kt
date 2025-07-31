package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen

import androidx.core.util.Consumer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.products.dashboard.hub.CardsHubFactory
import pe.com.scotiabank.blpm.android.analytics.factories.products.dashboard.hub.CardsHubFactory.Companion.PROBLEM_WITH_YOUR_CARD
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.ACCOUNT_TYPE
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.ALTERNATIVE
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.AUTHENTICATION_CHANNEL
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.DEBIT_CREDIT
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.DIGITAL_KEY
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.ERROR
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.ERROR_CODE
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.ERROR_MESSAGE
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.ERROR_TYPE
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.EVENT_LABEL
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.HAPPY_PATH
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.PRODUCT
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.PRODUCTS
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.PRODUCT_TYPE
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.QUESTION_FOUR
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.QUESTION_ONE
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.QUESTION_THREE
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.QUESTION_TWO
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.SCREEN_STATUS
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant.SCREEN_TYPE
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.consumer.tryAccepting
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.util.Constant.DASH

class CardsHubAnalyticModel(
    private val analyticsDataGateway: AnalyticsDataGateway,
    private val analyticFactory: CardsHubFactory,
) : Consumer<AnalyticEventData<*>> {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterScreenViewEvent),
            InstanceHandler(::handleCardsViewEvent)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterClickEvent),
            InstanceHandler(::handleClickAction)
        )
        .add(
            AnalyticEventData::class,
            InstancePredicate(AnalyticEvent::filterErrorScreenEvent),
            InstanceHandler(::handleCardsErrorEvent)
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

    private var atmCardType: String = String.EMPTY
    private var accountType: String = String.EMPTY
    var isCreditCardsErrorForAnalytics: Boolean = false
    var isDebitCardsErrorForAnalytics: Boolean = false
    private val isFullError: Boolean
        get() = isCreditCardsErrorForAnalytics && isDebitCardsErrorForAnalytics
    private val isPartialError: Boolean
        get() = isCreditCardsErrorForAnalytics || isDebitCardsErrorForAnalytics

    override fun accept(value: AnalyticEventData<*>) {
        tryAccepting(value, selfReceiver, FirebaseCrashlytics.getInstance()::recordException)
    }

    private fun handleCardsViewEvent(event: AnalyticEventData<*>) {
        if (isFullError) {
            handleCardsErrorEvent(event)
            return
        }
        sendCardsViewEvent(event)
    }

    private fun sendCardsViewEvent(event: AnalyticEventData<*>) {
        val screenCategory = if (isPartialError) ALTERNATIVE else HAPPY_PATH
        val screenStatus = event.data[SCREEN_STATUS] as? String ?: return
        val questionOne = event.data[QUESTION_ONE] as? String ?: return
        val questionTwo = event.data[QUESTION_TWO] as? String ?: return
        val questionThree = event.data[QUESTION_THREE] as? String ?: return
        val extraLine = event.data[QUESTION_FOUR] as? String ?: return

        val products: List<NewProductModel> = event.data[PRODUCTS] as? List<NewProductModel> ?: return
        val questionFive = concatenateDebtsDescriptionsToAnalytics(products)

        val createMyCardsView: AnalyticsEvent = analyticFactory.whenCreateMyCardsView(
            screenCategory = screenCategory,
            screenStatus = screenStatus,
            questionOne = questionOne,
            questionTwo = questionTwo,
            questionThree = questionThree,
            questionFour = extraLine,
            questionFive = questionFive,
        )
        analyticsDataGateway.sendEventV2(createMyCardsView)
    }

    private fun concatenateDebtsDescriptionsToAnalytics(products: List<NewProductModel>): String {
        val debtsDescriptionsToAnalytics: StringBuilder = StringBuilder()
        products
            .filter(::isValidExpirationDateDescription)
            .map(::concatenateNameWithDebtDescription)
            .forEach(debtsDescriptionsToAnalytics::append)
        return debtsDescriptionsToAnalytics.toString()
    }

    private fun isValidExpirationDateDescription(
        newProductModel: NewProductModel,
    ): Boolean = newProductModel.expirationDateDescription.isNullOrEmpty().not()

    private fun concatenateNameWithDebtDescription(product: NewProductModel): String {
        if (product.expirationDateDescription.isNullOrEmpty()) return String.EMPTY

        val debtDescription: String = product.name.orEmpty()
            .plus(Constant.SPACE_WHITE)
            .plus(product.expirationDateDescription)
            .plus(Constant.SPACE_WHITE)
        return debtDescription
    }

    private fun handleClickAction(event: AnalyticEventData<*>) = when {
        isFullError -> handleClickActionFromError(event)
        else -> handleClickActionFromSuccess(event)
    }

    private fun handleClickActionFromSuccess(event: AnalyticEventData<*>) {
        val screenCategory = if (isPartialError) ALTERNATIVE else HAPPY_PATH
        val eventLabel = event.data[EVENT_LABEL] as? String ?: return
        val screenStatus = event.data[SCREEN_STATUS] as? String ?: return
        val numDebitCards = event.data[QUESTION_ONE] as? String ?: return
        val numCreditCards = event.data[QUESTION_TWO] as? String ?: return
        atmCardType = event.data[PRODUCT_TYPE] as? String ?: return
        accountType = event.data[ACCOUNT_TYPE] as? String ?: return
        val pendingCard = event.data[QUESTION_THREE] as? String ?: return
        val product = event.data[PRODUCT] as? NewProductModel ?: return
        val extraLine = event.data[QUESTION_FOUR] as? String ?: return

        val paymentStatus = concatenateNameWithDebtDescription(product)
        val clickCardsSuccessfully: AnalyticsEvent = analyticFactory.whenClickInteractionFromSuccess(
            screenCategory = screenCategory,
            eventLabel = eventLabel,
            screenStatus = screenStatus,
            numDebitCards = numDebitCards,
            numCreditCards = numCreditCards,
            atmCardType = atmCardType,
            accountType = accountType,
            pendingCard = pendingCard,
            paymentStatus = paymentStatus,
            extraLine = extraLine,
        )
        analyticsDataGateway.sendEventV2(clickCardsSuccessfully)
    }

    private fun handleClickActionFromError(event: AnalyticEventData<*>) {
        val eventLabel = event.data[EVENT_LABEL] as? String ?: return
        val clickCardsFullError: AnalyticsEvent = analyticFactory.whenClickInteractionFromError(
            eventLabel = eventLabel,
            errorMessage = PROBLEM_WITH_YOUR_CARD,
            errorType = ERROR,
        )
        analyticsDataGateway.sendEventV2(clickCardsFullError)
    }

    private fun handleCardsErrorEvent(event: AnalyticEventData<*>) {
        val errorType = event.data[ERROR_TYPE] as? String ?: (DEBIT_CREDIT + DASH + ERROR)
        val errorMessage = event.data[ERROR_MESSAGE]  as? String ?: PROBLEM_WITH_YOUR_CARD
        val cardsShowError: AnalyticsEvent = analyticFactory.whenCardsShowError(
            errorType = errorType,
            errorMessage = errorMessage,
        )
        analyticsDataGateway.sendEventV2(cardsShowError)
    }

    private fun handlePushOtpEvent(event: AnalyticEventData<*>) {
        val authenticationChannel = event.data[AUTHENTICATION_CHANNEL] as? String ?: return

        val popUpEvent: AnalyticsEvent = analyticFactory.whenShowPopUp(
            popupName = DIGITAL_KEY,
            atmCardType = atmCardType,
            accountType = accountType,
            authenticationChannel = authenticationChannel,
        )
        analyticsDataGateway.sendEventV2(popUpEvent)
    }

    private fun handlePushOtpClickEvent(event: AnalyticEventData<*>) {
        val authenticationChannel = event.data[AUTHENTICATION_CHANNEL] as? String ?: return
        val eventLabel = event.data[EVENT_LABEL] as? String ?: return

        val clickPopUpEvent: AnalyticsEvent = analyticFactory.whenClickPopUp(
            popupName = DIGITAL_KEY,
            eventLabel = eventLabel,
            atmCardType = atmCardType,
            accountType = accountType,
            authenticationChannel = authenticationChannel,
        )
        analyticsDataGateway.sendEventV2(clickPopUpEvent)
    }

    private fun handlePushOtpErrorEvent(event: AnalyticEventData<*>) {
        val screenType = event.data[SCREEN_TYPE] as? String ?: return
        val authenticationChannel = event.data[AUTHENTICATION_CHANNEL] as? String ?: return
        val errorCode = event.data[ERROR_CODE] as? String ?: return
        val errorMessage = event.data[ERROR_MESSAGE] as? String ?: return

        val errorPushOtpEvent: AnalyticsEvent = analyticFactory.whenErrorPushOtp(
            screenType = screenType,
            errorCode = errorCode,
            errorMessage = errorMessage,
            atmCardType = atmCardType,
            accountType = accountType,
            authenticationChannel = authenticationChannel,
        )
        analyticsDataGateway.sendEventV2(errorPushOtpEvent)
    }

    private fun handlePushOtpErrorClickEvent(event: AnalyticEventData<*>) {
        val screenType = event.data[SCREEN_TYPE] as? String ?: return
        val authenticationChannel = event.data[AUTHENTICATION_CHANNEL] as? String ?: return
        val errorCode = event.data[ERROR_CODE] as? String ?: return
        val errorMessage = event.data[ERROR_MESSAGE] as? String ?: return
        val eventLabel = event.data[EVENT_LABEL] as? String ?: return

        val clickErrorPushOtpEvent: AnalyticsEvent = analyticFactory.whenClickErrorPushOtp(
            screenType = screenType,
            eventLabel = eventLabel,
            errorCode = errorCode,
            errorMessage = errorMessage,
            atmCardType = atmCardType,
            accountType = accountType,
            authenticationChannel = authenticationChannel,
        )
        analyticsDataGateway.sendEventV2(clickErrorPushOtpEvent)
    }
}
