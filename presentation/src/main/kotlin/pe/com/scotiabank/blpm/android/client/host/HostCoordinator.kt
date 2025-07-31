package pe.com.scotiabank.blpm.android.client.host

import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import com.scotiabank.errorhandling.StoreOfSuspendingErrorHandling
import com.scotiabank.errorhandling.SuspendingReceiverOfError
import com.scotiabank.errorhandling.SuspendingReceivingAgentOfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.factories.maintenance.MaintenanceErrorSettingsFactory
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.calltoaction.CallToAction
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActionDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityNotFound
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEvent
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEventCarrier
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.exception.ExceptionWithResource
import pe.com.scotiabank.blpm.android.client.host.nosession.NoSessionCoordinator
import pe.com.scotiabank.blpm.android.client.host.nosession.NoSessionCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.osversioncheck.VersionCheckCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.session.SessionCoordinator
import pe.com.scotiabank.blpm.android.client.host.session.SessionCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.session.SessionModel
import pe.com.scotiabank.blpm.android.client.host.session.subflow.FactoryOfModalDataHolder
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SingleUseLauncher
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SubFlowLauncher
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SuccessfulAuthToLaunch
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.maintenance.MaintenanceActivity
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.nosession.login.factor.SuccessfulAuth
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.browserotp.BrowserOtpEvent
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.browserotp.BrowserOtpFlowCoordinator
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.browserotp.BrowserOtpFlowCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.CarrierOfPushData
import pe.com.scotiabank.blpm.android.client.util.countdown.CountDownFactory
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.data.exception.MaintenanceException
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import retrofit2.Retrofit
import java.lang.ref.WeakReference
import kotlin.time.Duration

class HostCoordinator(
    factoryOfMainTopComposite: MainTopComposite.Factory,
    private val hub: Hub,
    private val featureHubShortcut: FeatureHubShortcut,
    private val launcher: SubFlowLauncher,
    private val carrierOfPushData: CarrierOfPushData,
    private val idRegistry: IdRegistry,
    private val peruRetrofit: Retrofit,
    private val uriHolder: UriHolder,
    private val model: HostModel,
    private val countDownFactory: CountDownFactory,
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

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            GooglePlayEvent::class,
            InstancePredicate(GooglePlayEvent::filterInAttemptToRemediateGooglePlay),
            InstanceHandler(::handleAttemptToRemediateGooglePlay)
        )
        .add(
            ModalEventCarrier::class,
            InstancePredicate(::filterInGoingToShortcutDuringNoSession),
            InstanceHandler(::handleGoingToShortcutDuringNoSession)
        )
        .add(
            ModalEventCarrier::class,
            InstancePredicate(::filterInGoingToShortcutDuringSession),
            InstanceHandler(::handleGoingToShortcutDuringSession)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            MaintenanceException::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleMaintenanceError)
        )
        .add(
            CarrierOfActivityNotFound::class,
            InstancePredicate(::filterInMarketAppNotFound),
            SuspendingHandlerOfInstance(::handleActivityNotFound)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOpeningSession),
            SuspendingHandlerOfInstance(::handleOpeningSession)
        )
        .add(
            SubFlowLauncher::class,
            InstancePredicate(::filterInLauncherDuringNoSession),
            SuspendingHandlerOfInstance(::handleLauncherDuringNoSession)
        )
        .add(
            SubFlowLauncher::class,
            InstancePredicate(::filterInLauncherDuringSession),
            SuspendingHandlerOfInstance(::handleLauncherDuringSession)
        )
        .add(
            CarrierOfPushData::class,
            InstancePredicate(::filterCarrierOfPushData),
            SuspendingHandlerOfInstance(::handleCarrierOfPushData)
        )
        .add(
            BrowserOtpEvent::class,
            InstancePredicate(::filterCloseInSession),
            SuspendingHandlerOfInstance(::closeBrowserOtpInSession)
        )
        .add(
            BrowserOtpEvent::class,
            InstancePredicate(::filterCloseInNoSession),
            SuspendingHandlerOfInstance(::closeBrowserOtpInNoSession)
        )
        .add(
            CallToAction::class,
            InstancePredicate(::filterUnderstood),
            SuspendingHandlerOfInstance(::handleAttemptDisplayFeatureHub)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val exception: Throwable by lazy {
        ExceptionWithResource(R.string.exception_message_generic)
    }

    private val storeOfSuspendingErrorHandling: StoreOfSuspendingErrorHandling = StoreOfSuspendingErrorHandling.Builder()
        .putHandlerByType(
            GooglePlayServicesNotAvailableException::class,
            SuspendingHandlerOfInstance(::handleGooglePlayNotAvailable),
        )
        .putHandlerByType(
            GooglePlayServicesRepairableException::class,
            SuspendingHandlerOfInstance(userInterface::receive),
        )
        .setHandlerForCatchingAll(
            SuspendingHandlerOfInstance(FirebaseCrashlytics.getInstance()::recordException),
        )
        .build()
    private val selfSuspendingReceiverOfError: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = storeOfSuspendingErrorHandling,
    )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create()

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        mainTopComposites = listOf(mainTopComposite),
    )

    private val factoryOfModalDataHolder = FactoryOfModalDataHolder(
        dispatcherProvider = dispatcherProvider,
        weakResources = hub.weakResources,
        receiver = selfReceiver,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val appEventObserver: AppEventObserver by lazy {
        AppEventObserver(hub.appModel, weakSelf)
    }

    private val noSessionCoordinatorFactory: NoSessionCoordinatorFactory by lazy {
        NoSessionCoordinatorFactory(hub, peruRetrofit, scope, weakSelf)
    }

    private val versionCheckCoordinatorFactory: VersionCheckCoordinatorFactory by lazy {
        VersionCheckCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val sessionCoordinatorFactory: SessionCoordinatorFactory by lazy {
        SessionCoordinatorFactory(hub, scope, weakSelf)
    }

    private val browserOtpFlowCoordinatorFactory: BrowserOtpFlowCoordinatorFactory by lazy {
        BrowserOtpFlowCoordinatorFactory(hub, scope, weakSelf)
    }

    private val isAndroidUpdateRequired: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S

    override suspend fun start() = withContext(scope.coroutineContext) {
        uiStateHolder.currentState = UiState.LOADING
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        updateUiData()
        appEventObserver.start()
        tryInspectingGooglePlayAvailability()
    }

    private suspend fun tryInspectingGooglePlayAvailability() = try {
        val isGooglePlayAvailable: Boolean = model.isGooglePlayAvailable()
        onGooglePlayInspected(isGooglePlayAvailable)
    } catch (throwable: Throwable) {
        selfSuspendingReceiverOfError.receive(throwable)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleGooglePlayNotAvailable(throwable: GooglePlayServicesNotAvailableException) {
        showDialogOnGooglePlayNotAvailable()
    }

    private fun showDialogOnGooglePlayNotAvailable() {
        userInterface.receive(GooglePlayEvent.NOT_AVAILABLE)
    }

    private suspend fun onGooglePlayInspected(isAvailable: Boolean) {
        if (isAvailable) onGooglePlayAvailable() else showDialogOnGooglePlayNotAvailable()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleAttemptToRemediateGooglePlay(event: GooglePlayEvent) = scope.launch {
        tryInspectingGooglePlayAvailability()
    }

    private suspend fun onGooglePlayAvailable() {
        uiStateHolder.currentState = UiState.SUCCESS
        updateUiData()
        setUpAttemptDisplayingFeatureHub()
    }

    private suspend fun setUpAttemptDisplayingFeatureHub() {
        if (isAndroidUpdateRequired) {
            startVersionCheckScreen()
            return
        }

        checkNextScreen()
    }

    private fun filterUnderstood(
        callToAction: CallToAction
    ): Boolean = callToAction.id == CallToAction.UNDERSTOOD_PRIMARY.id

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleAttemptDisplayFeatureHub(callToAction: CallToAction) {
        checkNextScreen()
    }

    private suspend fun checkNextScreen() {
        val encryptedOtp: String = carrierOfPushData.otp
        if (encryptedOtp.isBlank()) goToNoSession(launcher) else goToBrowserOtp(carrierOfPushData)
    }

    private suspend fun goToBrowserOtp(carrier: CarrierOfPushData) {
        if (currentChild is BrowserOtpFlowCoordinator) {
            currentDeepChild.receiveFromAncestor(carrier)
            return
        }
        val child = browserOtpFlowCoordinatorFactory.create(carrier)
        addChild(child)
        child.start()
    }

    private suspend fun goToNoSession(launcher: SubFlowLauncher) {
        val countDownFlow: Flow<Duration> = countDownFactory.create()
        val singleUseLauncher = SingleUseLauncher(hub.dispatcherProvider, launcher, countDownFlow)
        val child: Coordinator = noSessionCoordinatorFactory.create(singleUseLauncher)
        addChild(child)
        child.start()
    }

    private suspend fun startVersionCheckScreen() {
        val child: Coordinator = versionCheckCoordinatorFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_POP_UP)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInMarketAppNotFound(
        carrier: CarrierOfActivityNotFound,
    ): Boolean = carrier.id == idRegistry.callMarketApp

    @Suppress("UNUSED_PARAMETER")
    private fun handleActivityNotFound(carrier: CarrierOfActivityNotFound) {
        val uri: Uri = uriHolder.callMarketUrlUri
        val carrierOfActionDestination = CarrierOfActionDestination(
            uriDestination = uri,
            id = idRegistry.callMarketUrl,
        )
        userInterface.receive(carrierOfActionDestination)
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleMaintenanceError(throwable: MaintenanceException) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = MaintenanceActivity::class.java,
        ) {
            MaintenanceActivity.ANALYTICS_MAINTENANCE_NAME_STEP to MaintenanceErrorSettingsFactory.STEP_CREDENTIALS
        }
        currentChild.updateUiData()
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(carrier)
    }

    private fun filterInOpeningSession(
        finishingChild: FinishingCoordinator,
    ): Boolean = finishingChild.data is SuccessfulAuthToLaunch

    private suspend fun handleOpeningSession(finishingChild: FinishingCoordinator) {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        val data: SuccessfulAuthToLaunch = finishingChild.data as? SuccessfulAuthToLaunch ?: return
        val successfulAuth: SuccessfulAuth = data.successfulAuth

        successfulAuth.emitterScope.launch {
            val retrofit: Retrofit = sessionCoordinatorFactory.createSessionRetrofit(
                codeVerifier = successfulAuth.codeVerifier,
            )
            val model: SessionModel = sessionCoordinatorFactory.createSessionModel(
                successfulAuth = successfulAuth,
                retrofit = retrofit,
            )
            tryOpeningSession(finishingChild, model)
        }
    }

    private suspend fun tryOpeningSession(
        finishingChild: FinishingCoordinator,
        model: SessionModel,
    ) = try {
        val isOpen: Boolean = model.openSession()
        onOpenedSession(finishingChild, isOpen)
    } catch (throwable: Exception) {
        showErrorMessage(throwable)
    }

    private suspend fun onOpenedSession(
        finishingChild: FinishingCoordinator,
        isOpen: Boolean,
    ) {
        if (isOpen.not()) {
            showErrorMessage(exception)
            return
        }
        appEventObserver.reset()
        removeChild(finishingChild.coordinator)
        val successfulAuthToLaunch: SuccessfulAuthToLaunch = finishingChild.data as? SuccessfulAuthToLaunch ?: return
        val child: SessionCoordinator = sessionCoordinatorFactory.create(successfulAuthToLaunch)
        addChild(child)
        child.start()
    }

    private fun filterInLauncherDuringNoSession(
        launcher: SubFlowLauncher,
    ): Boolean = launcher.shortcutId.isNotBlank() && currentChild is NoSessionCoordinator

    private suspend fun handleLauncherDuringNoSession(launcher: SubFlowLauncher) {
        showModalOnShortcut(R.string.shortcut_dialog_message_during_no_session, launcher)
    }

    private suspend fun showModalOnShortcut(@StringRes textResId: Int, launcher: SubFlowLauncher) {
        hideKeyboard()
        val dataHolder: ModalDataHolder = factoryOfModalDataHolder.create(textResId, launcher)
        userInterface.receive(dataHolder)
    }

    private fun filterInGoingToShortcutDuringNoSession(
        carrier: ModalEventCarrier,
    ): Boolean = isGoingToShortcut(carrier) && currentChild is NoSessionCoordinator

    private fun isGoingToShortcut(carrier: ModalEventCarrier): Boolean {
        val event: ModalEvent = carrier.event
        if (ModalEvent.PRIMARY_CLICKED != event) return false

        val dataHolder: ModalDataHolder = carrier.dataHolder
        return dataHolder.data is SubFlowLauncher
    }

    private fun handleGoingToShortcutDuringNoSession(carrier: ModalEventCarrier) = scope.launch {
        val dataHolder: ModalDataHolder = carrier.dataHolder
        val launcher: SubFlowLauncher = dataHolder.data as? SubFlowLauncher ?: return@launch
        removeChild(currentChild)
        goToNoSession(launcher)
    }

    private fun filterInLauncherDuringSession(
        launcher: SubFlowLauncher,
    ): Boolean = launcher.shortcutId.isNotBlank() && currentChild is SessionCoordinator

    private suspend fun handleLauncherDuringSession(launcher: SubFlowLauncher) {
        showModalOnShortcut(R.string.shortcut_dialog_message_during_session, launcher)
    }

    private fun filterInGoingToShortcutDuringSession(
        carrier: ModalEventCarrier,
    ): Boolean = isGoingToShortcut(carrier) && currentChild is SessionCoordinator

    private fun handleGoingToShortcutDuringSession(carrier: ModalEventCarrier) = scope.launch {
        val dataHolder: ModalDataHolder = carrier.dataHolder
        val launcher: SubFlowLauncher = dataHolder.data as? SubFlowLauncher ?: return@launch
        currentChild.receiveFromAncestor(launcher)
    }

    private fun filterCarrierOfPushData(
        carrier: CarrierOfPushData,
    ): Boolean = carrier.otp.isNotBlank()

    private suspend fun handleCarrierOfPushData(carrier: CarrierOfPushData) {
        goToBrowserOtp(carrier)
    }

    private fun filterCloseInSession(
        event: BrowserOtpEvent,
    ): Boolean = event == BrowserOtpEvent.CLOSE && hub.appModel.isOpenedSession

    @Suppress("UNUSED_PARAMETER")
    private suspend fun closeBrowserOtpInSession(event: BrowserOtpEvent) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterCloseInNoSession(
        event: BrowserOtpEvent,
    ): Boolean = event == BrowserOtpEvent.CLOSE && hub.appModel.isOpenedSession.not()

    @Suppress("UNUSED_PARAMETER")
    private suspend fun closeBrowserOtpInNoSession(event: BrowserOtpEvent) {
        removeChild(currentChild)
        if (currentChild is NoSessionCoordinator) {
            currentChild.updateUiData()
            return
        }
        removeChildren()
        goToNoSession(launcher)
    }

    override suspend fun onCoordinatorCleared() {
        appEventObserver.stop()
    }
}