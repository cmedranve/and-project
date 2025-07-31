package pe.com.scotiabank.blpm.android.client.base.products.newpicking.bottomsheet

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.DataHolderOfSheetDialogDismissing
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.ProductPickingModel
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.CarrierOfProductPicked
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton.CollectorOfInstallmentChipsComponent
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.EventOfSelectionController
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import java.lang.ref.WeakReference

class ProductPickingSheetCoordinator(
    titleText: String,
    factoryOfComposite: ProductPickingSheetComposite.Factory,
    weakResources: WeakReference<Resources?>,
    private val availabilityRegistry: AvailabilityRegistry,
    private val model: ProductPickingModel,
    weakParent: WeakReference<out Coordinator?>,
    scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    mutableLiveHolder: MutableLiveHolder,
    userInterface: InstanceReceiver,
    uiStateHolder: UiStateHolder,
    override val id: Long = randomLong(),
): CoordinatorImpl(
    weakParent = weakParent,
    scope = scope,
    dispatcherProvider = dispatcherProvider,
    mutableLiveHolder = mutableLiveHolder,
    userInterface = userInterface,
    uiStateHolder = uiStateHolder,
) {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            UiEntityOfCheckableButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onProductChecked)
        )
        .add(
            UiEntityOfChip::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onInstallmentChecked)
        )
        .add(
            EventOfSelectionController::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onEventOfSelectionController)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val sheetComposite: ProductPickingSheetComposite = factoryOfComposite.create(selfReceiver)

    private val callbackForContinueButton: Runnable by lazy {
        Runnable(::handleClickOnContinue)
    }

    private val factoryOfStaticData: ProductPickingStaticDataFactory = ProductPickingStaticDataFactory(
        titleText = titleText,
        weakResources = weakResources,
        callbackForContinueButton = callbackForContinueButton,
    )

    private val selectedProduct: ProductModel?
        get() = sheetComposite
            .controllerOfRadioButton
            .selectedItem
            ?.data

    private val numberOfInstallments: Int
        get() = sheetComposite
            .controllerOfChip
            .selectedChip
            ?.data
            ?: CollectorOfInstallmentChipsComponent.MIN_NUMBER_OF_INSTALLMENTS

    private val _liveCompoundsOfSheetDialog: MutableLiveData<List<UiCompound<*>>> = MutableLiveData()
    override val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>>
        get() = _liveCompoundsOfSheetDialog

    override suspend fun start() = withContext(scope.coroutineContext) {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        tryGettingProductGroup()
        updateUiData()
    }

    override suspend fun updateUiData() = withContext(scope.coroutineContext) {
        sheetComposite.recomposeItselfIfNeeded()
        _liveCompoundsOfSheetDialog.postValue(sheetComposite.compounds)
    }

    private suspend fun tryGettingProductGroup() = try {
        val productGroup: ProductGroup = model.getProductGroup(
            inputData = emptyMap(),
        )
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        onSuccessfulProductGroup(productGroup)
    } catch (throwable: Throwable) {
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        weakParent.get()?.receiveFromChild(throwable)
    }

    private suspend fun onSuccessfulProductGroup(productGroup: ProductGroup) {
        uiStateHolder.currentState = UiState.SUCCESS
        sheetComposite.add(productGroup)
        showStaticData(productGroup)
        updateUiData()
    }

    private fun showStaticData(productGroup: ProductGroup) {
        val staticData: StaticDataOfBottomSheetList = factoryOfStaticData.create(productGroup)
        userInterface.receive(staticData)
    }

    private fun onProductChecked(entity: UiEntityOfCheckableButton<*>) = scope.launch {
        notifyEventOnProductChecked(entity)
        _liveCompoundsOfSheetDialog.postValue(sheetComposite.compounds)
        updateUiData()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun notifyEventOnProductChecked(entity: UiEntityOfCheckableButton<*>) {
        val productChecked: ProductModel = selectedProduct ?: return
        userInterface.receive(productChecked)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onInstallmentChecked(entity: UiEntityOfChip<*>) {
        _liveCompoundsOfSheetDialog.postValue(sheetComposite.compounds)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onEventOfSelectionController(event: EventOfSelectionController) {
        _liveCompoundsOfSheetDialog.postValue(sheetComposite.compounds)
    }

    private fun handleClickOnContinue()  {
        availabilityRegistry.setAvailabilityForAll(false)
        val product: ProductModel = selectedProduct ?: return
        val carrier = CarrierOfProductPicked(
            productPicked = product,
            numberOfInstallments = numberOfInstallments,
        )
        scope.cancel()
        sheetDialogDismissing()
        finishSelf(carrier)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private fun finishSelf(carrier: CarrierOfProductPicked) {
        val finishingCoordinator = FinishingCoordinator(carrier, this)
        weakParent.get()?.receiveFromChild(finishingCoordinator)
    }

    private fun sheetDialogDismissing() {
        _liveCompoundsOfSheetDialog.value = emptyList()
        val dataHolder = DataHolderOfSheetDialogDismissing()
        userInterface.receive(dataHolder)
    }
}
