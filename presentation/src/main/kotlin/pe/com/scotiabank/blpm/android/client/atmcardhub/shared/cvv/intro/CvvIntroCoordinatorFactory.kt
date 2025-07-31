package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.analytics.factories.atmcardhub.cvvonboarding.CvvOnboardingFactory
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import java.lang.ref.WeakReference

class CvvIntroCoordinatorFactory(
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(): CvvIntroCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()

        return CvvIntroCoordinator(
            factoryOfToolbarComposite = AppBarComposite.Factory(hub.dispatcherProvider),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(),
            factoryOfMainBottomComposite = BottomComposite.Factory(hub.dispatcherProvider),
            weakResources = hub.weakResources,
            idRegistry = idRegistry,
            availabilityRegistry = createAvailabilityRegistry(idRegistry),
            dataStore = DataStore(hub.appContext),
            analyticModel = createAnalyticModel(),
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfMainTopComposite() = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
    )

    private fun createAvailabilityRegistry(idRegistry: IdRegistry): AvailabilityRegistry {
        val ids: Collection<Long> = listOf(
            idRegistry.understoodButtonId,
        )
        return AvailabilityRegistry(ids)
    }

    private fun createAnalyticModel() = AnalyticModel(
        analyticsDataGateway = hub.analyticsDataGateway,
        analyticFactory = CvvOnboardingFactory(hub.systemDataFactory),
    )
}
