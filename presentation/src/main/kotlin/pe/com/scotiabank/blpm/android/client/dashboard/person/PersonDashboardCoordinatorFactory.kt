package pe.com.scotiabank.blpm.android.client.dashboard.person

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.navigation.NavigationComposite
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import java.lang.ref.WeakReference

class PersonDashboardCoordinatorFactory(
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
    private val isQrDeepLink: Boolean,
) {

    fun create(): PersonDashboardCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()

        return PersonDashboardCoordinator(
            factoryOfNavigationComposite = createFactoryForNavigationComposite(idRegistry),
            hub = hub,
            idRegistry = idRegistry,
            visitRegistry = createVisitRegistry(idRegistry),
            weakResources = hub.weakResources,
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            isQrDeepLink = isQrDeepLink,
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
            id = idRegistry.coordinatorIdOfDashboard
        )
    }

    private fun createFactoryForNavigationComposite(
        idRegistry: IdRegistry,
    ): NavigationComposite.Factory = NavigationComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        coordinatorId = idRegistry.coordinatorIdOfDashboard,
    )

    private fun createVisitRegistry(idRegistry: IdRegistry): VisitRegistry {
        val maxNumberAllowedById: Map<Long, Int> = mapOf(
            idRegistry.coordinatorIdOfHome to 1,
            idRegistry.coordinatorIdOfMyList to 1,
            idRegistry.coordinatorIdOfP2p to 1,
            idRegistry.coordinatorIdOfNews to 1,
            idRegistry.coordinatorIdOfManagementCenter to 1,
        )
        return VisitRegistry(maxNumberAllowedById)
    }
}