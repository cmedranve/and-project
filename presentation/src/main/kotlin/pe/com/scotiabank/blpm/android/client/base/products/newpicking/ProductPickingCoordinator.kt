package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import android.content.res.Resources
import androidx.core.util.Consumer
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
import pe.com.scotiabank.blpm.android.analytics.factories.newpayment.paymentconfirmation.PaymentConfirmationAnalyticFactory
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.analytics.util.formatTextToAnalyticsTags
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.number.IntegerParser
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment.EditableInstallment
import pe.com.scotiabank.blpm.android.client.base.products.newpicking.installmentchips.InstallmentOption
import pe.com.scotiabank.blpm.android.client.base.products.picking.FormatterOfProductName
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEvent
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import java.lang.ref.WeakReference

class ProductPickingCoordinator(
    titleText: String,
    factoryOfAppBarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: CompositeForProductPicking.Factory,
    factoryOfAnchoredBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    private val productGroup: ProductGroup,
    private val carrierFromPickingConsumer: CarrierFromPickingConsumer,
    private val integerParser: IntegerParser,
    private val idRegistry: IdRegistry,
    private val availabilityRegistry: AvailabilityRegistry,
    private val analyticConsumer: Consumer<AnalyticEventData<*>>,
    private val embeddedDataName: String,
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
            UiEntityOfCheckableButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onProductChecked)
        )
        .add(
            UiEntityOfChip::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onInstallmentOptionSelected)
        )
        .add(
            UiEntityOfEditText::class,
            InstancePredicate(::filterInInstallmentCleared),
            InstanceHandler(::onInstallmentCleared)
        )
        .add(
            UiEntityOfEditText::class,
            InstancePredicate(::filterInInstallmentEntered),
            InstanceHandler(::onInstallmentEntered)
        )
        .add(
            InputEventCarrier::class,
            InstancePredicate(::filterInImeAction),
            InstanceHandler(::handleImeAction)
        )
        .add(
            InputEventCarrier::class,
            InstancePredicate(::filterInBackKeyPressedWhenEditTextFocused),
            InstanceHandler(::handleBackKeyPressedWhenEditTextFocused)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnContinue)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val appBarComposite: AppBarComposite = factoryOfAppBarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = true,
            iconRes = com.scotiabank.canvascore.R.drawable.canvascore_icon_back,
            titleText = titleText,
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )
    private val mainTopComposite: CompositeForProductPicking = factoryOfMainTopComposite.create(
        receiver = selfReceiver,
    )
    private val anchoredBottomComposite: BottomComposite = factoryOfAnchoredBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(uiStateHolder::isSuccessVisible),
        )
        .addCanvasButton(
            id = idRegistry.continueButtonId,
            isEnabled = false,
            text = weakResources.get()?.getString(R.string.btn_continue).orEmpty(),
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = appBarComposite,
        mainTopComposites = listOf(mainTopComposite),
        anchoredBottomComposites = listOf(anchoredBottomComposite),
    )

    private val selectedProduct: ProductModel?
        get() = mainTopComposite
            .controller
            .selectedItem
            ?.data

    private val entityOfInstallmentField: UiEntityOfEditText<EditableInstallment>?
        get() = mainTopComposite.findEditTextEntityBy(idRegistry.installmentFieldId)

    override suspend fun start() = withContext(scope.coroutineContext) {
        uiStateHolder.currentState = UiState.SUCCESS
        mainTopComposite.add(productGroup)
        selectedProduct?.currency?.let(mainTopComposite::clearThenAddExchangeRate)
        enableContinueButtonByInput()
        sendScreenViewEvent()
        updateUiData()
    }

    private fun sendScreenViewEvent() {
        val embeddedData = carrierFromPickingConsumer.data
        val data : Map<String, Any?> = mapOf(
            embeddedDataName to embeddedData,
            AnalyticsConstant.AMOUNT to carrierFromPickingConsumer.currencyAmounts,
            AnalyticsConstant.TYPE_PAY to PaymentConfirmationAnalyticFactory.PAY_TYPE_TOTAL
        )
        val eventData = AnalyticEventData(AnalyticEvent.SCREEN, data)
        analyticConsumer.accept(eventData)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) {
        hideKeyboard()
        receiveEvent(NavigationIntention.BACK)
    }

    private fun onProductChecked(entity: UiEntityOfCheckableButton<*>) = scope.launch {
        hideKeyboard()
        showExchangeRateIfNeeded(entity)
        mainTopComposite.chipController.reset()
        mainTopComposite.clearEditableInstallments()
        enableContinueButtonByInput()
        updateUiData()
    }

    private fun showExchangeRateIfNeeded(entity: UiEntityOfCheckableButton<*>) {
        val product: ProductModel = entity.data as? ProductModel ?: return
        mainTopComposite.clearThenAddExchangeRate(product.currency)
    }

    private fun enableContinueButtonByInput() {
        anchoredBottomComposite.editCanvasButtonEnabling(
            id = idRegistry.continueButtonId,
            isEnabled = isToBeEnabled(),
        )
    }

    private fun isToBeEnabled(): Boolean {
        val installmentEntity: UiEntityOfEditText<EditableInstallment> = entityOfInstallmentField ?: return true
        val editableInstallment: EditableInstallment = installmentEntity.data ?: return false

        return editableInstallment.isAllowed(installmentEntity.text, integerParser)
    }

    private fun onInstallmentOptionSelected(entity: UiEntityOfChip<*>) = scope.launch {
        hideKeyboard()
        val option: InstallmentOption = entity.data as? InstallmentOption ?: return@launch
        mainTopComposite.clearEditableInstallments()
        option.editableInstallment?.let(mainTopComposite::addEditableInstallment)
        enableContinueButtonByInput()
        updateUiData()
    }

    private fun filterInInstallmentCleared(
        entity: UiEntityOfEditText<*>,
    ): Boolean = entity.text.isBlank()

    private fun onInstallmentCleared(entity: UiEntityOfEditText<*>) = scope.launch {
        showMinMaxTextIfNeeded(entity)
        disableContinueButton()
        updateUiData()
    }

    private fun showMinMaxTextIfNeeded(entity: UiEntityOfEditText<*>) {
        val editableInstallment: EditableInstallment = entity.data as? EditableInstallment ?: return
        entity.errorText = editableInstallment.getErrorTextOrEmpty(entity.text, integerParser)
        val isInformative: Boolean = entity.errorText.isBlank()
        entity.supplementaryText = if (isInformative) editableInstallment.rangeText else Constant.EMPTY_STRING
    }

    private fun disableContinueButton() {
        anchoredBottomComposite.editCanvasButtonEnabling(
            id = idRegistry.continueButtonId,
            isEnabled = false,
        )
    }

    private fun filterInInstallmentEntered(
        entity: UiEntityOfEditText<*>,
    ): Boolean = entity.text.isNotBlank()

    private fun onInstallmentEntered(entity: UiEntityOfEditText<*>) = scope.launch {
        showMinMaxTextIfNeeded(entity)
        enableContinueButtonByInput()
        updateUiData()
    }

    private fun filterInImeAction(
        carrier: InputEventCarrier<*, *>,
    ): Boolean = InputEvent.IME_ACTION_PRESSED == carrier.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleImeAction(carrier: InputEventCarrier<*, *>) = scope.launch {
        hideKeyboard()
    }

    private fun filterInBackKeyPressedWhenEditTextFocused(
        carrier: InputEventCarrier<*, *>,
    ): Boolean = InputEvent.BACK_KEY_PRESSED_WHEN_EDIT_TEXT_FOCUSED == carrier.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleBackKeyPressedWhenEditTextFocused(
        carrier: InputEventCarrier<*, *>,
    ) = scope.launch {
        hideKeyboard()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnContinue(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.continueButtonId)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)
        hideKeyboard()

        val product: ProductModel = selectedProduct
            ?: return@launch availabilityRegistry.setAvailabilityForAll(true)

        val zeroInstallments = 0
        val isCreditCard : Boolean = FormatterOfProductName().isCreditCard(product = product)
        var installmentNumberAnalytic : String = entityOfInstallmentField?.text?.toString().orEmpty()
        val numberOfInstallments: Int = integerParser.parse(installmentNumberAnalytic)
        if(numberOfInstallments == zeroInstallments) installmentNumberAnalytic =
            if(isCreditCard) AnalyticsConstant.NOT_INTALLMENTS
            else AnalyticsConstant.HYPHEN_STRING

        val carrier = CarrierOfProductPicked(
            productPickingScope = scope,
            currencyAmounts = carrierFromPickingConsumer.currencyAmounts,
            data = carrierFromPickingConsumer.data,
            productPicked = product,
            numberOfInstallments = numberOfInstallments,
        )
        sendClickEvent(
            originAccount = formatTextToAnalyticsTags(product.toStringDescriptionAccount()),
            currency = Currency.identifyBy(product.currencyId),
            installmentNumberAnalytic = installmentNumberAnalytic,
        )
        weakParent.get()?.receiveFromChild(carrier)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private fun sendClickEvent(originAccount: String, currency: Currency, installmentNumberAnalytic: String) {
        val embeddedData = carrierFromPickingConsumer.data
        val data : Map<String, Any?> = mapOf(
            embeddedDataName to embeddedData,
            AnalyticsConstant.AMOUNT to carrierFromPickingConsumer.currencyAmounts,
            AnalyticsConstant.TYPE_PAY to PaymentConfirmationAnalyticFactory.PAY_TYPE_TOTAL,
            AnalyticsConstant.TYPE_OF_ORIGIN_ACCOUNT to originAccount,
            AnalyticsConstant.ORIGIN_CURRENCY to currency,
            AnalyticsConstant.INSTALLMENTS_NUMBER to installmentNumberAnalytic,
        )
        val eventData = AnalyticEventData(AnalyticEvent.CONTINUE, data)
        analyticConsumer.accept(eventData)
    }
}
