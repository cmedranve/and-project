package pe.com.scotiabank.blpm.android.client.base

import android.os.Parcelable
import androidx.core.util.Supplier
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.newmedallia.MedalliaConfig
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import pe.com.scotiabank.blpm.android.ui.util.KeyboardIntention
import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException

abstract class CoordinatorImpl(
    protected val weakParent: WeakReference<out Coordinator?>,
    protected val scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    protected val mutableLiveHolder: MutableLiveHolder,
    protected val userInterface: InstanceReceiver,
    protected val uiStateHolder: UiStateHolder,
) : Coordinator, DispatcherProvider by dispatcherProvider, LiveHolder by mutableLiveHolder {

    private val closeableCoroutineScope: CloseableCoroutineScope = CloseableCoroutineScope(scope)

    open val selfReceiver: InstanceReceiver? = null
    open val selfSuspendingReceiver: SuspendingReceiverOfInstance? = null

    private val navigationItemRegistry: ItemRegistry = ItemRegistry()
    protected var selectedNavigationItemId: Long by navigationItemRegistry::selectedItemId

    private val childRegistry: ChildRegistry by lazy {
        ChildRegistry(
            parent = this,
            navigationItemRegistry = navigationItemRegistry,
        )
    }

    override val children: Collection<Coordinator>
        get() = childRegistry.children

    protected val childrenCount: Int
        get() = childRegistry.childrenCount

    override val currentChild: Coordinator
        get() = childRegistry.currentChild

    override val currentDeepChild: Coordinator
        get() = childRegistry.currentDeepChild

    override var recyclingState: Parcelable?
        get() = childRegistry.recyclingState
        set(value) {
            childRegistry.recyclingState = value
        }

    private val isDeepChild: Boolean
        get() = currentChild == this

    protected open val compositeRegistry: CompositeRegistry? = null

    protected open val medalliaConfig: MedalliaConfig? = null
    private val isMedalliaInterceptEnabled: Boolean
        get() = medalliaConfig?.isInterceptEnabled ?: false

    protected val visibilitySupplerForNavigation: Supplier<Boolean> by lazy {
        Supplier(childRegistry::isSingleChildDescendants)
    }

    private val rootChunkRegistry: RootChunkRegistry by lazy {
        RootChunkRegistry()
    }

    override fun receiveEvent(event: Any): Boolean {
        val isHandled: Boolean = selfReceiver?.receive(event) ?: false
        if (isHandled) return true
        handleUnhandledEvent(event)
        return false
    }

    private fun handleUnhandledEvent(event: Any) = scope.launch {
        val isBack: Boolean = NavigationIntention.BACK == event
        if (isBack) {
            handleUnhandledBackEvent()
            return@launch
        }
        weakParent.get()?.receiveFromChild(event)
    }

    private suspend fun handleUnhandledBackEvent() {
        if (childRegistry.isParentWithTwoOrMoreChildren) {

            removeChild(currentChild)
            mutableLiveHolder.notifyMainLoadingVisibility(false)
            mutableLiveHolder.notifyResultLoadingVisibility(false)
            mutableLiveHolder.anchoredBottom.postValue(emptyList())
            userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
            currentChild.updateUiData()
            currentChild.updateUiData()
            userInterface.receive(ObserverAction.REGISTER_AGAIN)

            return
        }

        val finishingCoordinator = FinishingCoordinator(
            data = NavigationIntention.BACK,
            coordinator = this,
        )
        weakParent.get()?.receiveFromChild(finishingCoordinator)
    }

    protected fun hideKeyboard() {
        userInterface.receive(KeyboardIntention.HIDE)
    }

    protected fun showKeyboard() {
        userInterface.receive(KeyboardIntention.SHOW)
    }

    override fun receiveFromChild(event: Any) {
        scope.launch {
            val isHandled: Boolean = selfSuspendingReceiver?.receive(event) ?: false
            if (isHandled) return@launch
            handleUnhandledSuspendingEvent(event)
        }
    }

    override fun receiveFromAncestor(event: Any) {
        scope.launch {
            selfSuspendingReceiver?.receive(event)
        }
    }

    private suspend fun handleUnhandledSuspendingEvent(event: Any) {
        val isBack: Boolean = event is FinishingCoordinator && NavigationIntention.BACK == event.data
        if (isBack) {
            handleUnhandledBackEvent()
            return
        }
        weakParent.get()?.receiveFromChild(event)
    }

    override suspend fun updateUiData() = withContext(scope.coroutineContext) {
        if (isDeepChild) {
            val registry: CompositeRegistry = compositeRegistry ?: return@withContext
            mutableLiveHolder.notifyAppBarAndMain(
                coordinatorId = id,
                toolbarCompounds = registry.recomposeToolbarCompounds(),
                swipeEntity = registry.swipeEntity,
                mainRecyclerEntity = registry.recomposeMainRecyclerEntity(),
                searchBarCompounds = registry.recomposeSearchBarCompounds(),
                resultRecyclerEntity = registry.recomposeResultRecyclerEntity(),
                isMedalliaInterceptEnabled = isMedalliaInterceptEnabled,
            )
            if (registry.isAnyAnchored) {
                mutableLiveHolder.anchoredBottom.postValue(registry.recomposeAnchoredBottomCompounds())
            }
            return@withContext
        }
        currentChild.updateUiData()
    }

    suspend fun showErrorMessage(throwable: Throwable) = withContext(mainDispatcher) {
        if (throwable is CancellationException) return@withContext
        val wrapper = ThrowableWrapper(throwable)
        uiStateHolder.currentState = UiState.SUCCESS
        updateUiData()
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        mutableLiveHolder.notifyResultLoadingVisibility(false)
        userInterface.receive(wrapper)
    }

    protected fun addChild(child: Coordinator) {
        childRegistry.addChild(child)
    }

    protected suspend fun removeChildren() {
        childRegistry.removeChildren()
    }

    protected suspend fun removeChild(child: Coordinator) {
        childRegistry.removeChild(child)
    }

    protected fun addNavigationItemFactory(id: Long, factory: Supplier<Coordinator>) {
        navigationItemRegistry.addItemFactory(id, factory)
    }

    protected fun setDefaultNavigationItem(id: Long) {
        navigationItemRegistry.defaultItemId = id
    }

    protected suspend fun hideNavigationItem(id: Long) {
        navigationItemRegistry.hideItem(id)
    }

    protected fun addChunk(chunk: Chunk) {
        rootChunkRegistry.addChunk(chunk)
    }

    protected suspend fun removeChunks() {
        rootChunkRegistry.removeChunks()
    }

    protected suspend fun removeChunk(chunk: Chunk) {
        rootChunkRegistry.removeChunk(chunk)
    }

    override suspend fun clearCoordinator() {
        val allChildrenAndSelf: List<Coordinator> = findAllChildrenAndSelf()
        val allDeferredClearing: List<Deferred<Unit>> = allChildrenAndSelf
            .map { child: Coordinator -> deferClearingChild(child) }
        allDeferredClearing.awaitAll()
        navigationItemRegistry.removeAllItems()
        rootChunkRegistry.removeChunks()
        mutableLiveHolder.removeNavigationCompound(id)
        tryClosingScope()
    }

    private suspend fun findAllChildrenAndSelf(): List<Coordinator> = withContext(defaultDispatcher) {
        val allChildrenAndSelf: MutableList<Coordinator> = mutableListOf()
        children
            .reversed()
            .flatMapTo(destination = allChildrenAndSelf, transform = ::toChildren)

        allChildrenAndSelf.addAll(navigationItemRegistry.nonSelectedItems)
        navigationItemRegistry.selectedItem?.let(allChildrenAndSelf::add)

        allChildrenAndSelf.add(element = this@CoordinatorImpl)
        allChildrenAndSelf
    }

    private fun toChildren(child: Coordinator): List<Coordinator> = child
        .children
        .reversed()
        .flatMap(transform = ::toChildren)

    private suspend fun deferClearingChild(
        child: Coordinator,
    ): Deferred<Unit> = withContext(mainDispatcher) {

        async { child.onCoordinatorCleared() }
    }

    private fun tryClosingScope() {
        try {
            closeableCoroutineScope.close()
        } catch (throwable: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }
}
