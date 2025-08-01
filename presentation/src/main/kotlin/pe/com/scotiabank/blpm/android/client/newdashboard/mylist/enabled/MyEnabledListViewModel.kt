package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.enabled

import android.content.res.Resources
import android.text.SpannableStringBuilder
import androidx.core.util.Supplier
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyListType
import com.scotiabank.canvascore.buttons.CanvasButtonLoading
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.ui.list.composite.LiveHolder
import pe.com.scotiabank.blpm.android.ui.list.composite.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfFragmentDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.client.base.products.picking.bottomsheetdialog.CarrierOfProductPicked
import pe.com.scotiabank.blpm.android.client.base.receipt.ReceiptActivity
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.model.ConstraintModel
import pe.com.scotiabank.blpm.android.client.model.DebtWrapperModel
import pe.com.scotiabank.blpm.android.client.model.OtherBankSeedModel
import pe.com.scotiabank.blpm.android.client.model.PaymentSummaryModel
import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.newdashboard.add.AddOperationFragment
import pe.com.scotiabank.blpm.android.client.newdashboard.add.CarrierOfOperationListAdded
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.CarrierOfOperationEdited
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.CarrierOfOperationToEdit
import pe.com.scotiabank.blpm.android.client.newdashboard.edit.EditOperationFragment
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.Intention
import pe.com.scotiabank.blpm.android.client.base.ViewModelWithSheetDialog
import pe.com.scotiabank.blpm.android.client.base.products.picking.IntentionPicker
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.submenu.SubMenuComposite
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.submenu.SubMenuOption
import pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary.FrequentOperationSummary
import pe.com.scotiabank.blpm.android.client.payment.institutions.search.company.bills.BillsActivity
import pe.com.scotiabank.blpm.android.client.products.frequents.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.templates.FeatureTemplate
import pe.com.scotiabank.blpm.android.client.templates.OptionTemplate
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.TemplatesUtil
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.silenceThrowable
import pe.com.scotiabank.blpm.android.data.exception.isForcingLogOut
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading.CanvasButtonLoadingController
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading.UiEntityOfCanvasButtonLoading
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class MyEnabledListViewModel(
    dispatcherProvider: DispatcherProvider,
    factoryOfToolbarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopCompositeForEnabled.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    factoryOfAnchoredBottomComposite: BottomComposite.Factory,
    factoryOfSubMenuComposite: SubMenuComposite.Factory,
    private val appModel: AppModel,
    private val weakResources: WeakReference<Resources?>,
    private val myListTemplate: FeatureTemplate,
    private val templateForAddingRecentPayments: OptionTemplate,
    private val idRegistry: IdRegistry,
    private val visitRegistry: VisitRegistry,
    private val availabilityRegistry: AvailabilityRegistry,
    private val model: MyListModel,
    private val deleteModel: DeleteOperationModel,
    private val paymentModel: PaymentModel,
    private val confirmModel: ConfirmModel,
    private val selectionHelper: SelectionHelper,
    private val summaryHelper: SummaryHelper,
    private val helperForMaxSelectionDialog: HelperForMaxSelectionDialog,
    private val paymentEnablingWithCreditCard: PaymentEnablingWithCreditCard,
    private val helperForDeletionDialog: HelperForDeletionDialog,
    private val textBuilderForSnackBar: TextBuilderForSnackBar,
    override val id: Long = randomLong(),
    private val mutableLiveHolder: MutableLiveHolder = MutableLiveHolder(),
    recycling: Recycling = StatefulRecycling(),
) : NewBaseViewModel(),
    DispatcherProvider by dispatcherProvider,
    ViewModelWithSheetDialog,
    LiveHolder by mutableLiveHolder,
    Recycling by recycling
{

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            Intention::class,
            InstancePredicate(::filterInMyListTabClicked),
            InstanceHandler(::onMyListTabClicked)
        )
        .add(
            UiEntityOfCanvasButtonLoading::class,
            InstancePredicate(::filterInRetrySummaryClicked),
            InstanceHandler(::onRetrySummaryClicked)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInAddPayClicked),
            InstanceHandler(::onAddPaymentClicked)
        )
        .add(
            UiEntityOfChip::class,
            InstancePredicate(::filterInTransferTypeSelected),
            InstanceHandler(::onTransferTypeSelected)
        )
        .add(
            UiEntityOfCanvasButtonLoading::class,
            InstancePredicate(::filterInRetryGettingOperationsClicked),
            InstanceHandler(::onRetryGettingOperationsClicked)
        )
        .add(
            UiEntityOfChip::class,
            InstancePredicate(::filterInPaymentTypeSelected),
            InstanceHandler(::onPaymentTypeSelected)
        )
        .add(
            UiEntityOfChip::class,
            InstancePredicate(::filterInDisabledOperationType),
            InstanceHandler(::handleDisabledOperationType)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInAddClicked),
            InstanceHandler(::onAddClicked)
        )
        .add(
            CarrierOfOperationListAdded::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onOperationsAdded)
        )
        .add(
            UiEntityOfCheckableButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onOperationChecked)
        )
        .add(
            Intention::class,
            InstancePredicate(::filterInOkAboutMaxSelection),
            InstanceHandler(::handleOkAboutMaxSelection)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInSubMenuClicked),
            InstanceHandler(::onSubMenuClicked)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInEditClicked),
            InstanceHandler(::onEditClicked)
        )
        .add(
            CarrierOfOperationToEdit::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onEnsureEditClicked)
        )
        .add(
            CarrierOfOperationEdited::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onEditSaved)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInDeleteClicked),
            InstanceHandler(::onDeleteClicked)
        )
        .add(
            Intention::class,
            InstancePredicate(::filterInStartDeletingOperation),
            InstanceHandler(::startDeletingOperation)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInSeeMoreReceiptsClicked),
            InstanceHandler(::onSeeMoreReceiptsClicked)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInTransferAmountClicked),
            InstanceHandler(::onTransferAmountClicked)
        )
        .add(
            CarrierOfProductPicked::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::startConfirming)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInPayAmountClicked),
            InstanceHandler(::onPayAmountClicked)
        )
        .add(
            DeferredEvent::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::storeDeferredEvent)
        )
        .add(
            Boolean::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::putUiChangeEnable)
        )
        .build()

    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val toolbarComposite: AppBarComposite = factoryOfToolbarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = false,
            iconRes = pe.com.scotiabank.blpm.android.ui.R.drawable.ic_logo,
        )

    private val mainTopComposite: MainTopCompositeForEnabled = factoryOfMainTopComposite.create(selfReceiver)
    private val millisOfRetryIcon: Long by lazy {
        2000
    }
    private val frequentOperationType: FrequentOperationType?
        get() = mainTopComposite.selectedFrequentOperationType
    private val compositeForOperationTypeSection: CompositeForOperationTypeSection?
        get() = frequentOperationType
            ?.id
            ?.let(mainTopComposite.compositeByTypeId::get)
    private val checkedOperationEntitiesOfTypeSection: Collection<UiEntityOfCheckable<FrequentOperationModel>>
        get() = compositeForOperationTypeSection
            ?.controller
            ?.itemSelection
            .orEmpty()
    private val checkedOperationsOfTypeSection: List<FrequentOperationModel>
        get() = checkedOperationEntitiesOfTypeSection
            .mapNotNull(selectionHelper::attemptToFrequentOperation)
    private val quantityOfCheckedOperations: Int
        get() = checkedOperationsOfTypeSection.size
    private val amountsByTransactionalCurrency: Map<Currency, Double>
        get() = checkedOperationEntitiesOfTypeSection
            .groupBy(selectionHelper::byCurrency, selectionHelper::toAmount)
            .filterKeys(Currency::isTransactional)
            .mapValues(selectionHelper::toSumOf)

    private val isAddButtonGoingToBeVisible: Boolean
        get() = mainTopComposite.isEmptyVisible
    private val mainBottomComposite: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(::isAddButtonGoingToBeVisible)
        )
        .addCanvasButton(
            id = idRegistry.addButtonId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.my_list_empty_list_add_button).orEmpty(),
        )

    private val isAmountButtonGoingToBeVisible: Boolean
        get() = checkedOperationEntitiesOfTypeSection.isNotEmpty()

    private val anchoredBottomComposite: BottomComposite = factoryOfAnchoredBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(::isAmountButtonGoingToBeVisible)
        )
        .addCanvasButton(
            id = idRegistry.amountButtonId,
            isEnabled = true,
            text = Constant.EMPTY_STRING,
        )

    private val subMenuComposite: SubMenuComposite = factoryOfSubMenuComposite.create(selfReceiver)
    private val cachedData: MutableMap<Int, Any?> = ConcurrentHashMap()

    private var _liveCompoundsOfSheetDialog: MutableLiveData<List<UiCompound<*>>> = MutableLiveData()
    override val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>>
        get() = _liveCompoundsOfSheetDialog

    private var deferredEvents: MutableSet<DeferredEvent> = ConcurrentHashMap.newKeySet()
    private var receiverOfViewModelEvents: InstanceReceiver? = null

    init {
        removeAddButtonIfHidden()
        appModel.addChild(this)
    }

    private fun removeAddButtonIfHidden() {
        if (templateForAddingRecentPayments.isVisible) return
        mainBottomComposite.removeCanvasButton(idRegistry.addButtonId)
    }

    override fun receiveEvent(event: Any): Boolean = selfReceiver.receive(event)

    private fun storeDeferredEvent(event: DeferredEvent) {
        deferredEvents.add(event)
    }

    override fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
    }

    private fun filterInMyListTabClicked(
        intention: Intention,
    ): Boolean = Intention.NOTIFY_CLICK_ON_MY_LIST_TAB == intention

    @Suppress("UNUSED_PARAMETER")
    private fun onMyListTabClicked(intention: Intention) = viewModelScope.launch {
        if (deferredEvents.isNotEmpty()) {
            handleDeferredEvents()
            return@launch
        }

        if (visitRegistry.isVisitAllowed(idRegistry.tabIdOfSummary).not()) {
            putUiDataLaunchedByCoroutineScope()
            return@launch
        }
        loadSummaries()
    }

    private suspend fun handleDeferredEvents() {
        val cachedEvents: MutableSet<DeferredEvent> = deferredEvents
        deferredEvents = ConcurrentHashMap.newKeySet()
        visitRegistry.resetAttemptsById(idRegistry.buttonIdOfSummaryRetry)

        if (mainTopComposite.isErrorVisible) {
            retryGettingSummary(true)
            return
        }

        if (mainTopComposite.isEmptyVisible) {
            loadSummaries()
            return
        }

        if (mainTopComposite.isSuccessVisible.not()) return

        for (cachedEvent in cachedEvents) {
            handleDeferredEvent(cachedEvent)
        }
    }

    private suspend fun handleDeferredEvent(event: DeferredEvent) {

        val sectionComposite: CompositeForOperationTypeSection = compositeForOperationTypeSection
            ?: return
        val typeFromEvent: FrequentOperationType = event.type

        visitRegistry.resetAttemptsById(typeFromEvent.retryId)
        availabilityRegistry.setAvailability(typeFromEvent.retryId, true)

        if (sectionComposite.isErrorVisible) {
            handleTypeFromEventIfSectionError(typeFromEvent, sectionComposite)
            return
        }

        if (sectionComposite.isEmptyVisible xor sectionComposite.isSuccessVisible) {
            handleTypeFromEventIfSectionEmptyOrSuccess(typeFromEvent, sectionComposite)
        }
    }

    private suspend fun handleTypeFromEventIfSectionError(
        typeFromEvent: FrequentOperationType,
        sectionComposite: CompositeForOperationTypeSection,
    ) {
        if (typeFromEvent == sectionComposite.frequentOperationType) {
            retryGettingOperations(true)
            return
        }

        sectionComposite.editForCanvasButtonLoading(
            id = typeFromEvent.retryId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.try_again).orEmpty(),
            state = CanvasButtonLoading.STATE_IDLE,
        )
        putUiDataLaunchedByCoroutineScope()
    }

    private suspend fun handleTypeFromEventIfSectionEmptyOrSuccess(
        typeFromEvent: FrequentOperationType,
        sectionComposite: CompositeForOperationTypeSection,
    ) {
        if (typeFromEvent == sectionComposite.frequentOperationType) {
            showSkeletonLoading(sectionComposite)
            tryGettingOperations(sectionComposite, false)
            return
        }

        visitRegistry.resetAttemptsById(typeFromEvent.id)
    }

    private fun putUiData() = viewModelScope.launch {
        putUiDataLaunchedByCoroutineScope()
    }

    private suspend fun putUiDataLaunchedByCoroutineScope() {
        updateAmountButton()
        recomposeComposite()
    }

    private suspend fun recomposeComposite() {
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

    private suspend fun loadSummaries() {
        showSkeletonLoading(mainTopComposite)
        tryGettingSummaries(false)
    }

    private suspend fun showSkeletonLoading(uiStateHolder: UiStateHolder) {
        uiStateHolder.currentState = UiState.LOADING
        putUiDataLaunchedByCoroutineScope()
    }

    private fun updateAmountButton() {
        val isGoingToBeEnabled: Boolean = quantityOfCheckedOperations in MIN_TO_ENABLE_BUTTON..MAX_TO_ENABLE_BUTTON
        editCanvasButtonEnabling(isGoingToBeEnabled)
        val text: CharSequence = buildAmountText()
        anchoredBottomComposite.editCanvasButtonText(idRegistry.amountButtonId, text)
    }

    private fun buildAmountText(): CharSequence {
        val type: FrequentOperationType = frequentOperationType ?: return Constant.EMPTY_STRING

        val formattedAmountsByTransactionalCurrency: Map<Currency, CharSequence> = amountsByTransactionalCurrency
            .mapValues(selectionHelper::formatAmount)

        val textOnAmounts: CharSequence = Currency
            .findTransactionalCurrencies()
            .mapNotNull(formattedAmountsByTransactionalCurrency::get)
            .joinToString(separator = Constant.SPACE_WHITE + Constant.AND + Constant.SPACE_WHITE)

        return type.actionText + Constant.SPACE_WHITE + textOnAmounts
    }

    private suspend fun tryGettingSummaries(
        isRetryingProgrammatically: Boolean,
        millisOfRetryIcon: Long = 0,
    ) = try {
        val summaries: List<FrequentOperationSummary> = model.getFrequentOperationSummaries()
        mainTopComposite.editForCanvasButtonLoading(
            id = idRegistry.buttonIdOfSummaryRetry,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.ready).orEmpty(),
            state = CanvasButtonLoading.STATE_SUCCESS,
        )
        availabilityRegistry.setAvailability(idRegistry.buttonIdOfSummaryRetry, true)
        putUiDataLaunchedByCoroutineScope()
        delay(millisOfRetryIcon)
        onSummaries(summaries)
    } catch (throwable: Throwable) {
        availabilityRegistry.setAvailability(idRegistry.buttonIdOfSummaryRetry, true)
        if (throwable.isForcingLogOut) {
            showErrorMessage(throwable)
        } else {
            mainTopComposite.currentState = UiState.ERROR
            showErrorState(
                controller = mainTopComposite,
                retryButtonId = idRegistry.buttonIdOfSummaryRetry,
                isRetryingProgrammatically = isRetryingProgrammatically,
            )
        }
    }

    private suspend fun showErrorState(
        controller: CanvasButtonLoadingController,
        retryButtonId: Long,
        isRetryingProgrammatically: Boolean,
    ) {
        controller.editForCanvasButtonLoading(
            id = retryButtonId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.try_again).orEmpty(),
            state = identifyStateForRetryButton(retryButtonId, isRetryingProgrammatically),
        )

        putUiDataLaunchedByCoroutineScope()

        if (visitRegistry.isVisitAllowed(retryButtonId)) return

        delay(millisOfRetryIcon)
        controller.editForCanvasButtonLoading(
            id = retryButtonId,
            isEnabled = false,
            text = weakResources.get()?.getString(R.string.try_again).orEmpty(),
            state = CanvasButtonLoading.STATE_IDLE,
        )
        putUiDataLaunchedByCoroutineScope()
    }

    private fun identifyStateForRetryButton(id: Long, isRetryingProgrammatically: Boolean): Int {
        if (isRetryingProgrammatically) return CanvasButtonLoading.STATE_ERROR
        val isVisited: Boolean = visitRegistry.findNumberOfVisits(id) > 0
        return if (isVisited) CanvasButtonLoading.STATE_ERROR else CanvasButtonLoading.STATE_IDLE
    }

    private suspend fun onSummaries(summaries: List<FrequentOperationSummary>) {
        if (summaries.all(summaryHelper::isEmpty)) {
            mainTopComposite.currentState = UiState.EMPTY
            putUiDataLaunchedByCoroutineScope()
            return
        }
        val type: FrequentOperationType = summaryHelper.findFirstTypeNotEmpty(summaries) ?: return
        val chipEntity: UiEntityOfChip<FrequentOperationType> = mainTopComposite.chipEntitiesByFrequentOperationTypeText[type.displayText]
            ?: return
        mainTopComposite.controllerOfFrequentOperationType.setDefaultChip(chipEntity)
        mainTopComposite.currentState = UiState.SUCCESS
        selfReceiver.receive(chipEntity)
    }

    private fun filterInRetrySummaryClicked(
        entity: UiEntityOfCanvasButtonLoading<*>,
    ): Boolean = idRegistry.buttonIdOfSummaryRetry == entity.id

    @Suppress("UNUSED_PARAMETER")
    private fun onRetrySummaryClicked(
        entity: UiEntityOfCanvasButtonLoading<*>
    ) = viewModelScope.launch {
        retryGettingSummary()
    }

    private suspend fun retryGettingSummary(isRetryingProgrammatically: Boolean = false) {
        val isUnavailable: Boolean = availabilityRegistry.isAvailable(idRegistry.buttonIdOfSummaryRetry).not()
        if (isUnavailable) return

        availabilityRegistry.setAvailability(idRegistry.buttonIdOfSummaryRetry, false)

        mainTopComposite.editForCanvasButtonLoading(
            id = idRegistry.buttonIdOfSummaryRetry,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.loading).orEmpty(),
            state = CanvasButtonLoading.STATE_LOADING,
        )
        putUiDataLaunchedByCoroutineScope()
        tryGettingSummaries(isRetryingProgrammatically, millisOfRetryIcon)
    }

    private fun filterInAddPayClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = entity.id == idRegistry.addButtonId

    @Suppress("UNUSED_PARAMETER")
    private fun onAddPaymentClicked(entity: UiEntityOfCanvasButton<*>) {
        attemptGoingToAddPayment()
    }

    private fun attemptGoingToAddPayment() {
        appModel.startInactivityTimer()

        val isDisabled: Boolean = TemplatesUtil.isDisabled(templateForAddingRecentPayments.type)
        if (isDisabled) {
            receiverOfViewModelEvents?.receive(templateForAddingRecentPayments)
            return
        }
        val carrier: CarrierOfFragmentDestination = destinationCarrierOf(
            screenDestination = AddOperationFragment::class.java,
        )
        receiverOfViewModelEvents?.receive(carrier)
    }

    private fun filterInTransferTypeSelected(entity: UiEntityOfChip<*>): Boolean {
        val expectedType: FrequentOperationType = FrequentOperationType.TRANSFER
        return if (expectedType == entity.data) isOperationTypeEnabled(expectedType) else false
    }

    private fun isOperationTypeEnabled(
        type: FrequentOperationType,
    ): Boolean = isOperationTypeDisabled(type).not()

    private fun isOperationTypeDisabled(type: FrequentOperationType): Boolean {
        val template: OptionTemplate = type.optionTemplateFinder.apply(myListTemplate)
        return TemplatesUtil.isDisabled(template.type)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onTransferTypeSelected(entity: UiEntityOfChip<*>) = viewModelScope.launch {
        val sectionComposite: CompositeForOperationTypeSection = compositeForOperationTypeSection
            ?: return@launch
        val type: FrequentOperationType = sectionComposite.frequentOperationType

        if (visitRegistry.isVisitAllowed(type.id).not()) {
            putUiDataLaunchedByCoroutineScope()
            return@launch
        }

        showSkeletonLoading(sectionComposite)
        fetchImmediateAvailability()
        tryGettingOperations(sectionComposite, false)
    }

    private fun filterInRetryGettingOperationsClicked(
        entity: UiEntityOfCanvasButtonLoading<*>,
    ): Boolean = entity.data is FrequentOperationType

    @Suppress("UNUSED_PARAMETER")
    private fun onRetryGettingOperationsClicked(
        entity: UiEntityOfCanvasButtonLoading<*>,
    ) = viewModelScope.launch {
        retryGettingOperations()
    }

    private suspend fun retryGettingOperations(isRetryingProgrammatically: Boolean = false) {
        val sectionComposite: CompositeForOperationTypeSection = compositeForOperationTypeSection
            ?: return
        val type: FrequentOperationType = sectionComposite.frequentOperationType

        val isUnavailable: Boolean = availabilityRegistry.isAvailable(type.retryId).not()
        if (isUnavailable) return

        availabilityRegistry.setAvailability(type.retryId, false)

        sectionComposite.editForCanvasButtonLoading(
            id = type.retryId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.loading).orEmpty(),
            state = CanvasButtonLoading.STATE_LOADING,
        )
        putUiDataLaunchedByCoroutineScope()
        tryGettingOperations(sectionComposite, isRetryingProgrammatically, millisOfRetryIcon)
    }

    private suspend fun fetchImmediateAvailability(): Unit = withContext(ioDispatcher) {
        appModel
            .otherSeed
            .retry(
                retries = Long.MAX_VALUE,
                predicate = ::filterInAnySubType,
            )
            .onEach(::onOtherSeedFetched)
            .catch(::silenceThrowable)
            .collect()
    }

    private fun onOtherSeedFetched(otherSeed: OtherBankSeedModel) {
        val constraint: ConstraintModel = otherSeed.constraintModels.firstOrNull() ?: return
        mainTopComposite.isImmediateAvailable = constraint.enabled
    }

    private fun filterInPaymentTypeSelected(entity: UiEntityOfChip<*>): Boolean {
        val expectedType: FrequentOperationType = FrequentOperationType.PAYMENT
        return if (expectedType == entity.data) isOperationTypeEnabled(expectedType) else false
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onPaymentTypeSelected(entity: UiEntityOfChip<*>) = viewModelScope.launch {
        val sectionComposite: CompositeForOperationTypeSection = compositeForOperationTypeSection
            ?: return@launch
        val type: FrequentOperationType = sectionComposite.frequentOperationType

        if (visitRegistry.isVisitAllowed(type.id).not()) {
            putUiDataLaunchedByCoroutineScope()
            return@launch
        }
        showSkeletonLoading(sectionComposite)
        tryGettingOperations(sectionComposite, false)
    }

    private suspend fun tryGettingOperations(
        sectionComposite: CompositeForOperationTypeSection,
        isRetryingProgrammatically: Boolean,
        millisOfRetryIcon: Long = 0,
    ) = try {
        val type: FrequentOperationType = sectionComposite.frequentOperationType
        val dataEntities: List<FrequentOperationModel> = model.getFrequentOperationsBy(
            type = type.typeFromNetworkCall,
        )
        sectionComposite.editForCanvasButtonLoading(
            id = type.retryId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.ready).orEmpty(),
            state = CanvasButtonLoading.STATE_SUCCESS,
        )
        availabilityRegistry.setAvailability(type.retryId, true)
        putUiDataLaunchedByCoroutineScope()
        delay(millisOfRetryIcon)
        sectionComposite.clear()
        dataEntities.forEach(sectionComposite::add)
        putUiDataLaunchedByCoroutineScope()
    } catch (throwable: Throwable) {
        val type: FrequentOperationType = sectionComposite.frequentOperationType
        availabilityRegistry.setAvailability(type.retryId, true)
        if (throwable.isForcingLogOut) {
            showErrorMessage(throwable)
        } else {
            sectionComposite.currentState = UiState.ERROR
            showErrorState(sectionComposite, type.retryId, isRetryingProgrammatically)
        }
    }

    private fun filterInDisabledOperationType(entity: UiEntityOfChip<*>): Boolean {
        val type: FrequentOperationType = entity.data as? FrequentOperationType ?: return false
        return isOperationTypeDisabled(type)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleDisabledOperationType(entity: UiEntityOfChip<*>) = viewModelScope.launch {
        val sectionComposite: CompositeForOperationTypeSection = compositeForOperationTypeSection
            ?: return@launch
        sectionComposite.currentState = UiState.DISABLED
        putUiDataLaunchedByCoroutineScope()
    }

    private fun filterInAddClicked(
        entity: UiEntityOfTextButton<*>,
    ): Boolean = Intention.GO_ADD_PAYMENTS == entity.data

    @Suppress("UNUSED_PARAMETER")
    private fun onAddClicked(entity: UiEntityOfTextButton<*>) {
        attemptGoingToAddPayment()
    }

    private fun onOperationsAdded(carrier: CarrierOfOperationListAdded) = viewModelScope.launch {

        val operations: List<FrequentOperationModel> = carrier.operationsAdded
        val text: SpannableStringBuilder = textBuilderForSnackBar.buildTextForAdding(operations)
        receiverOfViewModelEvents?.receive(text)

        visitRegistry.resetAttemptsById(idRegistry.buttonIdOfSummaryRetry)

        if (mainTopComposite.isEmptyVisible) {
            loadSummaries()
            return@launch
        }

        val sectionComposite: CompositeForOperationTypeSection = compositeForOperationTypeSection
            ?: return@launch

        showSkeletonLoading(sectionComposite)
        tryGettingOperations(sectionComposite, false)
    }

    private fun onOperationChecked(entity: UiEntityOfCheckableButton<*>) {
        val type: FrequentOperationType = frequentOperationType ?: return
        val operation: FrequentOperationModel = entity.data as? FrequentOperationModel ?: return

        putUiData()
        val isGreaterThanAllowed: Boolean = quantityOfCheckedOperations > MAX_TO_ENABLE_BUTTON
        if (isGreaterThanAllowed.not()) return

        val data = DataOfMaxSelectionDialog(helperForMaxSelectionDialog, type)
        receiverOfViewModelEvents?.receive(data)
    }

    private fun filterInOkAboutMaxSelection(
        intention: Intention,
    ): Boolean = Intention.NOTIFY_OK_ABOUT_MAX_SELECTION == intention

    @Suppress("UNUSED_PARAMETER")
    private fun handleOkAboutMaxSelection(intention: Intention) {
        // Notify to analytics
    }

    private fun filterInSubMenuClicked(entity: UiEntityOfTextButton<*>): Boolean {
        val carrier: CarrierOfFrequentOperation = entity.data as? CarrierOfFrequentOperation
            ?: return false
        return IdentifiableTextButton.SUB_MENU == carrier.eventFrom
    }

    private fun onSubMenuClicked(entity: UiEntityOfTextButton<*>) {
        appModel.startInactivityTimer()
        val carrier: CarrierOfFrequentOperation = entity.data as? CarrierOfFrequentOperation
            ?: return
        cachedData[KEY_OF_OPERATION_ON_SUB_MENU] = carrier.operation
        showSubMenu()
    }

    private fun showSubMenu() = viewModelScope.launch {
        subMenuComposite.recomposeItselfIfNeeded()
        _liveCompoundsOfSheetDialog.postValue(subMenuComposite.compounds)
        val data = createStaticDataOfSubMenu()
        receiverOfViewModelEvents?.receive(data)
    }

    private fun createStaticDataOfSubMenu(): StaticDataOfBottomSheetList {
        val secondaryButtonLabel: String = weakResources.get()?.getString(R.string.cancel).orEmpty()
        val attributes = AttrsBodyListType(Constant.EMPTY_STRING, Constant.EMPTY_STRING, secondaryButtonLabel)
        return StaticDataOfBottomSheetList(
            attributes = attributes,
            dividerPositions = listOf(DIVIDER_POSITION_FOR_SUB_MENU),
            isCloseButtonVisible = false,
            id = BottomSheetIdentifier.SUB_MENU,
        )
    }

    private fun filterInEditClicked(
        entity: UiEntityOfTextButton<*>,
    ): Boolean = SubMenuOption.EDIT == entity.data

    @Suppress("UNUSED_PARAMETER")
    private fun onEditClicked(entity: UiEntityOfTextButton<*>) {
        val frequentOperation: FrequentOperationModel = cachedData[KEY_OF_OPERATION_ON_SUB_MENU] as? FrequentOperationModel
            ?: return

        val carrier = CarrierOfOperationToEdit(frequentOperation)
        receiverOfViewModelEvents?.receive(carrier)
    }

    private fun onEnsureEditClicked(carrierOfOperationToEdit: CarrierOfOperationToEdit) {
        val frequentOperation: FrequentOperationModel = carrierOfOperationToEdit.frequentOperation
        val carrierOfFragmentDestination: CarrierOfFragmentDestination = destinationCarrierOf(
            screenDestination = EditOperationFragment::class.java,
            tagForBackStack = frequentOperation.id.toString(),
        ) {
            FREQUENT_OPERATION to frequentOperation
        }
        receiverOfViewModelEvents?.receive(carrierOfFragmentDestination)
    }

    private fun onEditSaved(carrier: CarrierOfOperationEdited) {
        val newOperation: FrequentOperationModel = carrier.frequentOperation
        compositeForOperationTypeSection?.edit(newOperation)
        val text: SpannableStringBuilder = textBuilderForSnackBar.buildTextForEditing()
        receiverOfViewModelEvents?.receive(text)
        putUiData()
    }

    private fun filterInDeleteClicked(
        entity: UiEntityOfTextButton<*>,
    ): Boolean = SubMenuOption.DELETE == entity.data

    @Suppress("UNUSED_PARAMETER")
    private fun onDeleteClicked(entity: UiEntityOfTextButton<*>) {
        val operation: FrequentOperationModel = cachedData[KEY_OF_OPERATION_ON_SUB_MENU] as? FrequentOperationModel
            ?: return

        val data = DataOfDeletionDialog(helperForDeletionDialog, operation)
        receiverOfViewModelEvents?.receive(data)
    }

    private fun filterInStartDeletingOperation(
        intention: Intention,
    ): Boolean = Intention.START_DELETING_OPERATION == intention

    @Suppress("UNUSED_PARAMETER")
    private fun startDeletingOperation(intention: Intention) = viewModelScope.launch {
        val operation: FrequentOperationModel = cachedData[KEY_OF_OPERATION_ON_SUB_MENU] as? FrequentOperationModel
            ?: return@launch
        setLoadingV2(true)
        tryDeleteOperation(operation)
    }

    private suspend fun tryDeleteOperation(operation: FrequentOperationModel) = try {
        deleteModel.deleteTransaction(operation)
        compositeForOperationTypeSection?.remove(operation)
        val text: SpannableStringBuilder = textBuilderForSnackBar.buildTextForDeletion(operation)
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(text)
        putUiDataLaunchedByCoroutineScope()
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private fun filterInSeeMoreReceiptsClicked(entity: UiEntityOfTextButton<*>): Boolean {
        val carrier: CarrierOfFrequentOperation = entity.data as? CarrierOfFrequentOperation
            ?: return false
        return IdentifiableTextButton.SEE_MORE_RECEIPTS == carrier.eventFrom
    }

    private fun onSeeMoreReceiptsClicked(entity: UiEntityOfTextButton<*>) {
        val carrier: CarrierOfFrequentOperation = entity.data as? CarrierOfFrequentOperation
            ?: return
        startSeeingMoreReceipts(carrier.operation)
    }

    private fun startSeeingMoreReceipts(operation: FrequentOperationModel) = viewModelScope.launch {
        setLoadingV2(true)
        trySeeingMoreReceipts(operation)
    }

    private suspend fun trySeeingMoreReceipts(operation: FrequentOperationModel) = try {
        val debtWrapper: DebtWrapperModel = paymentModel.fetchDebtWrapper(operation)
        onSuccessfulDebtWrapperFetched(operation, debtWrapper)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private fun onSuccessfulDebtWrapperFetched(
        operation: FrequentOperationModel,
        debtWrapper: DebtWrapperModel,
    ) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(BillsActivity::class.java) {
            Constant.DEBT_WRAPPER_MODEL to debtWrapper
            Constant.FROM_MY_LIST to true
            Constant.FREQUENT_ID to operation.id
            Constant.COMPANY_SELECTED to FrequentOperationType.PAYMENT.actionText
            Constant.INSTITUTION_ID to operation.institutionId.orEmpty()
            Constant.SERVICE_CODE to operation.serviceCode.orEmpty()
            Constant.ZONAL_ID to operation.zonal.orEmpty()
        }
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(carrier)
    }

    private fun filterInTransferAmountClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = entity.id == idRegistry.amountButtonId
            && FrequentOperationType.TRANSFER == mainTopComposite.selectedFrequentOperationType

    @Suppress("UNUSED_PARAMETER")
    private fun onTransferAmountClicked(entity: UiEntityOfCanvasButton<*>) {
        putUiChangeEnable(false)
        receiverOfViewModelEvents?.receive(false)
    }

    private fun filterInPayAmountClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = entity.id == idRegistry.amountButtonId
            && FrequentOperationType.PAYMENT == mainTopComposite.selectedFrequentOperationType

    @Suppress("UNUSED_PARAMETER")
    private fun onPayAmountClicked(entity: UiEntityOfCanvasButton<*>) {
        putUiChangeEnable(false)
        val isPaymentEnabledWithCreditCard: Boolean = compositeForOperationTypeSection
            ?.controller
            ?.let(paymentEnablingWithCreditCard::isEnabled)
            ?: return
        receiverOfViewModelEvents?.receive(isPaymentEnabledWithCreditCard)
    }

    private fun startConfirming(carrier: CarrierOfProductPicked) = viewModelScope.launch {
        val type: FrequentOperationType = frequentOperationType ?: return@launch
        setLoadingV2(true)
        tryConfirm(carrier, type)
        receiverOfViewModelEvents?.receive(IntentionPicker.ENABLE_CANVAS_BUTTON)
    }

    private suspend fun tryConfirm(
        carrier: CarrierOfProductPicked,
        type: FrequentOperationType,
    ) = try {
        val summary: PaymentSummaryModel = confirmModel.confirm(
            originProduct = carrier.productPicked,
            numberOfInstallments = carrier.numberOfInstallments,
            operations = checkedOperationsOfTypeSection,
        )
        resetAllCheckedOperations()
        putUiDataLaunchedByCoroutineScope()
        onSuccessfulConfirmation(summary, carrier.productPicked, carrier.numberOfInstallments, type)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private fun resetAllCheckedOperations() {
        val compositeByTypeId: Map<Long, CompositeForOperationTypeSection?> = mainTopComposite.compositeByTypeId
        val composites: Collection<CompositeForOperationTypeSection?> = compositeByTypeId.values
        composites.filterNotNull().forEach(::resetCheckedOperations)
    }

    private fun resetCheckedOperations(composite: CompositeForOperationTypeSection) {
        composite.controller.reset()
    }

    private fun onSuccessfulConfirmation(
        summary: PaymentSummaryModel,
        originProduct: ProductModel,
        numberOfInstallments: Int,
        type: FrequentOperationType,
    ) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(ReceiptActivity::class.java) {
            ReceiptActivity.PARAM_SUMMARY to summary
            ReceiptActivity.PARAM_PRODUCT to originProduct
            ReceiptActivity.PARAM_INSTALLMENTS_NUMBER to numberOfInstallments
            ReceiptActivity.USER_ACCOUNT_CURRENCY to originProduct.currencyId
            ReceiptActivity.PARAM_MY_LIST_PEN_AMOUNT to amountsByTransactionalCurrency[Currency.PEN].toString()
            ReceiptActivity.PARAM_MY_LIST_USD_AMOUNT to amountsByTransactionalCurrency[Currency.USD].toString()
            ReceiptActivity.PARAM_FREQUENT_OPERATION_TYPE to type.name
            Constant.FROM_MY_LIST to true
        }
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(carrier)
    }

    override fun onCleared() {
        appModel.removeChild(id)
        super.onCleared()
    }

    private fun editCanvasButtonEnabling(enabled: Boolean) {
        anchoredBottomComposite.editCanvasButtonEnabling(
            id = idRegistry.amountButtonId,
            isEnabled = enabled
        )
    }

    private fun putUiChangeEnable(isEnabled: Boolean) = viewModelScope.launch {
        editCanvasButtonEnabling(isEnabled)
        recomposeComposite()
    }

    companion object {

        private val KEY_OF_OPERATION_ON_SUB_MENU: Int
            get() = 0
        private val DIVIDER_POSITION_FOR_SUB_MENU: Int
            get() = 0

        private val MIN_TO_ENABLE_BUTTON: Int
            get() = 1
        private val MAX_TO_ENABLE_BUTTON: Int
            get() = 10
    }
}