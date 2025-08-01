package pe.com.scotiabank.blpm.android.client.newdashboard.add

import android.content.res.Resources
import androidx.core.util.Supplier
import androidx.lifecycle.viewModelScope
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.ui.list.viewmodel.PortableViewModel
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.newpayment.legacybridge.PaymentHostActivity
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.data.model.RecentTransactionModel
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling
import java.lang.ref.WeakReference

class AddOperationViewModel(
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    factoryOfAnchoredBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    private val visitRegistry: VisitRegistry,
    private val model: AddOperationModel,
    private val converter: ConverterForRecentOperation,
    private val frequentOperationType: FrequentOperationType,
    override val id: Long = randomLong(),
    private val mutableLiveHolder: MutableLiveHolder = MutableLiveHolder(),
    recycling: Recycling = StatefulRecycling(),
) : NewBaseViewModel(), PortableViewModel, LiveHolder by mutableLiveHolder, Recycling by recycling {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            UiEntityOfToolbar::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnToolbarIcon)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInPayClicked),
            InstanceHandler(::handleClickOnPay)
        )
        .add(
            UiEntityOfCheckableButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onOperationChecked)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInAddClicked),
            InstanceHandler(::handleClickOnAdd)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = true,
            iconRes = com.scotiabank.canvascore.R.drawable.canvascore_icon_back,
            titleText = weakResources.get()?.getString(R.string.list_recent_new_pay_toolbar).orEmpty(),
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create(selfReceiver)

    private val checkedOperations: Collection<UiEntityOfCheckable<RecentTransactionModel>>
        get() = mainTopComposite.controller.itemSelection

    private val isPayButtonGoingToBeVisible: Boolean
        get() = mainTopComposite.isEmptyVisible
    private val payButtonId: Long = randomLong()
    private val mainBottomComposite: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(::isPayButtonGoingToBeVisible)
        )
        .addCanvasButton(
            id = payButtonId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.list_recent_pay_service_button).orEmpty(),
        )

    private val isAddButtonGoingToBeVisible: Boolean
        get() = checkedOperations.isNotEmpty()

    private val addButtonId: Long = randomLong()
    private val anchoredBottomComposite: BottomComposite = factoryOfAnchoredBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(::isAddButtonGoingToBeVisible)
        )
        .addCanvasButton(
            id = addButtonId,
            isEnabled = true,
            text = weakResources.get()
                ?.getString(R.string.my_list_add_recent_payment_transaction_coach_mark_title)
                .orEmpty(),
        )

    private var receiverOfViewModelEvents: InstanceReceiver? = null

    override fun receiveEvent(event: Any): Boolean = selfReceiver.receive(event)

    override fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
        setUpUiData()
    }

    private fun setUpUiData() {
        if (visitRegistry.isVisitAllowed(OPERATION_LIST_ID_FOR_VISIT_REGISTRY).not()) {
            putUiData()
            return
        }
        setLoadingV2(true)
        putUiData()
        startGettingOperations()
    }

    private fun putUiData() = viewModelScope.launch {
        putUiDataLaunchedByCoroutineScope()
    }

    private suspend fun putUiDataLaunchedByCoroutineScope() {
        toolbarComposite.recomposeItselfIfNeeded()
        mainTopComposite.recomposeItselfIfNeeded()
        mainBottomComposite.recomposeItselfIfNeeded()
        anchoredBottomComposite.recomposeItselfIfNeeded()

        mutableLiveHolder.notifyAppBarAndMain(
            appBar = toolbarComposite.compounds,
            mainTop = mainTopComposite.compounds,
            mainBottom = mainBottomComposite.compounds,
        )
        mutableLiveHolder.anchoredBottom.postValue(anchoredBottomComposite.compounds)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) {
        handleOnBackClicked()
    }

    fun handleOnBackClicked() {
        receiverOfViewModelEvents?.receive(NavigationIntention.BACK)
    }

    private fun startGettingOperations() = viewModelScope.launch {
        tryGetOperations()
    }

    private suspend fun tryGetOperations() = try {
        val type: FrequentOperationType = frequentOperationType

        val dataEntities: List<RecentTransactionModel> = model.getRecentOperationsBy(
            type = type.typeFromNetworkCall,
        )
        mainTopComposite.currentState = UiState.from(dataEntities.size)
        dataEntities.forEach(mainTopComposite::add)
        setLoadingV2(false)
        putUiDataLaunchedByCoroutineScope()
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private fun filterInPayClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = entity.id == payButtonId

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnPay(entity: UiEntityOfCanvasButton<*>) = viewModelScope.launch {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = PaymentHostActivity::class.java,
        )
        receiverOfViewModelEvents?.receive(carrier)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onOperationChecked(entity: UiEntityOfCheckableButton<*>) {
        putUiData()
    }

    private fun filterInAddClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = entity.id == addButtonId

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnAdd(entity: UiEntityOfCanvasButton<*>) = viewModelScope.launch {

        setLoadingV2(true)

        val operations: List<RecentTransactionModel> = checkedOperations
            .mapNotNull(::attemptToRecentOperation)

        tryAddOperations(operations)
    }

    private suspend fun tryAddOperations(operations: List<RecentTransactionModel>) = try {
        model.saveRecentOperations(operations)

        val frequentOperations: List<FrequentOperationModel> = operations
            .map(converter::toFrequentOperation)

        val carrier = CarrierOfOperationListAdded(frequentOperations, frequentOperationType)
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(carrier)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private fun attemptToRecentOperation(
        entity: UiEntityOfCheckable<RecentTransactionModel>,
    ): RecentTransactionModel? = entity.data

    companion object {

        val OPERATION_LIST_ID_FOR_VISIT_REGISTRY: Long = randomLong()
    }
}
