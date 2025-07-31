package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.disabled

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
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import java.lang.ref.WeakReference

class DisabledCoordinator(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    idRegistry: IdRegistry,
    weakParent: WeakReference<out Coordinator?>,
    scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    mutableLiveHolder: MutableLiveHolder,
    userInterface: InstanceReceiver,
    uiStateHolder: UiStateHolder,
    override val id: Long = randomLong(),
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
            UiEntityOfToolbar::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnToolbarIcon)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnCanvasButton)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplier = Supplier(isGoingToBeVisible()::not),
        )
        .setHome(
            isEnabled = false,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create(
        receiver = selfReceiver,
    )

    private val mainBottomComposite: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(uiStateHolder::isDisabledVisible),
        )
        .addCanvasButton(
            id = idRegistry.idOfGoToHomeButton,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.go_home).orEmpty(),
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = toolbarComposite,
        mainTopComposites = listOf(mainTopComposite),
        mainBottomComposites = listOf(mainBottomComposite),
    )

    override suspend fun start() = withContext(scope.coroutineContext) {
        uiStateHolder.currentState = UiState.DISABLED
        updateUiData()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) {
        receiveEvent(NavigationIntention.BACK)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnCanvasButton(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        receiveEvent(NavigationIntention.BACK)
    }
}
