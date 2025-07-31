package pe.com.scotiabank.blpm.android.client.atmcardhub.personal

import android.content.Context
import com.scotiabank.enhancements.encoding.mapToCharArray
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.atmcardhub.personal.screen.CardHubCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.DataForOtpVerification
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.DataForPushOtpVerification
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro.CvvIntroCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.IntentionEvent
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.calltoaction.CallToAction
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.permission.CarrierOfPermissionResult
import pe.com.scotiabank.blpm.android.client.base.permission.PermissionResult
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.verification.FactoryOfChannelRegistry
import pe.com.scotiabank.blpm.android.client.base.verification.OtpVerificationEvent
import pe.com.scotiabank.blpm.android.client.cards.walletnotinstalled.GoogleWalletNotInstalledActivity
import pe.com.scotiabank.blpm.android.client.cardsettings.CardSettingHubHostActivity
import pe.com.scotiabank.blpm.android.client.cardsettings.CardSettingsConstants.CARD_SETTINGS_DETAIL
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.CardSettingsActivity
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.CardInfoForSetting
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.flow.CardSettingsFlowCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.creditcard.activation.CreditCardActivationCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.debitcard.DebitCard
import pe.com.scotiabank.blpm.android.client.debitcard.PendingCard
import pe.com.scotiabank.blpm.android.client.debitcard.detail.DebitCardFlowCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.debitcard.otpverification.DebitCardOtpVerificationCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.debitcard.pending.PendingDebitCardCoordinator
import pe.com.scotiabank.blpm.android.client.debitcard.pending.PendingDebitCardCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.mapview.MapActivity
import pe.com.scotiabank.blpm.android.client.mapview.MapMode
import pe.com.scotiabank.blpm.android.client.model.CardDetailModel
import pe.com.scotiabank.blpm.android.client.newdashboard.products.NewProductModel
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.PushKeyCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.error.OtpErrorType
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushError
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushSuccess
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.notification.turnon.NotificationPermissionCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.shared.numberinput.NumberInput
import pe.com.scotiabank.blpm.android.client.restrictedprofile.alert.RestrictedProfileAlertActivity
import pe.com.scotiabank.blpm.android.client.tasknav.clearThenNavigateToHost
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.permissions.NotificationsUtil
import pe.com.scotiabank.blpm.android.data.repository.OldCardSettingsDataRepository
import pe.com.scotiabank.blpm.android.data.repository.NewGatesDataRepository
import pe.com.scotiabank.blpm.android.data.repository.cardsettings.CardSettingDataRepository
import pe.com.scotiabank.blpm.android.data.repository.collections.CollectionsRepository
import pe.com.scotiabank.blpm.android.data.repository.creditcard.CreditCardRepository
import pe.com.scotiabank.blpm.android.data.repository.debitcard.DebitCardRepository
import pe.com.scotiabank.blpm.android.data.repository.googlepay.NewGooglePayRepository
import pe.com.scotiabank.blpm.android.data.repository.products.stable.ProductRepository
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class EnabledCoordinator(
    private val hub: Hub,
    private val retrofit: Retrofit,
    private val titleText: String,
    private val isDeepLink: Boolean,
    private val appModel: AppModel,
    private val weakAppContext: WeakReference<Context?>,
    private val factoryOfChannelRegistry: FactoryOfChannelRegistry,
    private val credentialDataMapper: CredentialDataMapper,
    private val gatesDataRepository: NewGatesDataRepository,
    private val productRepository: ProductRepository,
    private val debitCardRepository: DebitCardRepository,
    private val creditCardRepository: CreditCardRepository,
    private val cardSettingsDataRepository: OldCardSettingsDataRepository,
    private val newCardSettingsDataRepository: CardSettingDataRepository,
    private val googlePayRepository: NewGooglePayRepository,
    private val collectionsRepository: CollectionsRepository,
    weakParent: WeakReference<out Coordinator?>,
    scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    mutableLiveHolder: MutableLiveHolder,
    userInterface: InstanceReceiver,
    uiStateHolder: UiStateHolder,
    override val id: Long = randomLong(),
) : CoordinatorImpl(
    weakParent = weakParent,
    scope = scope,
    dispatcherProvider = dispatcherProvider,
    mutableLiveHolder = mutableLiveHolder,
    userInterface = userInterface,
    uiStateHolder = uiStateHolder,
) {

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            NewProductModel::class,
            InstancePredicate(::filterInClickOnPendingCreditCard),
            SuspendingHandlerOfInstance(::handleClickOnPendingCreditCard)
        )
        .add(
            CallToAction::class,
            InstancePredicate(::filterInCallToActionOnUnderstood),
            SuspendingHandlerOfInstance(::handleCallToActionOnUnderstood)
        )
        .add(
            CallToAction::class,
            InstancePredicate(::filterInCallToActionOnLookForBranch),
            SuspendingHandlerOfInstance(::handleCallToActionOnLookForBranch)
        )
        .add(
            CallToAction::class,
            InstancePredicate(::filterInCallToActionOnGoToHome),
            SuspendingHandlerOfInstance(::handleCallToActionOnGoToHome)
        )
        .add(
            CallToAction::class,
            InstancePredicate(::filterInCallToActionOnGoToCardHub),
            SuspendingHandlerOfInstance(::handleCallToActionOnGoToCardHub)
        )
        .add(
            PendingCard::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::goToPendingDebitCard)
        )
        .add(
            DataForOtpVerification::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::goToOtpVerification)
        )
        .add(
            OtpVerificationEvent::class,
            InstancePredicate(::filterInDebitCardVerified),
            SuspendingHandlerOfInstance(::handleDebitCardOtpVerified)
        )
        .add(
            DataForPushOtpVerification::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleRequiringOtpPushVerification)
        )
        .add(
            CarrierOfPermissionResult::class,
            InstancePredicate(::filterInNotificationPermissionDenied),
            SuspendingHandlerOfInstance(::handleNotificationPermissionDenied)
        )
        .add(
            CarrierOfPermissionResult::class,
            InstancePredicate(::filterInNotificationPermissionGranted),
            SuspendingHandlerOfInstance(::handleNotificationPermissionGranted)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOtpPushDismissed),
            SuspendingHandlerOfInstance(::handleOtpPushDismissed)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOtpPushGenericError),
            SuspendingHandlerOfInstance(::handleOtpPushGenericError)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOtpPushMaxAttemptsError),
            SuspendingHandlerOfInstance(::handleOtpPushMaxAttemptsError)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInSuccessOtpPush),
            SuspendingHandlerOfInstance(::handleSuccessOtpPush)
        )
        .add(
            DebitCard::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleGoingToDebitCard)
        )
        .add(
            CardDetailModel::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::goToCardSettings)
        )
        .add(
            CardInfoForSetting::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleCard)
        )
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInGoogleWalletUnavailable),
            SuspendingHandlerOfInstance(::goToGoogleWalletUnavailable)
        )
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInGoToCvvIntro),
            SuspendingHandlerOfInstance(::handleGoToCvvIntro)
        )
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInGoToRestrictedProfileAlert),
            SuspendingHandlerOfInstance(::handleGoToRestrictedProfileAlert)
        )
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInGoToCardSettingHub),
            SuspendingHandlerOfInstance(::goToCardSettingHubScreen)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val cardHubFactory: CardHubCoordinatorFactory by lazy {
        CardHubCoordinatorFactory(
            hub = hub,
            titleText = titleText,
            isDeepLink = isDeepLink,
            credentialDataMapper = credentialDataMapper,
            gatesDataRepository = gatesDataRepository,
            productRepository = productRepository,
            debitCardRepository = debitCardRepository,
            creditCardRepository = creditCardRepository,
            cardSettingsDataRepository = cardSettingsDataRepository,
            newCardSettingsDataRepository = newCardSettingsDataRepository,
            collectionRepository = collectionsRepository,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val creditCardActivationFactory: CreditCardActivationCoordinatorFactory by lazy {
        CreditCardActivationCoordinatorFactory(
            hub = hub,
            cardSettingsDataRepository = cardSettingsDataRepository,
            googlePayRepository = googlePayRepository,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val pendingDebitCardFactory: PendingDebitCardCoordinatorFactory by lazy {
        PendingDebitCardCoordinatorFactory(
            hub = hub,
            repository = debitCardRepository,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val debitCardOtpVerificationFactory: DebitCardOtpVerificationCoordinatorFactory by lazy {
        DebitCardOtpVerificationCoordinatorFactory(
            hub = hub,
            factoryOfChannelRegistry = factoryOfChannelRegistry,
            numberInput = NumberInput.DIGITAL_KEY,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val notificationPermissionCoordinatorFactory: NotificationPermissionCoordinatorFactory by lazy {
        NotificationPermissionCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val pushKeyCoordinatorFactory by lazy {
        PushKeyCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            embeddedDataName = Constant.ANALYTICS_PUSH_DATA,
            weakParent = weakSelf,
            hideErrorPage = false,
        )
    }

    private val debitCardFlowFactory: DebitCardFlowCoordinatorFactory by lazy {
        DebitCardFlowCoordinatorFactory(
            hub = hub,
            credentialDataMapper = credentialDataMapper,
            debitCardRepository = debitCardRepository,
            cardSettingsDataRepository = cardSettingsDataRepository,
            newCardSettingsDataRepository = newCardSettingsDataRepository,
            googlePayRepository = googlePayRepository,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val cvvIntroCoordinatorFactory: CvvIntroCoordinatorFactory by lazy {
        CvvIntroCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val newSettingsFactory: CardSettingsFlowCoordinatorFactory by lazy {
        CardSettingsFlowCoordinatorFactory(
            hub = hub,
            retrofit = retrofit,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        val child: Coordinator = cardHubFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        child.start()
    }

    private fun filterInClickOnPendingCreditCard(
        product: NewProductModel
    ): Boolean = AtmCardType.CREDIT.id.contentEquals(product.productType) && product.isInactive

    private suspend fun handleClickOnPendingCreditCard(product: NewProductModel) {
        val child: Coordinator = creditCardActivationFactory.create(product)
        addChild(child)
        child.start()
    }

    private fun filterInCallToActionOnUnderstood(
        callToAction: CallToAction
    ): Boolean = CallToAction.UNDERSTOOD_PRIMARY.id == callToAction.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleCallToActionOnUnderstood(callToAction: CallToAction) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInCallToActionOnLookForBranch(
        callToAction: CallToAction
    ): Boolean = CallToAction.LOOK_FOR_NEAR_BRANCH_PRIMARY.id == callToAction.id

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleCallToActionOnLookForBranch(callToAction: CallToAction) {
        removeChild(currentChild)
        goToMap(MapMode.NO_AGENTS_NO_ATM)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInCallToActionOnGoToHome(
        callToAction: CallToAction
    ): Boolean = CallToAction.GO_TO_HOME_PRIMARY.id == callToAction.id
            || CallToAction.GO_TO_HOME_SECONDARY.id == callToAction.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleCallToActionOnGoToHome(callToAction: CallToAction) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInCallToActionOnGoToCardHub(
        callToAction: CallToAction
    ): Boolean = CallToAction.SEE_MY_CREDIT_CARD_PRIMARY.id == callToAction.id

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleCallToActionOnGoToCardHub(callToAction: CallToAction) {
        removeChild(currentChild)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.receiveFromAncestor(IntentionEvent.CREDIT_CARD_CREATED)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    @Suppress("SameParameterValue")
    private fun goToMap(mapMode: MapMode) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = MapActivity::class.java
        ) {
            MapActivity.PARAM_MAP_MODE to mapMode.value
        }
        userInterface.receive(carrier)
    }

    private suspend fun goToPendingDebitCard(pendingCard: PendingCard) {
        val child: Coordinator = pendingDebitCardFactory.create(pendingCard)
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private suspend fun goToOtpVerification(data: DataForOtpVerification) {
        val child: Coordinator = debitCardOtpVerificationFactory.create(
            operationId = data.debitOperationId,
            operation = Constant.TRANSACTIONAL.mapToCharArray(),
            eventOnOtpVerified = OtpVerificationEvent.ON_DEBIT_CARD_OTP_VERIFIED,
        )
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInDebitCardVerified(
        event: OtpVerificationEvent,
    ): Boolean = event == OtpVerificationEvent.ON_DEBIT_CARD_OTP_VERIFIED

    private suspend fun handleDebitCardOtpVerified(event: OtpVerificationEvent) {
        removeChild(currentChild)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        currentDeepChild.receiveFromAncestor(event)
    }

    private suspend fun handleRequiringOtpPushVerification(data: DataForPushOtpVerification) {
        val isNotificationsEnabled: Boolean = NotificationsUtil.areNotificationsEnabled(weakAppContext)
        if (isNotificationsEnabled) goToOtpPushVerification(data) else goToNotificationPermission(data)
    }

    private suspend fun goToNotificationPermission(data: DataForPushOtpVerification) {
        val child: Coordinator = notificationPermissionCoordinatorFactory.create(data)
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInNotificationPermissionDenied(
        carrier: CarrierOfPermissionResult,
    ): Boolean = PermissionResult.NOT_GRANTED == carrier.result

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleNotificationPermissionDenied(carrier: CarrierOfPermissionResult) {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        appModel.receive(SessionEvent.ENDING)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        weakAppContext.get()?.let(::clearThenNavigateToHost)
    }

    private fun filterInNotificationPermissionGranted(
        carrier: CarrierOfPermissionResult,
    ): Boolean = PermissionResult.GRANTED == carrier.result

    private suspend fun handleNotificationPermissionGranted(carrier: CarrierOfPermissionResult) {
        val data: DataForPushOtpVerification = carrier.dataFromConsumer as? DataForPushOtpVerification
            ?: return
        removeChild(currentChild)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        goToOtpPushVerification(data)
    }

    private suspend fun goToOtpPushVerification(data: DataForPushOtpVerification) {
        val child = pushKeyCoordinatorFactory.create(
            transactionId = data.transactionId,
            analyticConsumer = data.analyticConsumer,
            analyticAdditionalData = data.analyticAdditionalData,
            dataFromConsumer = Unit,
        )
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        child.start()
    }

    private fun filterInOtpPushDismissed(finishingChild: FinishingCoordinator): Boolean {
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInNone(type = otpPushError.otpError)
    }

    private suspend fun handleOtpPushDismissed(finishingCoordinator: FinishingCoordinator) {
        removeChild(finishingCoordinator.coordinator)
    }

    private fun filterInOtpPushGenericError(finishingChild: FinishingCoordinator): Boolean{
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInSomethingWentWrong(type = otpPushError.otpError)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOtpPushGenericError(finishingCoordinator: FinishingCoordinator) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInOtpPushMaxAttemptsError(finishingChild: FinishingCoordinator): Boolean{
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInMaxAttempts(type = otpPushError.otpError)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOtpPushMaxAttemptsError(finishingCoordinator: FinishingCoordinator) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInSuccessOtpPush(
        finishingChild: FinishingCoordinator
    ): Boolean = finishingChild.data is OtpPushSuccess

    private suspend fun handleSuccessOtpPush(finishingCoordinator: FinishingCoordinator) {
        val data: OtpPushSuccess = finishingCoordinator.data as? OtpPushSuccess ?: return
        removeChild(currentChild)
        currentDeepChild.receiveFromAncestor(data)
    }

    private suspend fun handleGoingToDebitCard(debitCard: DebitCard) {
        if (currentChild is PendingDebitCardCoordinator) {
            removeChild(currentChild)
            userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
            currentChild.receiveFromAncestor(IntentionEvent.CARD_CREATED)
        }
        val child: Coordinator = debitCardFlowFactory.create(debitCard)
        addChild(child)
        child.start()
    }

    private fun goToCardSettings(cardSettings: CardDetailModel) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = CardSettingsActivity::class.java,
        ) {
            CARD_SETTINGS_DETAIL to cardSettings
        }
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(carrier)
    }

    private suspend fun handleCard(cardInfo: CardInfoForSetting) {
        val child: Coordinator = newSettingsFactory.create(cardInfo)
        addChild(child)
        child.start()
    }

    private fun filterInGoogleWalletUnavailable(
        event: IntentionEvent
    ): Boolean = IntentionEvent.ON_GOOGLE_WALLET_UNAVAILABLE == event

    @Suppress("UNUSED_PARAMETER")
    private fun goToGoogleWalletUnavailable(event: IntentionEvent) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            GoogleWalletNotInstalledActivity::class.java
        ) {
            AnalyticsConstant.PREVIOUS_SECTION to AnalyticsConstant.CARDS_HUB
        }
        userInterface.receive(carrier)
    }

    private fun filterInGoToCvvIntro(
        event: IntentionEvent
    ): Boolean = IntentionEvent.GO_TO_CVV_INTRO == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleGoToCvvIntro(event: IntentionEvent) {
        val child: Coordinator = cvvIntroCoordinatorFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInGoToCardSettingHub(
        event: IntentionEvent
    ): Boolean = IntentionEvent.GO_TO_CARD_SETTING_HUB == event

    @Suppress("UNUSED_PARAMETER")
    private fun goToCardSettingHubScreen(event: IntentionEvent) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = CardSettingHubHostActivity::class.java,
        )
        userInterface.receive(carrier)
    }

    private fun filterInGoToRestrictedProfileAlert(
        event: IntentionEvent
    ): Boolean = IntentionEvent.GO_TO_RESTRICTED_PROFILE_ALERT == event


    @Suppress("UNUSED_PARAMETER")
    private fun handleGoToRestrictedProfileAlert(event: IntentionEvent) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            RestrictedProfileAlertActivity::class.java
        )
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(carrier)
    }
}
