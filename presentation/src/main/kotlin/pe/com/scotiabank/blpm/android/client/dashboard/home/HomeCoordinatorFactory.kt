package pe.com.scotiabank.blpm.android.client.dashboard.home

import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.analytics.EmptyAnalyticConsumer
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.base.state.DelegateUiStateHolder
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.products.dashboard.findFocusQrCodeTemplate
import pe.com.scotiabank.blpm.android.client.products.dashboard.findHomeTemplate
import pe.com.scotiabank.blpm.android.client.products.dashboard.findTemplateForQrDeepLink
import pe.com.scotiabank.blpm.android.client.products.dashboard.findTemplateForQrMenuItem
import pe.com.scotiabank.blpm.android.client.scotiapay.shared.findTemplateForContactPayQr
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.coroutine.newChildScope
import java.lang.ref.WeakReference

class HomeCoordinatorFactory(
    private val coordinatorId: Long,
    private val hub: Hub,
    private val parentScope: CoroutineScope,
    private val weakParent: WeakReference<out Coordinator?>,
    private val isQrDeepLink: Boolean,
) {

    private val qrDeepLinkId: Long by lazy {
        randomLong()
    }

    fun create(): HomeCoordinator {

        val uiStateHolder: UiStateHolder = DelegateUiStateHolder()

        val focusQrCodeTemplate: FeatureTemplate = findFocusQrCodeTemplate(hub.appModel.navigationTemplate)
        val templateForQrDeepLink: OptionTemplate = findTemplateForQrDeepLink(focusQrCodeTemplate)

        val homeTemplate: FeatureTemplate = findHomeTemplate(hub.appModel.navigationTemplate)
        val templateForQrMenuItem: OptionTemplate = findTemplateForQrMenuItem(homeTemplate)

        val templateForContactPayQr: OptionTemplate = findTemplateForContactPayQr(hub.appModel.navigationTemplate)

        return HomeCoordinator(
            hub = hub,
            factoryOfToolbarComposite = createFactoryOfAppBarComposite(),
            factoryOfMainTopComposite = createMainTopComposite(uiStateHolder),
            appModel = hub.appModel,
            weakResources = hub.weakResources,
            templateForQrDeepLink = templateForQrDeepLink,
            templateForQrMenuItem = templateForQrMenuItem,
            templateForContactPayQr = templateForContactPayQr,
            isQrDeepLink = isQrDeepLink,
            qrDeepLinkId = qrDeepLinkId,
            analyticModel = EmptyAnalyticConsumer,
            weakParent = weakParent,
            scope = parentScope.newChildScope(),
            dispatcherProvider = hub.dispatcherProvider,
            mutableLiveHolder = hub.mutableLiveHolder,
            userInterface = hub.userInterface,
            visitRegistry = createVisitRegistry(),
            uiStateHolder = uiStateHolder,
            id = coordinatorId,
        )
    }

    private fun createFactoryOfAppBarComposite() = AppBarComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
    )

    private fun createMainTopComposite(
        uiStateHolder: UiStateHolder,
    ) = MainTopComposite.Factory(
        dispatcherProvider = hub.dispatcherProvider,
        uiStateHolder = uiStateHolder,
        appModel = hub.appModel,
        weakResources = hub.weakResources,
    )

    private fun createVisitRegistry(): VisitRegistry {
        val maxNumberAllowedById: Map<Long, Int> = mapOf(
            qrDeepLinkId to 1,
        )
        return VisitRegistry(maxNumberAllowedById)
    }
}