package pe.com.scotiabank.blpm.android.client.dashboard.business

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.navigation.NavigationComposite
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.dashboard.home.HomeCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import pe.com.scotiabank.blpm.android.ui.list.items.navigation.UiEntityOfNavigationItem
import java.lang.ref.WeakReference

class BusinessDashboardCoordinator(
    factoryOfNavigationComposite: NavigationComposite.Factory,
    private val weakResources: WeakReference<Resources?>,
    private val idRegistry: IdRegistry,
    private val visitRegistry: VisitRegistry,
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
        .add(
            UiEntityOfNavigationItem::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnNavigationItem)
        )
        .build()

    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val navigationComposite: NavigationComposite = factoryOfNavigationComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplier = Supplier(::isGoingToBeVisible)
        )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val homeFactory: HomeCoordinatorFactory by lazy {
        HomeCoordinatorFactory(
            coordinatorId = idRegistry.coordinatorIdOfHome,
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
            isQrDeepLink = isQrDeepLink,
        )
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        setUpBottomNavigationBar()
        navigationComposite.setSelectedItem(idRegistry.coordinatorIdOfHome)
        setDefaultNavigationItem(idRegistry.coordinatorIdOfHome)
        selectedNavigationItemId = idRegistry.coordinatorIdOfHome
        visitRegistry.isVisitAllowed(idRegistry.coordinatorIdOfHome)
        updateNavigationBottom()
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        currentChild.start()
        updateUiData()
    }

    private suspend fun updateNavigationBottom() {
        navigationComposite.recomposeItselfIfNeeded()
        navigationComposite.compounds.firstOrNull()?.let(mutableLiveHolder::addNavigationCompound)
    }

    private fun setUpBottomNavigationBar() {

        val featureTemplate = TemplatesUtil.getFeature(
            navigation = hub.appModel.navigationTemplate,
            name = TemplatesUtil.NAVIGATION_KEY,
        )

        BusinessNavigationItem.entries.map {
                navigationItem: BusinessNavigationItem ->
            addMenuItemByTemplate(
                featureTemplate = featureTemplate,
                item = navigationItem,
            )
        }
    }

    private fun addMenuItemByTemplate(featureTemplate: FeatureTemplate, item: BusinessNavigationItem) {

        val isVisible: Boolean = TemplatesUtil.getOperation(featureTemplate, item.templateKey).isVisible
        val idCoordinator: Long = getCoordinatorIdByItem(item)

        if (isVisible.not()) return

        navigationComposite.addItem(
            id = idCoordinator,
            idRes = item.idRes,
            iconRes = item.iconRes,
            title = weakResources.get()?.getString(item.title).orEmpty(),
            data = item
        )

        addNavigationItemFactory(
            id = idCoordinator,
            factory = getCoordinatorByIdItem(item)
        )
    }

    private fun getCoordinatorByIdItem(
        item: BusinessNavigationItem,
    ): Supplier<Coordinator> = when(item) {
        BusinessNavigationItem.CONTACT -> Supplier(homeFactory::create)
        BusinessNavigationItem.MY_ACCOUNT -> Supplier(homeFactory::create)
        else -> Supplier(homeFactory::create)
    }

    private fun getCoordinatorIdByItem(
        item: BusinessNavigationItem,
    ): Long = when(item) {
        BusinessNavigationItem.CONTACT -> idRegistry.coordinatorIdOfHome
        BusinessNavigationItem.MY_ACCOUNT -> idRegistry.coordinatorIdOfHome
        else -> idRegistry.coordinatorIdOfHome
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnNavigationItem(entity: UiEntityOfNavigationItem) = scope.launch {
        selectedNavigationItemId = entity.id

        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)

        val isPendingToStart: Boolean = visitRegistry.isVisitAllowed(entity.id)
        if (isPendingToStart.not()) return@launch

        currentChild.start()
    }
}