package pe.com.scotiabank.blpm.android.client.dashboard.home

import android.content.res.Resources
import androidx.core.util.Consumer
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.qrpayment.QRAnalyticsConstant
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.atmcardhub.flow.AtmCardFlowCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.base.session.entities.contactpay.ScotiaPayStatus
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.newdashboard.DashboardType
import pe.com.scotiabank.blpm.android.client.qrpayment.generalaccess.QRGeneralAccessActivity
import pe.com.scotiabank.blpm.android.client.scotiapay.shared.qr.ScotiaPayQrActivity
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.client.util.isFP
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu.UiEntityOfMenuItem
import pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard.UiEntityOfQuickActionCard
import java.lang.ref.WeakReference

class HomeCoordinator(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    private val weakResources: WeakReference<Resources?>,
    private val appModel: AppModel,
    private val hub: Hub,
    private val analyticModel: Consumer<AnalyticEventData<*>>,
    private val isQrDeepLink: Boolean,
    private val templateForQrDeepLink: OptionTemplate,
    private val templateForQrMenuItem: OptionTemplate,
    private val templateForContactPayQr: OptionTemplate,
    private val qrDeepLinkId: Long,
    weakParent: WeakReference<out Coordinator?>,
    scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    mutableLiveHolder: MutableLiveHolder,
    userInterface: InstanceReceiver,
    uiStateHolder: UiStateHolder,
    private val visitRegistry: VisitRegistry,
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
        .add(
            UiEntityOfMenuItem::class,
            InstancePredicate(::filterInClickOnQrMenuItem),
            InstanceHandler(::handleClickOnQrMenuItem)
        )
        .add(
            UiEntityOfMenuItem::class,
            InstancePredicate(::filterInClickOnSeeMyQrMenuItem),
            InstanceHandler(::handleClickOnSeeMyQrMenuItem)
        )
        .add(
            UiEntityOfQuickActionCard::class,
            InstancePredicate(::filterInHubCard),
            InstanceHandler(::handleClickOnHubCard),
        )
        .add(
            UiEntityOfQuickActionCard::class,
            InstancePredicate(::filterInAcquireProduct),
            InstanceHandler(::handleClickAcquireProduct),
        )
        .build()

    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(
        store = handlingStore,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = false,
            iconRes = pe.com.scotiabank.blpm.android.ui.R.drawable.ic_logo,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite
        .create(
            receiver = selfReceiver,
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = toolbarComposite,
        mainTopComposites = listOf(mainTopComposite),
    )

    private val atmCardHubFlowFactory: AtmCardFlowCoordinatorFactory by lazy {
        AtmCardFlowCoordinatorFactory(
            hub = hub,
            retrofit = hub.appModel.sessionRetrofit,
            parentScope = scope,
            weakParent = weakSelf
        )
    }

    init {
        addMenuItemIf()
    }

    private fun addMenuItemIf() {
        if(DashboardType.BUSINESS == appModel.dashboardType) {
            toolbarComposite.addMenuItem(
                idRes = R.id.contact_payment_qr,
                title = weakResources.get()?.getString(R.string.qr_camera).orEmpty(),
                data = templateForContactPayQr,
                iconRes = R.drawable.ic_qr_menu,
                isVisible = isContactPayQrVisible(),
            )

            return
        }

        toolbarComposite.addMenuItem(
            idRes = R.id.qr_camera_settings,
            title = weakResources.get()?.getString(R.string.qr_camera).orEmpty(),
            iconRes = R.drawable.ic_qr_menu,
            data = templateForQrMenuItem,
            isVisible = templateForQrMenuItem.isVisible,
        )
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        sendAnalyticEvent(HomeEvent.SCREEN)
        updateToolbar()
        attemptHandlingQrDeepLink()
        addProductGateCards()
        updateUiData()
    }

    private fun sendAnalyticEvent(event: HomeEvent, data: Map<String, Any?> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        analyticModel.accept(eventData)
    }

    private fun updateToolbar() = scope.launch {
        updateContactPayQRVisibility()
        toolbarComposite.recomposeItselfIfNeeded()
    }

    private fun updateContactPayQRVisibility() {
        toolbarComposite.editMenuItemVisibilityWith(
            idRes = R.id.contact_payment_qr,
            isVisible = isContactPayQrVisible(),
        )
    }

    private fun navigateToQRGeneralAccess(carrier: CarrierForQr) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = QRGeneralAccessActivity::class.java,
        ) {
            putStringBy(QRGeneralAccessActivity.PREVIOUS_SECTION, carrier.previousSection)
            putBooleanBy(QRGeneralAccessActivity.TO_SCAN, carrier.isGoingToScan)
        }
        userInterface.receive(carrier)
    }

    private fun attemptHandlingQrDeepLink() {
        if (isQrDeepLink.not()) return
        if (visitRegistry.isVisitAllowed(qrDeepLinkId).not()) return

        val isDisabled: Boolean = isDisabled(templateForQrDeepLink)
        if (isDisabled) return

        attemptGoingToQrMenuItem()
    }

    private fun isDisabled(template: OptionTemplate): Boolean {
        val isDisabled: Boolean = TemplatesUtil.isDisabled(template.type)
        if (isDisabled) {
            weakParent.get()?.receiveFromChild(template)
            return true
        }
        return false
    }

    private fun isContactPayQrVisible(): Boolean {
        val isQrVisible: Boolean = templateForContactPayQr.isVisible
        val isAffiliated: Boolean = appModel.scotiaPayStatus == ScotiaPayStatus.AFFILIATED

        return isQrVisible && isAffiliated
    }

    private fun attemptGoingToQrMenuItem() {
        val isDisabled: Boolean = isDisabled(templateForQrMenuItem)
        if (isDisabled) return

        goToQrMenuItem()
    }

    private fun goToQrMenuItem() {
        sendClickEvent(AnalyticLabel.QR_MENU.value)
        val carrier = CarrierForQr(
            previousSection = QRAnalyticsConstant.MY_ACCOUNTS,
            isGoingToScan = hub.appModel.profile.isFP(),
        )
        navigateToQRGeneralAccess(carrier)
    }

    private fun sendClickEvent(label: String) {
        val data: Map<String, Any?> = mapOf(AnalyticsBaseConstant.EVENT_LABEL to label)
        sendAnalyticEvent(HomeEvent.CLICK, data)
    }

    private fun filterInClickOnQrMenuItem(
        entity: UiEntityOfMenuItem,
    ): Boolean {
        val template: OptionTemplate = entity.data as? OptionTemplate ?: return false
        return templateForQrMenuItem.name.contentEquals(template.name)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnQrMenuItem(entity: UiEntityOfMenuItem) {
        val isDisabled: Boolean = isDisabled(templateForQrMenuItem)
        if (isDisabled) return

        goToQrMenuItem()
    }

    private fun filterInClickOnSeeMyQrMenuItem(entity: UiEntityOfMenuItem): Boolean {
        val template: OptionTemplate = entity.data as? OptionTemplate ?: return false
        return templateForContactPayQr.name.contentEquals(template.name)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnSeeMyQrMenuItem(entity: UiEntityOfMenuItem) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = ScotiaPayQrActivity::class.java,
        )
        userInterface?.receive(carrier)
    }

    private fun addProductGateCards() {
        mainTopComposite.composerOfProduct.add(GateProduct.HUB)
        mainTopComposite.composerOfProduct.add(GateProduct.ACQUIRE_PRODUCT)
    }

    private fun filterInAcquireProduct(entity: UiEntityOfQuickActionCard<*>): Boolean {
        val gateProduct = entity.data as GateProduct
        return gateProduct == GateProduct.ACQUIRE_PRODUCT
    }

    private fun filterInHubCard(entity: UiEntityOfQuickActionCard<*>): Boolean {
        val gateProduct = entity.data as GateProduct
        return gateProduct == GateProduct.HUB
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickAcquireProduct(entity: UiEntityOfQuickActionCard<*>) {

    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnHubCard(entity: UiEntityOfQuickActionCard<*>) = scope.launch {
        val child = atmCardHubFlowFactory.create(false)
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }
}
