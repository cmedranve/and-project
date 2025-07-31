package pe.com.scotiabank.blpm.android.client.atmcardhub.business

import android.content.Context
import android.content.res.Resources
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.CardHubCoordinator
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.CardHubCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.DataForPushOtpVerification
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro.CvvIntroCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.IntentionEvent
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.EmptyAnalyticConsumer
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.permission.CarrierOfPermissionResult
import pe.com.scotiabank.blpm.android.client.base.permission.PermissionResult
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.verification.FactoryOfChannelRegistry
import pe.com.scotiabank.blpm.android.client.base.verification.OtpVerificationEvent
import pe.com.scotiabank.blpm.android.client.base.verification.business.BusinessOtpVerificationCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.base.verification.business.DataForBusinessOtpVerification
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared.CardSettingsEvent
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.flow.CardSettingsFlowCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.PushKeyCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.error.OtpErrorType
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushError
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushSuccess
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.notification.turnon.NotificationPermissionCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.shared.numberinput.NumberInput
import pe.com.scotiabank.blpm.android.client.tasknav.clearThenNavigateToHost
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.permissions.NotificationsUtil
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import java.lang.ref.WeakReference

class EnabledCoordinator(
    private val hub: Hub,
    private val titleText: String,
    private val appModel: AppModel,
    private val weakResources: WeakReference<Resources?>,
    private val weakAppContext: WeakReference<Context?>,
    private val factoryOfChannelRegistry: FactoryOfChannelRegistry,
    private val credentialDataMapper: CredentialDataMapper,
    private val cardRepository: BusinessCardsRepository,
    private val otpRepository: BusinessOtpRepository,
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
            IntentionEvent::class,
            InstancePredicate(::filterInGoToCvvIntro),
            SuspendingHandlerOfInstance(::handleGoToCvvIntro)
        )
        .add(
            AtmCardInfo::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::goToCardSettings)
        )
        .add(
            DataForBusinessOtpVerification::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::goToOtpVerification)
        )
        .add(
            OtpVerificationEvent::class,
            InstancePredicate(::filterInCardVerified),
            SuspendingHandlerOfInstance(::handleCardVerified)
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
            CardSettingsEvent::class,
            InstancePredicate(::filterInSettingChanged),
            SuspendingHandlerOfInstance(::handleSettingChanged)
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
            credentialDataMapper = credentialDataMapper,
            businessCardRepository = cardRepository,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val cvvIntroFactory: CvvIntroCoordinatorFactory by lazy {
        CvvIntroCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val cardSettingsFlowFactory: CardSettingsFlowCoordinatorFactory by lazy {
        CardSettingsFlowCoordinatorFactory(
            hub = hub,
            businessCardRepository = cardRepository,
            businessOtpRepository = otpRepository,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val otpVerificationFactory: BusinessOtpVerificationCoordinatorFactory by lazy {
        BusinessOtpVerificationCoordinatorFactory(
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
            embeddedDataName = String.EMPTY,
            weakParent = weakSelf,
            hideErrorPage = false,
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

    private fun filterInGoToCvvIntro(
        event: IntentionEvent
    ): Boolean = IntentionEvent.GO_TO_CVV_INTRO == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleGoToCvvIntro(event: IntentionEvent) {
        val child: Coordinator = cvvIntroFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private suspend fun goToCardSettings(card: AtmCardInfo) {
        val child: Coordinator = cardSettingsFlowFactory.create(card)
        addChild(child)
        child.start()
    }

    private suspend fun goToOtpVerification(data: DataForBusinessOtpVerification) {
        val child: Coordinator = otpVerificationFactory.create(
            titleText = weakResources.get()?.getString(data.titleResId).orEmpty(),
            transactionId = data.transactionId,
            transactionType = data.transactionType,
            eventOnOtpVerified = OtpVerificationEvent.ON_BUSINESS_CARD_OTP_VERIFIED,
        )
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
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
            analyticConsumer = EmptyAnalyticConsumer,
            analyticAdditionalData = Unit,
            dataFromConsumer = Unit,
        )
        addChild(child)
        mutableLiveHolder.anchoredBottom.postValue(emptyList())
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        child.start()
    }

    private fun filterInOtpPushDismissed(finishingChild: FinishingCoordinator): Boolean {
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInNone(type = otpPushError.otpError)
    }

    private suspend fun handleOtpPushDismissed(finishingCoordinator: FinishingCoordinator) {
        removeChild(finishingCoordinator.coordinator)
        currentDeepChild.updateUiData()
    }

    private fun filterInOtpPushGenericError(finishingChild: FinishingCoordinator): Boolean {
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInSomethingWentWrong(type = otpPushError.otpError)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOtpPushGenericError(finishingCoordinator: FinishingCoordinator) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInOtpPushMaxAttemptsError(finishingChild: FinishingCoordinator): Boolean {
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

    private fun filterInCardVerified(
        event: OtpVerificationEvent,
    ): Boolean = event == OtpVerificationEvent.ON_BUSINESS_CARD_OTP_VERIFIED

    private suspend fun handleCardVerified(event: OtpVerificationEvent) {
        removeChild(currentChild)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        currentDeepChild.receiveFromAncestor(event)
    }

    private fun filterInSettingChanged(
        event: CardSettingsEvent,
    ): Boolean = event == CardSettingsEvent.CARD_SETTING_CHANGED

    @Suppress("UNUSED_PARAMETER")
    private fun handleSettingChanged(event: CardSettingsEvent) {
        children.firstOrNull(::isCardHubCoordinator)?.receiveFromAncestor(IntentionEvent.REFRESH_CARD_HUB)
    }

    private fun isCardHubCoordinator(
        child: Coordinator,
    ): Boolean = child.currentDeepChild is CardHubCoordinator
}
