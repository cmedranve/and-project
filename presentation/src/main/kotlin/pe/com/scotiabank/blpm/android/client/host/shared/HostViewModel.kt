package pe.com.scotiabank.blpm.android.client.host.shared

import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyTextType
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
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
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfBooleanCreation
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfStringCreation
import pe.com.scotiabank.blpm.android.client.base.carrier.feedFrom
import pe.com.scotiabank.blpm.android.client.base.checksecurity.EmptyNonBlockingEntity
import pe.com.scotiabank.blpm.android.client.base.checksecurity.SealedNonBlockingEntity
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.network.EnvironmentHolder
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.host.FeatureHubShortcut
import pe.com.scotiabank.blpm.android.client.host.HostCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.host.session.subflow.SubFlowLauncher
import pe.com.scotiabank.blpm.android.client.messaging.notification.PushOtpHandler
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.CarrierOfPushData
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfAppBarAndMain
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import java.lang.ref.WeakReference

class HostViewModel(
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

    private val hostCoordinatorFactory: HostCoordinatorFactory by lazy {
        HostCoordinatorFactory(hub, viewModelScope, weakSelf)
    }

    override fun receiveFromChild(event: Any) {
        viewModelScope.launch {
            selfSuspendingReceiver.receive(event)
        }
    }

    override fun setUpUi(weakActivity: WeakReference<FragmentActivity?>, intent: Intent) {
        userInterface.weakActivity = weakActivity
        onScreenCreated(intent)
    }

    private fun onScreenCreated(intent: Intent) = viewModelScope.launch {
        if (visitRegistry.isVisitAllowed(HOST_ID_FOR_VISIT_REGISTRY).not()) {
            userInterface.receive(ObserverAction.REGISTER_AGAIN)
            return@launch
        }
        val deferredRootDetection: Deferred<Unit> = async { checkRooting() }
        val deferredRoutingToHost: Deferred<Unit> = async { handleRoutingToHost(intent) }
        deferredRootDetection.await()
        deferredRoutingToHost.await()
    }

    private fun checkRooting() {
        val nonBlockingEntity: SealedNonBlockingEntity = appModel.checkRooting()
        if (nonBlockingEntity is EmptyNonBlockingEntity) return

        val attributes = AttrsBodyTextType(
            headline = nonBlockingEntity.title,
            bodyText = nonBlockingEntity.description,
            secondaryButtonLabel = nonBlockingEntity.buttonText,
        )
        userInterface.receive(attributes)
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

    private suspend fun handleRoutingToHost(intent: Intent) {
        val creationExtras = MutableCreationExtras()
        creationExtras.feedFrom(intent)

        val carrierOfPushData: CarrierOfPushData = extractMaybeOtp(intent)

        val shortcut: FeatureHubShortcut = createFeatureHubShortcut(creationExtras)
        val launcher: SubFlowLauncher = identifySubFlowLauncher(creationExtras)

        val child: Coordinator = hostCoordinatorFactory.create(
            featureHubShortcut = shortcut,
            launcher = launcher,
            carrier = carrierOfPushData,
        )
        childRegistry.addChild(child)
        child.start()
    }

    private fun createFeatureHubShortcut(creationExtras: CreationExtras): FeatureHubShortcut {
        val holder = HolderOfBooleanCreation(creationExtras)
        val isDisplayable: Boolean = holder.findBy(FeatureHubShortcut.DISPLAYABLE_FLAG)
        return FeatureHubShortcut(isDisplayable)
    }

    private fun identifySubFlowLauncher(creationExtras: CreationExtras): SubFlowLauncher {
        val holder = HolderOfStringCreation(creationExtras)
        return SubFlowLauncher.createFrom(holder)
    }

    private suspend fun removeChild(child: Coordinator) {
        childRegistry.removeChild(child)
    }

    override fun receiveEvent(event: Any): Boolean = currentDeepChild.receiveEvent(event)

    override fun receiveNewIntent(intent: Intent) {
        val creationExtras = MutableCreationExtras()
        creationExtras.feedFrom(intent)

        val carrierOfPushData: CarrierOfPushData = extractMaybeOtp(intent)
        if (carrierOfPushData.otp.isNotBlank()) {
            currentChild.receiveFromAncestor(carrierOfPushData)
            return
        }

        val shortcut: FeatureHubShortcut = createFeatureHubShortcut(creationExtras)
        if (shortcut.isDisplayable) {
            currentChild.receiveFromAncestor(shortcut)
            return
        }

        val launcher: SubFlowLauncher = identifySubFlowLauncher(creationExtras)
        currentChild.receiveFromAncestor(launcher)
    }

    private fun extractMaybeOtp(intent: Intent): CarrierOfPushData {
        val otp: String = intent.getStringExtra(PushOtpHandler.OTP).orEmpty()
        val keyAlias: String = intent.getStringExtra(PushOtpHandler.KEY_ALIAS).orEmpty()
        intent.removeExtra(PushOtpHandler.OTP)
        intent.removeExtra(PushOtpHandler.KEY_ALIAS)
        return CarrierOfPushData(otp, keyAlias)
    }

    companion object {

        val HOST_ID_FOR_VISIT_REGISTRY: Long = randomLong()
    }
}