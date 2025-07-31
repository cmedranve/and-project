package pe.com.scotiabank.blpm.android.client.atmcardhub.flow

import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.EnabledCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.disabled.DisabledCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.calltoaction.CallToAction
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.mapview.MapActivity
import pe.com.scotiabank.blpm.android.client.mapview.MapMode
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class BusinessFlowCoordinator(
    private val hub: Hub,
    private val titleText: String,
    private val retrofit: Retrofit,
    private val cardHubTemplate: OptionTemplate,
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
            CallToAction::class,
            InstancePredicate(::filterInCallToActionOnLookForBranch),
            SuspendingHandlerOfInstance(::handleCallToActionOnLookForBranch)
        )
        .add(
            CallToAction::class,
            InstancePredicate(::filterInCallToActionOnGoToHome),
            SuspendingHandlerOfInstance(::handleCallToActionOnGoToHome)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val disabledFactory: DisabledCoordinatorFactory by lazy {
        DisabledCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val enabledFactory: EnabledCoordinatorFactory by lazy {
        EnabledCoordinatorFactory(
            hub = hub,
            titleText = titleText,
            retrofit = retrofit,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        if (cardHubTemplate.isVisible) {
            goToEnabledCoordinator()
            return@withContext
        }
        goToDisabledCoordinator()
    }

    private suspend fun goToEnabledCoordinator() {
        val child: Coordinator = enabledFactory.create()
        addChild(child)
        child.start()
    }

    private suspend fun goToDisabledCoordinator() {
        val child: Coordinator = disabledFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
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

    @Suppress("SameParameterValue")
    private fun goToMap(mapMode: MapMode) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = MapActivity::class.java
        ) {
            MapActivity.PARAM_MAP_MODE to mapMode.value
        }
        userInterface.receive(carrier)
    }
}
