package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.text.SpannableStringBuilder
import androidx.core.util.Supplier
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyTextType
import com.scotiabank.enhancements.handling.HandlingStore
import com.scotiabank.enhancements.handling.InstanceHandler
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.InstanceReceivingAgent
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import com.scotiabank.errorhandling.AbstractStoreBuilderOfSuspendingErrorHandling
import com.scotiabank.errorhandling.StoreOfSuspendingErrorHandling
import com.scotiabank.errorhandling.SuspendingReceiverOfError
import com.scotiabank.errorhandling.SuspendingReceivingAgentOfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.DataForPushOtpVerification
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.EmptyAnalyticConsumer
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.canvassnackbar.CanvasSnackbarDataHolder
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActionDestination
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEvent
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEventCarrier
import pe.com.scotiabank.blpm.android.client.base.number.DoubleParser
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.base.verification.OtpVerificationEvent
import pe.com.scotiabank.blpm.android.client.base.verification.TransactionType
import pe.com.scotiabank.blpm.android.client.base.verification.business.DataForBusinessOtpVerification
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared.CardSettingsEvent
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.success.SuccessfulSaving
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushError
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushSuccess
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.data.exception.ForceUpdateException
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.BuddyTipEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEvent
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.currencyedittext.UiEntityOfCurrencyEditText
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.toggleswitch.UiEntityOfToggleSwitch
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException

class CardSettingsCoordinator(
    factoryOfAppBarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfAnchoredBottomComposite: BottomComposite.Factory,
    factoryOfErrorBottomComposite: BottomComposite.Factory,
    private val weakResources: WeakReference<Resources?>,
    private val store: CardSettingStore,
    private val uriHolder: UriHolder,
    private val model: CardSettingModel,
    private val shipmentFactory: CardSettingShipment.Factory,
    private val idRegistry: IdRegistry,
    private val doubleParser: DoubleParser,
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
), SettingsByInfoHolder by store {

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            NavigationIntention::class,
            InstancePredicate(NavigationIntention::filterInBack),
            InstanceHandler(::handleBack)
        )
        .add(
            UiEntityOfToolbar::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnToolbarIcon)
        )
        .add(
            UiEntityOfToggleSwitch::class,
            InstancePredicate(::filterInClickOnLockCardSwitch),
            InstanceHandler(::handleClickOnLockCardSwitch)
        )
        .add(
            UiEntityOfToggleSwitch::class,
            InstancePredicate(::filterInClickOnOtherSwitch),
            InstanceHandler(::handleClickOnToggleSwitch)
        )
        .add(
            UiEntityOfCurrencyEditText::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onAmountEntered)
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
            BuddyTipEventCarrier::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnBuddyTip)
        )
        .add(
            UiEntityOfText::class,
            InstancePredicate(::filterInClickOnOverdraftText),
            InstanceHandler(::handleClickOnOverdraft)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInClickOnSave),
            InstanceHandler(::handleClickOnSave)
        )
        .add(
            ModalEventCarrier::class,
            InstancePredicate(::filterOnPrimaryModalSaveChanges),
            InstanceHandler(::handleOnPrimaryModalSaveChanges)
        )
        .add(
            ModalEventCarrier::class,
            InstancePredicate(::filterOnSecondaryModalSaveChanges),
            InstanceHandler(::handleOnModalSecondarySaveChanges)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterClickInGoHome),
            InstanceHandler(::handleClickOnGoHome)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val storeOfSuspendingHandling: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            OtpVerificationEvent::class,
            InstancePredicate(::filterInOtpVerified),
            SuspendingHandlerOfInstance(::handleOtpVerified)
        )
        .add(
            OtpPushSuccess::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleOnOtpPushVerified)
        )
        .add(
            OtpPushError::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleOnOtpPushError)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = storeOfSuspendingHandling,
    )

    private val storeBuilderOfSuspendingErrorHandling: AbstractStoreBuilderOfSuspendingErrorHandling = StoreOfSuspendingErrorHandling.Builder()
        .putHandlerByType(
            FinishedSessionException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .putHandlerByType(
            ForceUpdateException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .setHandlerForCatchingAll(
            SuspendingHandlerOfInstance(FirebaseCrashlytics.getInstance()::recordException),
        )

    private val errorHandlingStoreOnScreenCreated: StoreOfSuspendingErrorHandling = storeBuilderOfSuspendingErrorHandling
        .setHandlerForUnregistered(
            SuspendingHandlerOfInstance(::handleErrorOnScreenCreated),
        )
        .build()
    private val errorReceiverOnScreenCreated: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = errorHandlingStoreOnScreenCreated,
    )

    private val errorHandlingStoreOnSavingSettings: StoreOfSuspendingErrorHandling = storeBuilderOfSuspendingErrorHandling
        .setHandlerForUnregistered(
            SuspendingHandlerOfInstance(::handleErrorOnSavingSetting),
        )
        .build()
    private val errorReceiverOnSavingSettings: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = errorHandlingStoreOnSavingSettings,
    )

    private val errorHandlingStoreOnRetryingSettings: StoreOfSuspendingErrorHandling = storeBuilderOfSuspendingErrorHandling
        .setHandlerForUnregistered(
            SuspendingHandlerOfInstance(::handleErrorOnRetryingSetting),
        )
        .build()
    private val errorReceiverOnRetryingSettings: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = errorHandlingStoreOnRetryingSettings,
    )

    private val isAppBarVisible: Boolean
        get() = uiStateHolder.isErrorVisible.not()

    private val appBarComposite: AppBarComposite = factoryOfAppBarComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplier = Supplier(::isAppBarVisible)
        )
        .setHome(
            isEnabled = true,
            iconRes = com.scotiabank.canvascore.R.drawable.canvascore_icon_back,
            titleText = weakResources.get()?.getString(R.string.my_account_configuration).orEmpty(),
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite
        .create(
            receiver = selfReceiver,
        )

    private val isSaveSettingsAllowed: Boolean
        get() = store.isSaveSettingsAllowed && uiStateHolder.isSuccessVisible

    private var isCardHubToBeRefreshed: Boolean = false
        set(newValue) {
            val previousValue: Boolean = field
            if (previousValue.not()) field = newValue
        }

    private val anchoredBottomComposite: BottomComposite = factoryOfAnchoredBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(::isSaveSettingsAllowed)
        ).addCanvasButton(
            id = idRegistry.idOfSaveButton,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.save).orEmpty(),
        )

    private val errorBottomComposite: BottomComposite = factoryOfErrorBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(uiStateHolder::isErrorVisible),
        )
        .addCanvasButton(
            id = idRegistry.idOfGoHomeButton,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.go_home).orEmpty(),
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = appBarComposite,
        mainTopComposites = listOf(mainTopComposite),
        mainBottomComposites = listOf(errorBottomComposite),
        anchoredBottomComposites = listOf(anchoredBottomComposite),
    )

    private val attrsForHowOverdraftWorks: AttrsBodyTextType by lazy {
        AttrsBodyTextType(
            headline = weakResources.get()?.getString(R.string.card_settings_overdraft_headline_tooltip).orEmpty(),
            bodyText = weakResources.get()?.getString(R.string.card_settings_overdraft_body_text_tooltip).orEmpty(),
            secondaryButtonLabel = weakResources.get()?.getString(R.string.understood).orEmpty(),
        )
    }

    private val factoryOfModalDataHolder = FactoryOfModalDataHolder(
        dispatcherProvider = dispatcherProvider,
        receiver = selfReceiver,
        weakResources = weakResources
    )

    override suspend fun start() = withContext(scope.coroutineContext) {
        tryGetSettings()
    }

    private suspend fun tryGetSettings() = try {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        model.fetchSettings()
        onSettingsRetrieved()
    } catch (throwable: Throwable) {
        errorReceiverOnScreenCreated.receive(throwable)
    }

    private suspend fun onSettingsRetrieved() {
        store.createSettingByInfo()
        addItems()
        mutableLiveHolder.notifyMainLoadingVisibility(false)
    }

    private suspend fun addItems() {
        val isTempLock: Boolean = model.settingsReceived?.isTempLock == true
        mainTopComposite.clearThenAdd(isTempLock)
        mainTopComposite.clearCards()
        store.settings.forEach(mainTopComposite::addCard)
        mainTopComposite.updateCardsAfterLocking(isTempLock)
        uiStateHolder.currentState = UiState.SUCCESS
        updateUiData()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) = scope.launch {
        handleReturnToHub()
    }

    private suspend fun handleReturnToHub() {
        hideKeyboard()
        if (isSaveSettingsAllowed) return showSaveChangesMessage()
        notifyCardHubRefreshing()
        weakParent.get()?.receiveFromChild(CardSettingsEvent.RETURN_TO_CARD_HUB)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleBack(intention: NavigationIntention) = scope.launch {
        handleReturnToHub()
    }

    private fun notifyCardHubRefreshing() {
        if (isCardHubToBeRefreshed.not()) return
        weakParent.get()?.receiveFromChild(CardSettingsEvent.CARD_SETTING_CHANGED)
    }

    private suspend fun showSaveChangesMessage() {
        val saveDataHolder: ModalDataHolder = factoryOfModalDataHolder.createBy(ModalData.SAVE_CHANGES)
        userInterface.receive(saveDataHolder)
    }

    private fun filterInClickOnLockCardSwitch(
        entity: UiEntityOfToggleSwitch<*>,
    ): Boolean = entity.id == CardSettingInfo.TEMPORARILY_LOCKING.switchId

    private fun handleClickOnLockCardSwitch(entity: UiEntityOfToggleSwitch<*>) = scope.launch {
        mainTopComposite.updateCardsAfterLocking(isLocked = entity.isChecked)
        updateCardSettings(entity)
    }

    private suspend fun updateCardSettings(entity: UiEntityOfToggleSwitch<*>) {
        hideKeyboard()
        mainTopComposite.editCard(entity.id, entity.isChecked)
        updateUiData()
    }

    private fun filterInClickOnOtherSwitch(
        entity: UiEntityOfToggleSwitch<*>,
    ): Boolean = entity.id != CardSettingInfo.TEMPORARILY_LOCKING.switchId

    private fun handleClickOnToggleSwitch(entity: UiEntityOfToggleSwitch<*>) = scope.launch {
        updateCardSettings(entity)
    }

    private fun clearErrorText(limit: EditableLimit, entity: UiEntityOfCurrencyEditText<*>) {
        entity.errorText = String.EMPTY
        entity.supplementaryText = limit.createSupplementaryText(doubleParser.numberFormat)
    }

    private fun onAmountEntered(entity: UiEntityOfCurrencyEditText<*>) = scope.launch {
        val limit: EditableLimit = entity.data as? EditableLimit ?: return@launch
        val amount: Double = doubleParser.parse(entity.text)
        limit.setAmount(amount)

        val isAllowed: Boolean = limit.isAllowed()
        if (isAllowed) clearErrorText(limit, entity) else setErrorText(limit, entity)
        updateUiData()
    }

    private fun setErrorText(limit: EditableLimit, entity: UiEntityOfCurrencyEditText<*>) {
        entity.errorText = limit.createErrorText(doubleParser.numberFormat)
        entity.supplementaryText = String.EMPTY
    }

    private fun filterInImeAction(
        carrier: InputEventCarrier<*, *>,
    ): Boolean = InputEvent.IME_ACTION_PRESSED == carrier.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleImeAction(carrier: InputEventCarrier<*, *>) {
        hideKeyboard()
    }

    private fun filterInBackKeyPressedWhenEditTextFocused(
        carrier: InputEventCarrier<*, *>,
    ): Boolean = InputEvent.BACK_KEY_PRESSED_WHEN_EDIT_TEXT_FOCUSED == carrier.event

    @Suppress("UNUSED_PARAMETER")
    private fun handleBackKeyPressedWhenEditTextFocused(carrier: InputEventCarrier<*, *>) {
        hideKeyboard()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnBuddyTip(eventCarrier: BuddyTipEventCarrier) {
        hideKeyboard()
        val uri: Uri = uriHolder.callUri
        val carrierOfActionDestination = CarrierOfActionDestination(
            uriDestination = uri,
            action = Intent.ACTION_DIAL,
        )
        userInterface.receive(carrierOfActionDestination)
    }

    private fun filterInClickOnOverdraftText(entity: UiEntityOfText): Boolean {
        val setting = entity.data as? Setting ?: return false
        return setting.info.cardId == CardSettingInfo.OVERDRAFT.cardId
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnOverdraft(
        entity: UiEntityOfText,
    ) {
        hideKeyboard()
        userInterface.receive(attrsForHowOverdraftWorks)
    }

    private fun filterInClickOnSave(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = entity.id == idRegistry.idOfSaveButton

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnSave(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        hideKeyboard()
        attemptToSaveCardSettings()
    }

    private suspend fun attemptToSaveCardSettings() {
        val isPushOtpEnabled: Boolean = model.pushOtpFlowChecker.isPushOtpEnabled
        val isOtpRequired: Boolean = store.isOtpRequiredForSaving

        when {
            isPushOtpEnabled && isOtpRequired -> requirePushOtpVerification()
            isPushOtpEnabled.not() && isOtpRequired -> tryRequestOtp()
            else -> attemptToSendSettings(authId = String.EMPTY, authTracking = String.EMPTY)
        }
    }

    private fun requirePushOtpVerification() {
        val data = DataForPushOtpVerification(
            transactionId = model.operationId,
            analyticConsumer = EmptyAnalyticConsumer,
            analyticAdditionalData = Unit,
        )
        weakParent.get()?.receiveFromChild(data)
    }

    private suspend fun attemptToSendSettings(authId: String, authTracking: String) {
        val operationId: String = model.operationId
        val shipment: CardSettingShipment = shipmentFactory.attemptCreate(operationId)
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        trySendingSettings(authId = authId, authTracking = authTracking, shipment = shipment)
    }

    private suspend fun tryRequestOtp() = try {
        model.requestOtp()
        onSuccessfulOtpRequest()
    } catch (throwable: Throwable) {
        errorReceiverOnSavingSettings.receive(throwable)
    }

    private fun onSuccessfulOtpRequest() {
        mutableLiveHolder.anchoredBottom.postValue(emptyList())
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        val operationId: String = model.operationId
        val data = DataForBusinessOtpVerification(
            titleResId = R.string.my_account_configuration,
            transactionId = operationId,
            transactionType = TransactionType.SETTINGS,
        )
        weakParent.get()?.receiveFromChild(data)
    }

    private fun filterInOtpVerified(
        event: OtpVerificationEvent,
    ): Boolean = OtpVerificationEvent.ON_BUSINESS_CARD_OTP_VERIFIED == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleOtpVerified(event: OtpVerificationEvent) {
        attemptToSendSettings(authId = String.EMPTY, authTracking = String.EMPTY)
    }

    private suspend fun handleOnOtpPushVerified(data: OtpPushSuccess) {
        attemptToSendSettings(authId = data.authId, authTracking = data.authTracking)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOnOtpPushError(data: OtpPushError) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private suspend fun trySendingSettings(
        authId: String,
        authTracking: String,
        shipment: CardSettingShipment
    ) = try {
        model.updateSettings(
            authId = authId,
            authTracking = authTracking,
            requestEntity = shipment.requestEntity,
        )
        isCardHubToBeRefreshed = store.isTempLockChanged
        retrySettings()
    } catch (throwable: Throwable) {
        errorReceiverOnSavingSettings.receive(throwable)
    }

    private suspend fun retrySettings() = try {
        model.fetchSettings()
        showSnackbar()
        onSettingsRetrieved()
    } catch (throwable: Throwable) {
        errorReceiverOnRetryingSettings.receive(throwable)
    }

    private fun showSnackbar() {
        val message: SpannableStringBuilder = store.createSnackbarText()
        val dataHolder = CanvasSnackbarDataHolder(
            icon = com.scotiabank.icons.functional.R.drawable.ic_checkmark_default_white_18,
            message = message,
            receiver = selfReceiver,
            id = randomLong(),
        )
        userInterface.receive(dataHolder)
    }

    private suspend fun handleErrorOnScreenCreated(throwable: Throwable) {
        if (throwable is CancellationException) return
        uiStateHolder.currentState = UiState.ERROR
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        updateUiData()
    }

    private suspend fun handleErrorOnSavingSetting(throwable: Throwable) {
        if (throwable is CancellationException) return
        val errorDataHolder: ModalDataHolder = factoryOfModalDataHolder.createBy(ModalData.ERROR)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(errorDataHolder)
    }

    private fun handleErrorOnRetryingSetting(throwable: Throwable) {
        if (throwable is CancellationException) return
        notifyCardHubRefreshing()
        mutableLiveHolder.anchoredBottom.postValue(emptyList())
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        finishAndGoToSuccess()
    }

    private fun finishAndGoToSuccess() {
        val finishingCoordinator = FinishingCoordinator(
            data = SuccessfulSaving.SUCCESS_DATA,
            coordinator = this,
        )
        weakParent.get()?.receiveFromChild(finishingCoordinator)
    }

    private fun filterOnPrimaryModalSaveChanges(carrier: ModalEventCarrier): Boolean {
        val event: ModalEvent = carrier.event
        if (ModalEvent.PRIMARY_CLICKED != event) return false

        val dataHolder: ModalDataHolder = carrier.dataHolder
        return ModalData.SAVE_CHANGES.id == dataHolder.id
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOnPrimaryModalSaveChanges(carrier: ModalEventCarrier) = scope.launch {
        attemptToSaveCardSettings()
    }

    private fun filterOnSecondaryModalSaveChanges(carrier: ModalEventCarrier): Boolean {
        val event: ModalEvent = carrier.event
        if (ModalEvent.SECONDARY_CLICKED != event) return false

        val dataHolder: ModalDataHolder = carrier.dataHolder
        return ModalData.SAVE_CHANGES.id == dataHolder.id
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOnModalSecondarySaveChanges(carrier: ModalEventCarrier) {
        notifyCardHubRefreshing()
        weakParent.get()?.receiveFromChild(CardSettingsEvent.RETURN_TO_CARD_HUB)
    }

    private fun filterClickInGoHome(entity: UiEntityOfCanvasButton<*>) = entity.id == idRegistry.idOfGoHomeButton

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnGoHome(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        userInterface.receive(NavigationIntention.CLOSE)
    }
}