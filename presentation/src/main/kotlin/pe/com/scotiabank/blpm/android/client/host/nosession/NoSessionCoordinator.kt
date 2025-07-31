package pe.com.scotiabank.blpm.android.client.host.nosession

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import com.scotiabank.enhancements.handling.HandlingStore
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
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActionDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.host.user.User
import pe.com.scotiabank.blpm.android.client.host.user.UserDao
import pe.com.scotiabank.blpm.android.client.mapview.MapActivity
import pe.com.scotiabank.blpm.android.client.mapview.MapMode
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.DoiCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.nosession.login.LoginCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.login.factor.SuccessfulAuth
import pe.com.scotiabank.blpm.android.client.nosession.login.factor.first.FirstFactorEvent
import pe.com.scotiabank.blpm.android.client.nosession.login.passrecovery.PassRecoveryEvent
import pe.com.scotiabank.blpm.android.client.nosession.onboarding.OnboardingCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.onboarding.OnboardingEvent
import pe.com.scotiabank.blpm.android.client.nosession.shared.actiongroupcomposite.Action
import pe.com.scotiabank.blpm.android.client.nosession.shared.atmcardscreen.Document
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfCustomTabsIntentDestination
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SingleUseLauncher
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SuccessfulAuthToLaunch
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.ModalData
import pe.com.scotiabank.blpm.android.client.nosession.gotoagency.GoToAgencyCoordinatorFactory
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class NoSessionCoordinator(
    private val hub: Hub,
    private val singleUseLauncher: SingleUseLauncher,
    private val userDao: UserDao,
    private val peruRetrofit: Retrofit,
    private val uriHolder: UriHolder,
    private val weakResources: WeakReference<Resources?>,
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
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInEventToChangeUser),
            SuspendingHandlerOfInstance(::handleEventToChangeUser)
        )
        .add(
            Action::class,
            InstancePredicate(::filterInCallUsClicked),
            SuspendingHandlerOfInstance(::handleClickOnCallUs)
        )
        .add(
            Action::class,
            InstancePredicate(::filterInLocateUsClicked),
            SuspendingHandlerOfInstance(::handleClickOnLocateUs)
        )
        .add(
            Document::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleFromDoiToOnboarding)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOnboardingSuccess),
            SuspendingHandlerOfInstance(::handleOnboardingSuccess)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterOnErrorRedirect),
            SuspendingHandlerOfInstance(::handleOnErrorRedirect)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInPassRecoverySuccess),
            SuspendingHandlerOfInstance(::handlePassRecoverySuccess)
        )
        .add(
            User::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleFromDoiToLogin)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOpeningSession),
            SuspendingHandlerOfInstance(::handleOpeningSession)
        )
        .add(
            ModalData::class,
            InstancePredicate(::filterInMissingDigitalKey),
            SuspendingHandlerOfInstance(::handleMissingDigitalKey)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInEventToCloseEnrollment),
            SuspendingHandlerOfInstance(::handleEventToCloseEnrollment)
        )
        .build()

    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val doiCoordinatorFactory: DoiCoordinatorFactory by lazy {
        DoiCoordinatorFactory(hub, peruRetrofit, scope, weakSelf)
    }

    private val onboardingCoordinatorFactory: OnboardingCoordinatorFactory by lazy {
        OnboardingCoordinatorFactory(hub, peruRetrofit, scope, weakSelf)
    }

    private val loginCoordinatorFactory: LoginCoordinatorFactory by lazy {
        LoginCoordinatorFactory(hub, peruRetrofit, scope, weakSelf)
    }

    private val goToAgencyCoordinatorFactory: GoToAgencyCoordinatorFactory by lazy {
        GoToAgencyCoordinatorFactory(hub, scope, weakSelf)
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        launch { singleUseLauncher.countDownLifetime() }
        launch { goToDoiOrLogin() }
        Unit
    }

    private suspend fun goToDoiOrLogin() {
        if (userDao.isEmpty) {
            goToDoiScreen(NavigationArrangement.ADD_POP_UP)
            return
        }
        goToLoginFlow(userDao.user, true)
    }

    private suspend fun goToDoiScreen(arrangement: NavigationArrangement) {
        val child: Coordinator = doiCoordinatorFactory.create()
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(arrangement)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInEventToChangeUser(finishingChild: FinishingCoordinator): Boolean {
        val event: FirstFactorEvent = finishingChild.data as? FirstFactorEvent ?: return false
        return FirstFactorEvent.ON_CHANGE_USER_CLICKED == event
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleEventToChangeUser(finishingChild: FinishingCoordinator) {
        removeChildren()
        goToDoiScreen(NavigationArrangement.ADD_SCREEN)
    }

    private fun filterInCallUsClicked(action: Action): Boolean = Action.CALL.id == action.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnCallUs(action: Action) {
        val uri: Uri = uriHolder.callUri
        val carrier = CarrierOfActionDestination(uriDestination = uri, action = Intent.ACTION_DIAL)
        userInterface.receive(carrier)
    }

    private fun filterInLocateUsClicked(action: Action): Boolean = Action.LOCATION.id == action.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnLocateUs(action: Action) {
        val url: String = weakResources.get()?.getString(R.string.locate_us_url).orEmpty()
        val uri: Uri = Uri.parse(url)

        val carrier = CarrierOfCustomTabsIntentDestination(uri)
        userInterface.receive(carrier)
    }

    private suspend fun handleFromDoiToOnboarding(document: Document) {
        val newChild: Coordinator = onboardingCoordinatorFactory.create(document)
        addChild(newChild)
        newChild.start()
    }

    private fun filterInOnboardingSuccess(
        finishingChild: FinishingCoordinator,
    ): Boolean = OnboardingEvent.ON_PASS_REGISTERED == finishingChild.data

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleOnboardingSuccess(finishingChild: FinishingCoordinator) {
        removeChildren()
        goToDoiScreen(NavigationArrangement.ADD_SCREEN)
    }

    private fun filterOnErrorRedirect(
        finishingChild: FinishingCoordinator,
    ): Boolean = OnboardingEvent.ON_ERROR_REDIRECT == finishingChild.data

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleOnErrorRedirect(finishingChild: FinishingCoordinator) {
        removeChildren()
        goToDoiOrLogin()
    }

    private suspend fun handleFromDoiToLogin(user: User) {
        goToLoginFlow(user, false)
    }

    private suspend fun goToLoginFlow(user: User, isUserFromStore: Boolean) {
        val child: Coordinator = loginCoordinatorFactory.create(user, isUserFromStore)
        addChild(child)
        child.start()
    }

    private fun filterInPassRecoverySuccess(
        finishingChild: FinishingCoordinator,
    ): Boolean = PassRecoveryEvent.ON_PASS_REGISTERED == finishingChild.data

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handlePassRecoverySuccess(finishingChild: FinishingCoordinator) {
        removeChildren()
        goToDoiScreen(NavigationArrangement.ADD_SCREEN)
    }

    private fun filterInOpeningSession(
        finishingChild: FinishingCoordinator,
    ): Boolean = finishingChild.data is SuccessfulAuth

    private suspend fun handleOpeningSession(finishingChild: FinishingCoordinator) {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        val successfulAuth: SuccessfulAuth = finishingChild.data as? SuccessfulAuth ?: return

        val data = SuccessfulAuthToLaunch(successfulAuth, singleUseLauncher.launcher)
        val finishingCoordinator = FinishingCoordinator(data, this)
        weakParent.get()?.receiveFromChild(finishingCoordinator)
    }

    private fun filterInMissingDigitalKey(modalData: ModalData): Boolean = modalData == ModalData.MISSING_DIGITAL_KEY_BINDING

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleMissingDigitalKey(modalData: ModalData) {
        val child: Coordinator = goToAgencyCoordinatorFactory.create()
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInEventToCloseEnrollment(finishingChild: FinishingCoordinator): Boolean = finishingChild.data == NavigationIntention.CLOSE

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleEventToCloseEnrollment(finishingChild: FinishingCoordinator) {
        removeChildren()
        goToDoiOrLogin()
    }
}