package pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyListType
import com.scotiabank.enhancements.handling.*
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.exception.ExceptionWithResource
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.radiobutton.CollectorOfInstallmentChipsComponent
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.recyclerview.DividerPositionUtil
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.EventOfSelectionController
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.ControllerOfSingleSelection
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.toUiEntities
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class ProductPickingViewModel(
    dispatcherProvider: DispatcherProvider,
    private val weakAppContext: WeakReference<Context?>,
    private val model: ProductPickingModel,
    private val dataFactory: FactoryOfTypeSubmission,
    private val availabilityRegistry: AvailabilityRegistry = AvailabilityRegistry(emptyList()),
    ): NewBaseViewModel(), DispatcherProvider by dispatcherProvider {

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

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val cachedData: MutableMap<Int, Any?> = ConcurrentHashMap()
    private var receiverOfViewModelEvents: InstanceReceiver? = null

    private var compounds: List<UiCompound<*>> = emptyList()

    private var _liveCompounds: MutableLiveData<List<UiCompound<*>>> = MutableLiveData()
    val liveCompounds: LiveData<List<UiCompound<*>>>
        get() = _liveCompounds

    private val callbackForContinueButton: Runnable by lazy {
        Runnable(::onContinueClicked)
    }

    private var isValidateEnableButton: Boolean = false

    fun setUp(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
    }

    @JvmOverloads
    fun fetchProductList(
        isPayableWithCreditCard: Boolean,
        title: String = Constant.EMPTY_STRING,
    ) = viewModelScope.launch {
        setLoadingV2(true)
        tryGetProducts(isPayableWithCreditCard, title)
    }

    private suspend fun tryGetProducts(isPayableWithCreditCard: Boolean, title: String) = try {
        val products: List<ProductModel> = model.getProducts(isPayableWithCreditCard)
        onSuccessfulProducts(products, title)
    } catch (throwable: Throwable) {
        removeControllersThenHideLoading()
        showErrorMessage(throwable)
    }

    private suspend fun onSuccessfulProducts(products: List<ProductModel>, title: String) {
        if (products.isEmpty()) {
            showNoAccountsDialog()
            return
        }
        val data = createStaticDataOfProductPicker(
            title = title.ifBlank { getTextOrEmpty(R.string.choose_origin_account) },
            continueButtonText = getTextOrEmpty(R.string.btn_continue),
            products = products
        )
        _liveCompounds.postValue(compounds)
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(data)
    }

    private fun getTextOrEmpty(@StringRes res: Int): String = weakAppContext.get()
        ?.getString(res)
        .orEmpty()

    private fun showNoAccountsDialog() {
        val exception = ExceptionWithResource(R.string.exception_message_no_accounts)
        removeControllersThenHideLoading()
        receiverOfViewModelEvents?.receive(exception)
    }

    private suspend fun createStaticDataOfProductPicker(
        title: String,
        continueButtonText: String,
        products: List<ProductModel>
    ): StaticDataOfBottomSheetList = withContext(defaultDispatcher) {
        val attributes = AttrsBodyListType(title, continueButtonText)
        val controllerOfRadioButton: ControllerOfSingleSelection<ProductModel> = ControllerOfSingleSelection(
            selfReceiver
        )
        val controllerOfChip: SelectionControllerOfChipsComponent<Int> = SelectionControllerOfChipsComponent(
            selfReceiver
        )
        saveControllers(controllerOfRadioButton, controllerOfChip)
        compounds = dataFactory.createTypes(
            products,
            controllerOfRadioButton,
            controllerOfChip
        )
        val allUiEntities: List<*> = compounds.flatMap(::toUiEntities)
        val dividerPositions: List<Int> = DividerPositionUtil.findDividerPositions(allUiEntities)
        StaticDataOfBottomSheetList(
            attributes = attributes,
            dividerPositions = dividerPositions,
            isCloseButtonVisible = true,
            callbackOfPrimaryButton = callbackForContinueButton,
            id = ProductPicking.IDENTIFIER,
        )
    }

    private fun saveControllers(
        controllerOfRadioButton: ControllerOfSingleSelection<ProductModel>,
        controllerOfChip: SelectionControllerOfChipsComponent<Int>
    ) {
        cachedData[KEY_OF_CONTROLLER_OF_RADIO_BUTTON] = controllerOfRadioButton
        cachedData[KEY_OF_CONTROLLER_OF_CHIP] = controllerOfChip
    }

    private fun onProductChecked(entity: UiEntityOfCheckableButton<*>) {
        notifyEventOnProductChecked(entity)
        _liveCompounds.postValue(compounds)
    }

    @Suppress("UNCHECKED_CAST")
    private fun notifyEventOnProductChecked(entity: UiEntityOfCheckableButton<*>) {
        val selectedItem: UiEntityOfCheckableButton<ProductModel> = entity as? UiEntityOfCheckableButton<ProductModel>
            ?: return
        val productChecked: ProductModel = selectedItem.data
            ?: return
        receiverOfViewModelEvents?.receive(productChecked)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onInstallmentChecked(uiEntityOfChip: UiEntityOfChip<*>) {
        _liveCompounds.postValue(compounds)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onEventOfSelectionController(event: EventOfSelectionController) {
        _liveCompounds.postValue(compounds)
    }

    @Suppress("UNCHECKED_CAST")
    fun onContinueClicked() {
        if (isValidateEnableButton) {
            val isUnavailable: Boolean =
                availabilityRegistry.isAvailable(KEY_OF_CONTROLLER_OF_CANVAS_BUTTON).not()
            if (isUnavailable) return
            changeAvailabilityRegister(false)
        }

        val controllerOfRadioButton = cachedData[KEY_OF_CONTROLLER_OF_RADIO_BUTTON] as? ControllerOfSingleSelection<ProductModel>
            ?: return removeControllersThenHideLoading()
        val selectedItem: UiEntityOfCheckable<ProductModel> = controllerOfRadioButton
            .selectedItem
            ?: return removeControllersThenHideLoading()
        val productPicked: ProductModel = selectedItem.data
            ?: return removeControllersThenHideLoading()
        val controllerOfChip = cachedData[KEY_OF_CONTROLLER_OF_CHIP] as? SelectionControllerOfChipsComponent<Int>
            ?: return removeControllersThenHideLoading()
        val numberOfInstallments = findNumberOfInstallments(controllerOfChip)
        val carrier = CarrierOfProductPicked(productPicked, numberOfInstallments)
        receiverOfViewModelEvents?.receive(carrier)
    }

    private fun findNumberOfInstallments(
        controllerOfChip: SelectionControllerOfChipsComponent<Int>
    ): Int = controllerOfChip.selectedChip
        ?.data
        ?: CollectorOfInstallmentChipsComponent.MIN_NUMBER_OF_INSTALLMENTS

    private fun removeControllersThenHideLoading() {
        cachedData.remove(KEY_OF_CONTROLLER_OF_RADIO_BUTTON)
        cachedData.remove(KEY_OF_CONTROLLER_OF_CHIP)
        setLoadingV2(false)
    }

    fun changeAvailabilityRegister(isAvailability: Boolean) {
        availabilityRegistry.setAvailability(KEY_OF_CONTROLLER_OF_CANVAS_BUTTON, isAvailability)
    }

    fun validateEnableButton() {
        isValidateEnableButton = true
    }

    companion object {

        private val KEY_OF_CONTROLLER_OF_RADIO_BUTTON: Int
            get() = 0
        private val KEY_OF_CONTROLLER_OF_CHIP: Int
            get() = 1

        val KEY_OF_CONTROLLER_OF_CANVAS_BUTTON: Long = randomLong()
    }
}
