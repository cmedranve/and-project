package pe.com.scotiabank.blpm.android.client.host.session

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.session.analytics.logindashboard.LoginAnalyticModel
import pe.com.scotiabank.blpm.android.client.host.user.UserModel
import pe.com.scotiabank.blpm.android.client.medallia.setup.MedalliaFacade
import pe.com.scotiabank.blpm.android.client.medallia.util.MedalliaConstants
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.newdashboard.NewDashboardActivity
import pe.com.scotiabank.blpm.android.client.newdashboard.businessdashboard.BusinessDashboardActivity
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEvent
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEventCarrier
import pe.com.scotiabank.blpm.android.client.base.permission.CarrierOfPermissionResult
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent
import pe.com.scotiabank.blpm.android.client.host.session.postloading.PostLoadingCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SubFlowLauncher
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SubFlowFactory
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.enrollment.refreshtoken.FirebaseTokenEvent
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.enrollment.refreshtoken.RefreshTokenModel
import pe.com.scotiabank.blpm.android.client.base.permission.PermissionResult
import pe.com.scotiabank.blpm.android.client.dashboard.DashboardCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.notification.turnon.NotificationPermissionCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.tasknav.clearThenNavigateToHost
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.permissions.NotificationsUtil
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import java.lang.ref.WeakReference

class SessionCoordinator(
    private val hub: Hub,
    private val weakResources: WeakReference<Resources?>,
    private val weakAppContext: WeakReference<Context?>,
    private val launcher: SubFlowLauncher,
    private val userModel: UserModel,
    private val appModel: AppModel,
    private val refreshTokenModel: RefreshTokenModel,
    private val idRegistry: IdRegistry,
    private val isQrDeepLink: Boolean,
    private val loginAnalyticModel: LoginAnalyticModel,
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
            ModalEventCarrier::class,
            InstancePredicate(::filterInClickOnLogOut),
            InstanceHandler(::handleClickOnLogOut)
        )
        .add(
            FirebaseTokenEvent::class,
            InstancePredicate(::filterOnNewToken),
            InstanceHandler(::handleOnNewTokenEvent)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(
        store = handlingStore,
    )

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            SubFlowLauncher::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleLauncher)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInBackFromChild),
            SuspendingHandlerOfInstance(::handleBackFromChild)
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
            FirebaseTokenEvent::class,
            InstancePredicate(::filterOnNewToken),
            SuspendingHandlerOfInstance(::handleOnNewTokenEvent)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val dashboardDestination: Class<out AppCompatActivity>
        get() {
            if (DashboardType.BUSINESS === appModel.dashboardType) return BusinessDashboardActivity::class.java
            return NewDashboardActivity::class.java
        }

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val modalDataHolder: ModalDataHolder by lazy {
        val attrs = AttrsCanvasDialogModal(
            title = weakResources.get()?.getString(R.string.warning).toString(),
            textBody = weakResources.get()?.getString(R.string.logout_message).toString(),
            primaryButtonLabel = weakResources.get()?.getString(R.string.yes_logout).toString(),
            secondaryButtonLabel = weakResources.get()?.getString(R.string.no).toString(),
        )
        ModalDataHolder(
            attrs = attrs,
            receiver = selfReceiver,
            id = idRegistry.modalIdOnLoggingOut,
            buttonDirectionColumn = true,
        )
    }

    private val postLoadingFactory: PostLoadingCoordinatorFactory by lazy {
        PostLoadingCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val dashboardFactory: DashboardCoordinatorFactory by lazy {
        DashboardCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
            isQrDeepLink = isQrDeepLink,
        )
    }

    private val notificationPermissionCoordinatorFactory: NotificationPermissionCoordinatorFactory by lazy {
        NotificationPermissionCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        FirebaseMessaging.getInstance().isAutoInitEnabled  = true
        checkNotificationsPermission()
    }

    private suspend fun checkNotificationsPermission() {
        if (appModel.profile.isPushOtpEnabled && NotificationsUtil.areNotificationsEnabled(hub.weakAppContext).not()) {
            goToNotificationPermissionScreen()
            return
        }
        onOpenedSession()
    }

    private suspend fun goToNotificationPermissionScreen() {
        val child: Coordinator = notificationPermissionCoordinatorFactory.create(Unit)
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

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleNotificationPermissionGranted(carrier: CarrierOfPermissionResult) {
        onOpenedSession()
    }

    private suspend fun onOpenedSession() {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        userModel.saveChangesIf(
            nickName = appModel.profile.client?.userName.orEmpty().toCharArray(),
            avatar = appModel.profile.client?.avatar.orEmpty().toCharArray(),
            isQrDeepLinkAvailable = appModel.profile.client?.personType?.isQrDeepLinkAvailable ?: false,
            isContactPayQrAvailable = false,
        )
        val medalliaFacade = MedalliaFacade(appModel)
        medalliaFacade.sendCustomParameters(additionalData = createMedalliaCustomParams())
        clearAnalyticsDefaultParameterGroup()
        goToSubFlow()
    }

    private fun clearAnalyticsDefaultParameterGroup() {
        sendAnalyticEvent(AnalyticEvent.CLEAR_DEFAULT_PARAMETER_GROUP)
    }

    private fun createMedalliaCustomParams(): HashMap<String, Any> = hashMapOf(
        MedalliaConstants.CUSTOM_PARAM_FLOW to MedalliaConstants.FLOW_LOGIN,
        MedalliaConstants.CUSTOM_PARAM_RESULT to true,
        MedalliaConstants.PLATFORM_TYPE to Constant.HYPHEN_STRING,
    )

    private suspend fun goToSubFlow() {
        val child: Coordinator? = launcher.factorySupplier.get().let(::createSubFlow)

        if (child == null) {
            //showPostLogin()
            goToDashboard()
            return
        }

        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        child.start()
    }

    private fun createSubFlow(factory: SubFlowFactory): Coordinator? = factory.create(
        hub = hub,
        parentScope = scope,
        weakParent = weakSelf,
    )

    private suspend fun showPostLogin() {
        removeChild(currentChild)
        val child: Coordinator = postLoadingFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private suspend fun goToDashboard() {
        sendScreenViewEvent()
        openDashboardCoordinator()
        /*
         val carrier: CarrierOfActivityDestination = destinationCarrierOf(
              screenDestination = dashboardDestination,
          ) {
              Constant.QR_DEEPLINK to isQrDeepLink
              addFlag(Intent.FLAG_ACTIVITY_NEW_TASK)
              addFlag(Intent.FLAG_ACTIVITY_CLEAR_TASK)
          }
          mutableLiveHolder.notifyMainLoadingVisibility(false)
          userInterface.receive(carrier)
          userInterface.receive(NavigationIntention.CLOSE)
         */
    }

    private suspend fun openDashboardCoordinator() {
        removeChild(currentChild)
        val child: Coordinator = dashboardFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun sendScreenViewEvent() {
        val data: Map<String, Any> = mapOf(
            AnalyticsConstant.PERSON_TYPE to appModel.profile.client?.personType?.analyticValue.orEmpty(),
            AnalyticsConstant.PLATFORM_TYPE to appModel.profile.client?.personType?.platformType.orEmpty(),
        )
        sendAnalyticEvent(AnalyticEvent.SCREEN, data)
    }

    private fun sendAnalyticEvent(event: AnalyticEvent, data: Map<String, Any?> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        loginAnalyticModel.sendEvent(eventData)
    }

    private suspend fun handleLauncher(launcher: SubFlowLauncher) {
        hideKeyboard()

        val child: Coordinator = launcher.factorySupplier.get().let(::createSubFlow)
            ?: return mutableLiveHolder.notifyMainLoadingVisibility(false)

        removeChild(currentChild)
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        child.start()

    }

    private fun filterInBackFromChild(
        finishingChild: FinishingCoordinator,
    ): Boolean = NavigationIntention.BACK == finishingChild.data

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleBackFromChild(finishingChild: FinishingCoordinator) {
        hideKeyboard()
        userInterface.receive(modalDataHolder)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun filterOnNewToken(event: FirebaseTokenEvent): Boolean = appModel.profile.isPushOtpEnabled

    private fun handleOnNewTokenEvent(event: FirebaseTokenEvent) = scope.launch  {
        tryToRefreshToken(event.token)
    }

    private suspend fun tryToRefreshToken(token: String) = try {
        refreshTokenModel.refreshToken(token)
    } catch (throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    private fun filterInClickOnLogOut(carrier: ModalEventCarrier): Boolean {
        val event: ModalEvent = carrier.event
        if (ModalEvent.PRIMARY_CLICKED != event) return false

        val dataHolder: ModalDataHolder = carrier.dataHolder
        return dataHolder.id == modalDataHolder.id
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnLogOut(carrier: ModalEventCarrier) = scope.launch {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        appModel.receive(SessionEvent.ENDING)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        weakAppContext.get()?.let(::clearThenNavigateToHost)
    }
}