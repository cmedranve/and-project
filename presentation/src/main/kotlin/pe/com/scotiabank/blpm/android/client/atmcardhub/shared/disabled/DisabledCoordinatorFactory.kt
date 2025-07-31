package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.disabled

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import java.lang.ref.WeakReference

class DisabledCoordinatorFactory(
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(): DisabledCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()

        val idRegistry = IdRegistry()

        return DisabledCoordinator(
            factoryOfToolbarComposite = AppBarComposite.Factory(hub.dispatcherProvider),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(uiStateHolder),
            factoryOfMainBottomComposite = BottomComposite.Factory(hub.dispatcherProvider),
            weakResources = hub.weakResources,
            idRegistry = idRegistry,
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfMainTopComposite(
        uiStateHolder: UiStateHolder
    ) = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        weakResources = hub.weakResources,
        uiStateHolder = uiStateHolder,
    )
}
