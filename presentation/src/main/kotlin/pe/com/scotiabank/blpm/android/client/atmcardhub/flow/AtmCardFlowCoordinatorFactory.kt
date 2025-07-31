package pe.com.scotiabank.blpm.android.client.atmcardhub.flow

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class AtmCardFlowCoordinatorFactory(
    private val hub: Hub,
    private val retrofit: Retrofit,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
) {

    private val appModel: AppModel
        get() = hub.appModel

    private val currentDashboardType: DashboardType
        get() = appModel.dashboardType

    fun create(isDeepLink: Boolean): Coordinator {

        if (DashboardType.BUSINESS == currentDashboardType) {
            return createBusinessFlowCoordinator()
        }

        return createPersonalFlowCoordinator(isDeepLink)
    }

    private fun createPersonalFlowCoordinator(isDeepLink: Boolean) = PersonalFlowCoordinator(
        hub = hub,
        titleText = hub.weakResources.get()?.getString(R.string.my_cards).orEmpty(),
        retrofit = retrofit,
        cardHubTemplate = OptionTemplate(name = TemplatesUtil.CARDS_HUB_KEY, isVisible = true),
        isDeepLink = isDeepLink,
        weakParent = weakParent,
        scope = parentScope.newChildScope(),
        dispatcherProvider = hub.dispatcherProvider,
        mutableLiveHolder = hub.mutableLiveHolder,
        userInterface = hub.userInterface,
        uiStateHolder = DelegateUiStateHolder(),
    )

    private fun createBusinessFlowCoordinator() = BusinessFlowCoordinator(
        hub = hub,
        titleText = hub.weakResources.get()?.getString(R.string.my_cards).orEmpty(),
        retrofit = retrofit,
        cardHubTemplate = findTemplateForCardHub(appModel.navigationTemplate),
        weakParent = weakParent,
        scope = parentScope.newChildScope(),
        dispatcherProvider = hub.dispatcherProvider,
        mutableLiveHolder = hub.mutableLiveHolder,
        userInterface = hub.userInterface,
        uiStateHolder = DelegateUiStateHolder(),
    )
}
