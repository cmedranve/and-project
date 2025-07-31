package pe.com.scotiabank.blpm.android.client.cardsettings

import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.ChildRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.cardsettings.flow.CardSettingHubFlowCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.shared.HolderOfInstanceFlow
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.host.shared.UserInterface
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfAppBarAndMain
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import java.lang.ref.WeakReference

class CardSettingHubHostViewModel(
    dispatcherProvider: DispatcherProvider,
    hubFactory: Hub.Factory,
    environmentHolder: EnvironmentHolder,
    private val visitRegistry: VisitRegistry,
    private val appModel: AppModel,
    override val id: Long = randomLong(),
) : NewBaseViewModel(),
    DispatcherProvider by dispatcherProvider,
    PortableViewModel,
    Coordinator,
    HolderOfInstanceFlow
{

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInBackFromChild),
            SuspendingHandlerOfInstance(::handleBackFromChild)
        )
        .build()
    private val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val childRegistry: ChildRegistry by lazy {
        ChildRegistry(parent = this)
    }

    override val children: Collection<Coordinator>
        get() = childRegistry.children

    override val currentChild: Coordinator
        get() = childRegistry.currentChild

    override val currentDeepChild: Coordinator
        get() = childRegistry.currentDeepChild

    override val liveMainLoading: LiveData<Int>
        get() = currentDeepChild.liveMainLoading

    override val liveResultLoading: LiveData<Int>
        get() = currentDeepChild.liveResultLoading

    override val liveCompositeOfAppBarAndMain: LiveData<CompositeOfAppBarAndMain>
        get() = currentDeepChild.liveCompositeOfAppBarAndMain

    override val liveAnchoredBottomCompounds: LiveData<List<UiCompound<*>>>
        get() = currentDeepChild.liveAnchoredBottomCompounds

    override val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>>
        get() = currentDeepChild.liveCompoundsOfSheetDialog

    override val windowSecureFlagFlow: StateFlow<Boolean>
        get() = currentDeepChild.windowSecureFlagFlow

    override val medalliaInterceptFlagFlow: StateFlow<Boolean>
        get() = currentDeepChild.medalliaInterceptFlagFlow

    override var recyclingState: Parcelable?
        get() = currentChild.recyclingState
        set(value) {
            currentChild.recyclingState = value
        }

    private val mutableLiveHolder: MutableLiveHolder = MutableLiveHolder(
        childRegistry = childRegistry,
        environmentHolder = environmentHolder,
    )

    private val userInterface: UserInterface = UserInterface(viewModelScope)
    override val instanceFlow: MutableSharedFlow<Any> by userInterface::instanceFlow

    private val hub: Hub = hubFactory.create(mutableLiveHolder, userInterface)

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val cardSettingHubFlowFactory: CardSettingHubFlowCoordinatorFactory by lazy {
        CardSettingHubFlowCoordinatorFactory(
            hub = hub,
            retrofit = appModel.sessionRetrofit,
            parentScope = viewModelScope,
            weakParent = weakSelf,
        )
    }

    init {
        appModel.addChild(this)
    }

    override fun receiveFromChild(event: Any) {
        viewModelScope.launch {
            selfSuspendingReceiver.receive(event)
        }
    }

    override fun setUpUi(weakActivity: WeakReference<FragmentActivity?>, intent: Intent) {
        userInterface.weakActivity = weakActivity
        onScreenCreated()
    }

    private fun onScreenCreated() = viewModelScope.launch {
        if (visitRegistry.isVisitAllowed(HOST_ID_FOR_VISIT_REGISTRY).not()) {
            userInterface.receive(ObserverAction.REGISTER_AGAIN)
            return@launch
        }

        val child: Coordinator = cardSettingHubFlowFactory.create()
        childRegistry.addChild(child)
        child.start()
    }

    override suspend fun updateUiData() {
        currentChild.updateUiData()
    }

    private fun filterInBackFromChild(
        finishingChild: FinishingCoordinator,
    ): Boolean = NavigationIntention.BACK == finishingChild.data

    private suspend fun handleBackFromChild(finishingChild: FinishingCoordinator) {
        val oldChild: Coordinator = finishingChild.coordinator
        removeChild(oldChild)
        notifyUi(NavigationIntention.CLOSE)
    }

    private suspend fun notifyUi(event: Any) = withContext(mainDispatcher) {
        setLoadingV2(false)
        userInterface.receive(event)
    }

    private suspend fun removeChild(child: Coordinator) {
        childRegistry.removeChild(child)
    }

    override fun receiveEvent(event: Any): Boolean = currentDeepChild.receiveEvent(event)

    override fun onCleared() {
        appModel.removeChild(id)
        super.onCleared()
    }

    companion object {

        val HOST_ID_FOR_VISIT_REGISTRY: Long = randomLong()
    }
}
