package pe.com.scotiabank.blpm.android.client.host.session.postloading

import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import java.lang.ref.WeakReference

class PostLoadingCoordinator(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
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
            NavigationIntention::class,
            InstancePredicate(NavigationIntention::filterInBack),
            InstanceHandler(::handleBack)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = false,
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create()

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = toolbarComposite,
        mainTopComposites = listOf(mainTopComposite),
    )

    override suspend fun start() = withContext(scope.coroutineContext) {
        uiStateHolder.currentState = UiState.LOADING
        updateUiData()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleBack(intention: NavigationIntention) {
        // back is not required at this screen
    }
}
