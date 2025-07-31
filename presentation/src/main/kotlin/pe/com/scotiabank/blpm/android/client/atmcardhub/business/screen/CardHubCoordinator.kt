package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import android.content.res.Resources
import androidx.core.util.Supplier
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.cvv.SheetDialogCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.DataForPushOtpVerification
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.SheetDialogCoordinator
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.intro.DataStore
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.AtmCardAction
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.IntentionEvent
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.EmptyAnalyticConsumer
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardStatus
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.base.verification.OtpVerificationEvent
import pe.com.scotiabank.blpm.android.client.base.verification.TransactionType
import pe.com.scotiabank.blpm.android.client.base.verification.business.DataForBusinessOtpVerification
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushSuccess
import pe.com.scotiabank.blpm.android.client.base.blankStateDuration
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.data.exception.ForceUpdateException
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException

class CardHubCoordinator(
    factoryOfAppBarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfMainBottomComposite: BottomComposite.Factory,
    weakResources: WeakReference<Resources?>,
    titleText: String,
    private val sheetDialogCoordinatorFactory: SheetDialogCoordinatorFactory,
    private val idRegistry: IdRegistry,
    private val model: CardHubModel,
    private val dataStore: DataStore,
    weakParent: WeakReference<out Coordinator?>,
    scope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    mutableLiveHolder: MutableLiveHolder,
    userInterface: InstanceReceiver,
    uiStateHolder: UiStateHolderWithErrorType,
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
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInClickOnShowCardData),
            InstanceHandler(::handleClickOnShowCardData)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInClickOnSettings),
            InstanceHandler(::handleClickOnSettings)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInRetryHub),
            InstanceHandler(::handleRetryHub)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInGoToHome),
            InstanceHandler(::handleGoToHome)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInShowCardDataAgain),
            SuspendingHandlerOfInstance(::handleShowCardDataAgain)
        )
        .add(
            OtpVerificationEvent::class,
            InstancePredicate(::filterInOtpVerified),
            SuspendingHandlerOfInstance(::handleOnOtpVerified)
        )
        .add(
            OtpPushSuccess::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleOnOtpPushVerified)
        )
        .add(
            AtmCardAction::class,
            InstancePredicate(::filterInCardSettings),
            SuspendingHandlerOfInstance(::handleCardSettings)
        )
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInRefreshCardHub),
            SuspendingHandlerOfInstance(::handleRefreshCardHub)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
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
            SuspendingHandlerOfInstance(::handleRetryableError),
        )
        .build()
    private val selfErrorReceiverOnScreenCreated: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = errorHandlingStoreOnScreenCreated,
    )

    private val errorHandlingStoreOnUiClicked: StoreOfSuspendingErrorHandling = storeBuilderOfSuspendingErrorHandling
        .setHandlerForUnregistered(
            SuspendingHandlerOfInstance(::handleTerminalError),
        )
        .build()
    private val selfErrorReceiverOnUiClicked: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = errorHandlingStoreOnUiClicked,
    )

    private val appBarComposite: AppBarComposite = factoryOfAppBarComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplier = Supplier(uiStateHolder::isAppBarVisible)
        )
        .setHome(
            isEnabled = true,
            iconRes = com.scotiabank.canvascore.R.drawable.canvascore_icon_back,
            titleText = titleText,
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create(selfReceiver)

    private val bottomCompositeOfRetryableError: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(uiStateHolder::isRetryableErrorVisible),
        )
        .addCanvasButton(
            id = idRegistry.idOfRetryHubButton,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.hub_partial_error_button).orEmpty(),
        )

    private val bottomCompositeOfBlockingError: BottomComposite = factoryOfMainBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(uiStateHolder::isBlockingErrorVisible),
        )
        .addCanvasButton(
            id = idRegistry.idOfGoToHomeButton,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.go_home).orEmpty(),
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = appBarComposite,
        mainTopComposites = listOf(mainTopComposite),
        mainBottomComposites = listOf(bottomCompositeOfRetryableError, bottomCompositeOfBlockingError),
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)
    private var sheetDialogCoordinator: SheetDialogCoordinator? = null

    private val _liveCompoundsOfSheetDialog: MutableLiveData<List<UiCompound<*>>> = MutableLiveData()
    override val liveCompoundsOfSheetDialog: LiveData<List<UiCompound<*>>>
        get() = _liveCompoundsOfSheetDialog

    override suspend fun start() = withContext(scope.coroutineContext) {
        showSkeletonLoading()
        tryGetCards()
    }

    private suspend fun showSkeletonLoading() {
        uiStateHolder.currentState = UiState.LOADING
        mainTopComposite.compositeForCreditCardSection.currentState = UiState.LOADING
        mainTopComposite.compositeForDebitCardSection.currentState = UiState.LOADING
        updateUiData()
    }

    private suspend fun tryGetCards() = try {
        val cards: List<AtmCardInfo> = model.getCards()
        onCardsRetrieved(cards)
    } catch (throwable: Throwable) {
        selfErrorReceiverOnScreenCreated.receive(throwable)
    }

    private suspend fun onCardsRetrieved(cards: List<AtmCardInfo>) {
        showBlankScreen()

        val creditCards: List<AtmCardInfo> = cards.filter(::isCreditCard)
        mainTopComposite.compositeForCreditCardSection.clearThenAdd(creditCards)
        mainTopComposite.compositeForCreditCardSection.currentState = UiState.from(creditCards.size)

        val debitCards: List<AtmCardInfo> = cards.filter(::isDebitCard)
        mainTopComposite.compositeForDebitCardSection.clearThenAdd(debitCards)
        mainTopComposite.compositeForDebitCardSection.currentState = UiState.from(debitCards.size)

        uiStateHolder.currentState = UiState.from(cards.size)
        updateUiData()

        showCvvIntro(cards)
    }

    private suspend fun showBlankScreen() {
        uiStateHolder.currentState = UiState.BLANK
        mainTopComposite.compositeForCreditCardSection.currentState = UiState.BLANK
        mainTopComposite.compositeForDebitCardSection.currentState = UiState.BLANK
        updateUiData()
        delay(duration = blankStateDuration)
    }

    private fun isCreditCard(card: AtmCardInfo): Boolean = card.atmCard.type == AtmCardType.CREDIT

    private fun isDebitCard(card: AtmCardInfo): Boolean = card.atmCard.type == AtmCardType.DEBIT

    @Suppress("RedundantSuspendModifier")
    private suspend fun showCvvIntro(cards: List<AtmCardInfo>) {

        if (dataStore.isCvvOnboardingWasShown) return

        val isAnyActiveCard: Boolean = cards.any(::isActiveCard)
        if (isAnyActiveCard.not()) return

        weakParent.get()?.receiveFromChild(IntentionEvent.GO_TO_CVV_INTRO)
    }

    private fun isActiveCard(card: AtmCardInfo): Boolean = card.atmCard.status == AtmCardStatus.ACTIVE

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) {
        receiveEvent(NavigationIntention.BACK)
    }

    private fun filterInClickOnShowCardData(entity: UiEntityOfTextButton<*>): Boolean {
        val atmCardAction: AtmCardAction = entity.data as? AtmCardAction ?: return false
        val action: Action = atmCardAction.action
        return Action.SHOW_CARD_DATA == action
    }

    private fun handleClickOnShowCardData(entity: UiEntityOfTextButton<*>) = scope.launch {
        sheetDialogCoordinator?.receiveFromAncestor(IntentionEvent.CANCEL_SCOPE)
        val atmCardAction: AtmCardAction = entity.data as? AtmCardAction ?: return@launch
        val card: AtmCardInfo = atmCardAction.data as? AtmCardInfo ?: return@launch
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        model.currentCard = card

        val cardId: String = card.cardId
        tryGetCardSettings(cardId)
    }

    private suspend fun tryGetCardSettings(cardId: String) = try {
        val cardSettings: CardSettings = model.getCardSettingsDetail(cardId)
        checkCardSettingFlags(cardSettings)
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private suspend fun checkCardSettingFlags(cardSettings: CardSettings) {
        val isTempLock: Boolean = cardSettings.isTempLock
        val isPurchasesDisabled: Boolean = cardSettings.isOnlinePurchase.not()

        if (isTempLock || isPurchasesDisabled) {
            mutableLiveHolder.notifyMainLoadingVisibility(false)

            val atmCardInfo: AtmCardInfo = model.currentCard ?: return
            goToCardData(atmCardInfo)
            return
        }

        fetchOperationId()
    }

    private suspend fun fetchOperationId() = try {
        model.fetchOperationId()
        onSuccessFetchOperationId()
    } catch (throwable: Throwable) {
        selfErrorReceiverOnUiClicked.receive(throwable)
    }

    private suspend fun onSuccessFetchOperationId() {
        val card: AtmCardInfo = model.currentCard ?: return

        if (model.pushOtpFlowChecker.isPushOtpEnabled) {
            requirePushOtpVerification(card)
            return
        }
        trySendOtp(card)
    }

    private fun requirePushOtpVerification(card: AtmCardInfo) {
        val data = DataForPushOtpVerification(
            transactionId = card.operationId,
            analyticConsumer = EmptyAnalyticConsumer,
            analyticAdditionalData = Unit,
        )
        weakParent.get()?.receiveFromChild(data)
    }

    private suspend fun trySendOtp(card: AtmCardInfo) = try {
        model.requestOtp(card)
        requireOtpVerification(card)
    } catch (throwable: Throwable) {
        selfErrorReceiverOnUiClicked.receive(throwable)
    }

    private fun requireOtpVerification(card: AtmCardInfo) {
        val data = DataForBusinessOtpVerification(
            transactionId = card.operationId,
            transactionType = TransactionType.CVV,
        )
        weakParent.get()?.receiveFromChild(data)
    }

    private fun filterInClickOnSettings(entity: UiEntityOfTextButton<*>): Boolean {
        val atmCardAction: AtmCardAction = entity.data as? AtmCardAction ?: return false
        return atmCardAction.action == Action.CARD_SETTINGS
    }

    private fun handleClickOnSettings(entity: UiEntityOfTextButton<*>) = scope.launch {
        val atmCardAction: AtmCardAction = entity.data as? AtmCardAction ?: return@launch
        val card: AtmCardInfo = atmCardAction.data as? AtmCardInfo ?: return@launch
        weakParent.get()?.receiveFromChild(card)
    }

    private fun filterInShowCardDataAgain(
        event: IntentionEvent,
    ): Boolean = IntentionEvent.SHOW_CARD_DATA_AGAIN == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleShowCardDataAgain(event: IntentionEvent) {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        fetchOperationId()
    }

    private fun filterInOtpVerified(
        event: OtpVerificationEvent,
    ): Boolean = OtpVerificationEvent.ON_BUSINESS_CARD_OTP_VERIFIED == event

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleOnOtpVerified(event: OtpVerificationEvent) {
        model.currentCard?.authId = String.EMPTY
        model.currentCard?.authTracking = String.EMPTY
        val atmCardInfo: AtmCardInfo = model.currentCard ?: return

        goToCardData(atmCardInfo)
    }

    private suspend fun goToCardData(atmCardInfo: AtmCardInfo) {
        sheetDialogCoordinator = sheetDialogCoordinatorFactory.create(
            atmCardInfo = atmCardInfo,
            mutableLiveCompoundsOfSheetDialog = _liveCompoundsOfSheetDialog,
            weakParent = weakSelf,
        )
        sheetDialogCoordinator?.start()
    }

    private suspend fun handleOnOtpPushVerified(data: OtpPushSuccess) {
        model.currentCard?.authId = data.authId
        model.currentCard?.authTracking = data.authTracking
        val atmCardInfo: AtmCardInfo = model.currentCard ?: return

        goToCardData(atmCardInfo)
    }

    private fun filterInRetryHub(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = idRegistry.idOfRetryHubButton == entity.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleRetryHub(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        start()
    }

    private fun filterInGoToHome(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = idRegistry.idOfGoToHomeButton == entity.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleGoToHome(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInCardSettings(
        atmCardAction: AtmCardAction,
    ): Boolean = Action.CARD_SETTINGS == atmCardAction.action

    private fun handleCardSettings(atmCardAction: AtmCardAction) {
        val card: AtmCardInfo = atmCardAction.data as? AtmCardInfo ?: return
        weakParent.get()?.receiveFromChild(card)
    }

    private fun filterInRefreshCardHub(
        event: IntentionEvent,
    ): Boolean = event == IntentionEvent.REFRESH_CARD_HUB

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleRefreshCardHub(event: IntentionEvent) {
        start()
    }

    private suspend fun handleRetryableError(throwable: Throwable) {
        if (throwable is CancellationException) return
        showErrorScreen(errorType = ErrorType.RETRYABLE)
    }

    private suspend fun showErrorScreen(errorType: ErrorType) {
        mainTopComposite.errorType = errorType
        uiStateHolder.currentState = UiState.ERROR
        mainTopComposite.compositeForCreditCardSection.currentState = UiState.ERROR
        mainTopComposite.compositeForDebitCardSection.currentState = UiState.ERROR
        updateUiData()
    }

    private suspend fun handleTerminalError(throwable: Throwable) {
        if (throwable is CancellationException) return
        showErrorScreen(errorType = ErrorType.BLOCKING)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
    }
}
