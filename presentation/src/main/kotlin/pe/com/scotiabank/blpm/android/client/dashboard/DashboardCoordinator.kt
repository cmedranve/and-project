package pe.com.scotiabank.blpm.android.client.dashboard

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.dashboard.business.BusinessDashboardCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.dashboard.person.PersonDashboardCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import java.lang.ref.WeakReference

class DashboardCoordinator(
    private val hub: Hub,
    private val isQrDeepLink: Boolean,
    weakParent: WeakReference<out Coordinator?>,
    scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    mutableLiveHolder: MutableLiveHolder,
    userInterface: InstanceReceiver,
    uiStateHolder: UiStateHolder,
    override val id: Long = randomLong()
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

    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(
        store = handlingStore,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val businessDashboardFactory: BusinessDashboardCoordinatorFactory by lazy {
        BusinessDashboardCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
            isQrDeepLink = isQrDeepLink,
        )
    }

    private val personDashboard: PersonDashboardCoordinatorFactory by lazy {
        PersonDashboardCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
            isQrDeepLink = isQrDeepLink,
        )
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        dashboardDestination()
    }

    private suspend fun dashboardDestination() {
        if (DashboardType.BUSINESS === hub.appModel.dashboardType) {
            goToBusinessDashboard()
            return
        }

        goToPersonDashboard()
    }

    private suspend fun goToBusinessDashboard() {
        val child: Coordinator = businessDashboardFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private suspend fun goToPersonDashboard() {
        val child: Coordinator = personDashboard.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }
}