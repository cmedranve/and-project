package pe.com.scotiabank.blpm.android.client.cardsettings.flow

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class CardSettingHubFlowCoordinatorFactory(
    private val hub: Hub,
    private val retrofit: Retrofit,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    fun create(): CardSettingHubFlowCoordinator = CardSettingHubFlowCoordinator(
        hub = hub,
        appModel = hub.appModel,
        retrofit = retrofit,
        uriHolder = UriHolder(hub.weakResources),
        textProvider = TextProvider(hub.weakResources),
        weakAppContext = hub.weakAppContext,
        weakParent = weakParent,
        scope = parentScope.newChildScope(),
        dispatcherProvider = hub.dispatcherProvider,
        mutableLiveHolder = hub.mutableLiveHolder,
        userInterface = hub.userInterface,
        uiStateHolder = DelegateUiStateHolder(),
    )
}
