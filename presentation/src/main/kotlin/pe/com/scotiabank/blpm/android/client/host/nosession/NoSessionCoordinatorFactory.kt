package pe.com.scotiabank.blpm.android.client.host.nosession

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SingleUseLauncher
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class NoSessionCoordinatorFactory(
    private val hub: Hub,
    private val peruRetrofit: Retrofit,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(singleUseLauncher: SingleUseLauncher): NoSessionCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()

        return NoSessionCoordinator(
            hub = hub,
            singleUseLauncher = singleUseLauncher,
            userDao = hub.userDao,
            peruRetrofit = peruRetrofit,
            uriHolder = UriHolder(hub.weakResources),
            weakResources = hub.weakResources,
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            uiStateHolder = uiStateHolder,
        )
    }
}