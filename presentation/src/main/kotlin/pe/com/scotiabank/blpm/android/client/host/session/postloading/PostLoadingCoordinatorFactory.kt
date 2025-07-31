package pe.com.scotiabank.blpm.android.client.host.session.postloading

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import java.lang.ref.WeakReference

class PostLoadingCoordinatorFactory(
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(): PostLoadingCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()
        val idRegistry = IdRegistry()

        return PostLoadingCoordinator(
            factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = createFactoryOfMainTopComposite(uiStateHolder, idRegistry),
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }

    private fun createFactoryOfAppBarComposite() = AppBarComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
    )

    private fun createFactoryOfMainTopComposite(
        uiStateHolder: UiStateHolder,
        idRegistry: IdRegistry,
    ) = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        uiStateHolder = uiStateHolder,
        idRegistry = idRegistry,
    )
}