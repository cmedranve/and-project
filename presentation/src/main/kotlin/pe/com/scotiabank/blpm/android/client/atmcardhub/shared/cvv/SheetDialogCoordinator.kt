package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv

import android.content.ClipData
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyListType
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
import com.scotiabank.errorhandling.StoreOfSuspendingErrorHandling
import com.scotiabank.errorhandling.SuspendingReceiverOfError
import com.scotiabank.errorhandling.SuspendingReceivingAgentOfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet.AlertBannerInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet.CardDataComposite
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.informativebottomsheet.CvvInformativeComposite
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.informativebottomsheet.CvvInformativeStaticDataHolder
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.AtmCardAction
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.IntentionEvent
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.DataHolderOfSheetDialogDismissing
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActionDestination
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.ActionRequired
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCard
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialData
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.clipdata.ClipContent
import pe.com.scotiabank.blpm.android.client.util.clipdata.setPreviewAsSensitiveData
import pe.com.scotiabank.blpm.android.client.util.countdown.ConvenienceCountDownFactory
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.BuddyTipEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.UiEntityOfButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import java.lang.ref.WeakReference
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SheetDialogCoordinator(
    factoryOfCardDataComposite: CardDataComposite.Factory,
    factoryOfCvvInformativeComposite: CvvInformativeComposite.Factory,
    private val weakResources: WeakReference<Resources?>,
    private val mutableLiveCompoundsOfSheetDialog: MutableLiveData<List<UiCompound<*>>>,
    private val model: Model,
    private val analyticModel: AnalyticModel,
    private val idRegistry: IdRegistry,
    private val cardName: String,
    private val uriHolder: UriHolder,
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
            UiEntityOfButton::class,
            InstancePredicate(::filterInClickOnCopy),
            InstanceHandler(::handleClickOnCopy)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInShowCardDataButtonClicked),
            InstanceHandler(::handleClickOnShowCardDataButton)
        )
        .add(
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInRetryButton),
            InstanceHandler(::handleClickRetryButton)
        )
        .add(
            BuddyTipEventCarrier::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnTimerBuddyTip)
        )
        .add(
            Action::class,
            InstancePredicate(::filterInClickOnCardSettings),
            InstanceHandler(::handleClickOnCardSettings)
        )
        .add(
            Action::class,
            InstancePredicate(::filterInClickOnCallNow),
            InstanceHandler(::handleClickOnCallNow)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val storeOfSuspendingErrorHandling: StoreOfSuspendingErrorHandling = StoreOfSuspendingErrorHandling.Builder()
        .putHandlerByType(
            FinishedSessionException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .setHandlerForUnregistered(
            SuspendingHandlerOfInstance(::handleUnregisteredError),
        )
        .setHandlerForCatchingAll(
            SuspendingHandlerOfInstance(FirebaseCrashlytics.getInstance()::recordException),
        )
        .build()

    private val selfSuspendingReceiverOfError: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = storeOfSuspendingErrorHandling,
    )

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            IntentionEvent::class,
            InstancePredicate(::filterInCancelScope),
            SuspendingHandlerOfInstance(::handleCancelScope)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val cvvInformativeStaticDataHolder: CvvInformativeStaticDataHolder by lazy {
        CvvInformativeStaticDataHolder(weakResources, Runnable(::reopenCardDataBottomSheet))
    }

    private val cvvInformativeComposite: CvvInformativeComposite by lazy {
        factoryOfCvvInformativeComposite.create()
    }

    private val cardDataComposite: CardDataComposite by lazy {
        factoryOfCardDataComposite.create(selfReceiver)
    }

    private var isCardDataCurrentSheetDialog = true

    private val countDownFactory: ConvenienceCountDownFactory by lazy {
        ConvenienceCountDownFactory(
            total = TOTAL_DURATION_TIMER_IN_SECONDS.seconds,
            interval = COUNT_DOWN_INTERVAL_IN_SECONDS.seconds,
        )
    }

    private val isDecrypted: Boolean
        get() {
            val atmCardReceived: AtmCard = model.atmCardReceived ?: return false
            val decryptedData: CredentialData = atmCardReceived.credentialData
            return ActionRequired.NONE == decryptedData.actionRequired
        }

    override suspend fun start() = withContext(scope.coroutineContext) {
        mutableLiveCompoundsOfSheetDialog.value = emptyList()
        cardDataComposite.currentState = UiState.LOADING
        openCardDataBottomSheet()
        tryGettingDataToShow()
    }

    override suspend fun updateUiData() = withContext(scope.coroutineContext) {
        if (!isCardDataCurrentSheetDialog) return@withContext
        cardDataComposite.recomposeItselfIfNeeded()
        mutableLiveCompoundsOfSheetDialog.postValue(cardDataComposite.compounds)
    }

    private suspend fun openCardDataBottomSheet() {
        isCardDataCurrentSheetDialog = true
        updateUiData()

        val attributes = AttrsBodyListType(appBarTitle = cardName)
        val data = StaticDataOfBottomSheetList(attributes = attributes, isCancelable = false)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(data)
    }

    private suspend fun tryGettingDataToShow() {
        try {
            if (model.isCardAvailable.not() || model.isCardLocked || model.isPurchasesDisabled) {
                onCardValidationFail()
                return
            }
            val data: CredentialData = model.getCredentialData()
            val key: String = model.getDecryptionKey()
            val decryptedData: CredentialData = model.decryptCredentialData(data, key)
            onSuccessfulDecryption(decryptedData)
        } catch (throwable: Throwable) {
            model.isFullError = true
            analyticModel.isFullError = true
            sendAnalyticLoadEvent()
            selfSuspendingReceiverOfError.receive(throwable)
        }
    }

    private suspend fun onCardValidationFail() {
        val atmCardReceived: AtmCard = model.atmCardReceived ?: return

        model.isFullError = false
        analyticModel.isFullError = false
        analyticModel.isCvvError = model.isCvvError
        cardDataComposite.currentState = UiState.SUCCESS
        cardDataComposite.addAtmCard(atmCardReceived)
        cardDataComposite.addAlertBanner(model.state.alertBannerInfo)
        cardDataComposite.setupShowDataButton(isDecrypted.not())
        updateUiData()
        sendAnalyticLoadEvent()
    }

    private suspend fun onSuccessfulDecryption(decryptedData: CredentialData) {
        val atmCardReceived: AtmCard = model.atmCardReceived ?: return
        atmCardReceived.credentialData = decryptedData

        val countDownFactory: ConvenienceCountDownFactory = countDownFactory

        model.isFullError = false
        analyticModel.isFullError = false
        analyticModel.isCvvError = model.isCvvError
        cardDataComposite.currentState = UiState.SUCCESS
        cardDataComposite.addAtmCard(atmCardReceived)
        cardDataComposite.addAlertBanner(model.state.alertBannerInfo)
        cardDataComposite.addTimerBuddyTip(model.state.timerInfo, countDownFactory.total)
        updateUiData()
        sendAnalyticLoadEvent()
        weakParent.get()?.receiveFromChild(isDecrypted)

        val countDownFlow: Flow<Duration> = countDownFactory.create()
        countDownFlow
            .takeWhile(::isDataDecrypted)
            .onEach(::onCountDownTicked)
            .onCompletion(::onCountDownFinished)
            .flowOn(defaultDispatcher)
            .collect()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun isDataDecrypted(durationRemaining: Duration): Boolean = isDecrypted

    private fun onCountDownTicked(durationRemaining: Duration) = scope.launch {
        cardDataComposite.addTimerBuddyTip(model.state.timerInfo, durationRemaining)
        updateUiData()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onCountDownFinished(
        collector: FlowCollector<Duration>,
        throwable: Throwable?
    ) = scope.launch {
        hideData()
    }

    private suspend fun hideData() {
        val credentialDataReceived: CredentialData = model.credentialDataReceived ?: return
        model.atmCardReceived?.credentialData = credentialDataReceived
        model.atmCardReceived?.let(cardDataComposite::addAtmCard)
        cardDataComposite.addAlertBanner(model.state.alertBannerInfo)
        cardDataComposite.addTimerBuddyTip(model.state.timerInfo)
        cardDataComposite.setupShowDataButton(isDecrypted)
        updateUiData()
        weakParent.get()?.receiveFromChild(isDecrypted)
    }

    private fun filterInClickOnCopy(
        entity: UiEntityOfButton<*>,
    ): Boolean = entity.id == idRegistry.copyButtonId

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnCopy(entity: UiEntityOfButton<*>) {
        sendAnalyticClickEvent(AtmCardAnalyticLabel.COPY.value)

        val atmCardReceived: AtmCard = model.atmCardReceived ?: return

        val decryptedData: CredentialData = atmCardReceived.credentialData

        val clipData: ClipData = ClipData
            .newPlainText(decryptedData.cardNumber, decryptedData.cardNumber)
            .setPreviewAsSensitiveData()

        val message: String = weakResources.get()
            ?.getString(R.string.tip_copy_account_number)
            .orEmpty()

        val clipContent = ClipContent(data = clipData, messageForLowerVersions = message)
        userInterface.receive(clipContent)

        val data: Map<String, Any?> = createDataForAnalytic()
        sendAnalyticEvent(AnalyticEvent.SNACK_BAR, data)
    }

    private fun filterInShowCardDataButtonClicked(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = idRegistry.showDataButtonId == entity.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnShowCardDataButton(entity: UiEntityOfCanvasButton<*>) {
        scope.cancel()
        sheetDialogDismissing()
        weakParent.get()?.receiveFromChild(IntentionEvent.SHOW_CARD_DATA_AGAIN)

        val analyticLabel: String = AtmCardAnalyticLabel.SHOW_DATA.value
        sendAnalyticClickEvent(analyticLabel)
    }

    private fun filterInRetryButton(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = idRegistry.retryShowDataButtonId == entity.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickRetryButton(entity: UiEntityOfCanvasButton<*>) {
        scope.cancel()
        sheetDialogDismissing()
        weakParent.get()?.receiveFromChild(IntentionEvent.SHOW_CARD_DATA_AGAIN)

        val analyticLabel: String = AtmCardAnalyticLabel.RETRY.value
        sendAnalyticClickEvent(analyticLabel)
    }

    private fun sheetDialogDismissing() {
        mutableLiveCompoundsOfSheetDialog.value = emptyList()
        val dataHolder = DataHolderOfSheetDialogDismissing()
        userInterface.receive(dataHolder)
    }

    private fun reopenCardDataBottomSheet() = scope.launch {
        sheetDialogDismissing()
        openCardDataBottomSheet()
        sendAnalyticClickInformativePopupEvent(AtmCardAnalyticLabel.BACK.value)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnTimerBuddyTip(carrier: BuddyTipEventCarrier) = scope.launch {
        isCardDataCurrentSheetDialog = false
        sendAnalyticClickEvent(AtmCardAnalyticLabel.HOW_WORK.value)
        hideData()
        sheetDialogDismissing()

        cvvInformativeComposite.recomposeItselfIfNeeded()
        mutableLiveCompoundsOfSheetDialog.postValue(cvvInformativeComposite.compounds)
        userInterface.receive(cvvInformativeStaticDataHolder.data)

        val data: Map<String, Any?> = createDataForAnalytic()
        sendAnalyticEvent(AnalyticEvent.INFORMATIVE_POPUP, data)
    }

    private fun filterInClickOnCardSettings(
        action: Action
    ): Boolean = Action.CARD_SETTINGS == action

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnCardSettings(action: Action) = scope.launch {
        val currentAlertBannerInfo: AlertBannerInfo? = cardDataComposite.findAlertBannerInfoBy(model.state.alertBannerInfo.id)
        sendAnalyticClickEvent(label = currentAlertBannerInfo?.analyticsValue.orEmpty())

        hideData()
        scope.cancel()
        sheetDialogDismissing()
        val atmCardAction = AtmCardAction(Action.CARD_SETTINGS, model.atmCardInfo)
        weakParent.get()?.receiveFromChild(atmCardAction)
    }

    private fun filterInClickOnCallNow(
        action: Action
    ): Boolean = Action.CALL_NOW == action

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnCallNow(action: Action) = scope.launch {
        val currentAlertBannerInfo: AlertBannerInfo? = cardDataComposite.findAlertBannerInfoBy(model.state.alertBannerInfo.id)
        sendAnalyticClickEvent(label = currentAlertBannerInfo?.analyticsValue.orEmpty())

        hideData()
        scope.cancel()
        sheetDialogDismissing()
        val uri: Uri = uriHolder.callUri
        val carrier = CarrierOfActionDestination(uriDestination = uri, action = Intent.ACTION_DIAL)
        userInterface.receive(carrier)
    }

    private fun filterInCancelScope(
        event: IntentionEvent,
    ): Boolean = IntentionEvent.CANCEL_SCOPE == event

    @Suppress("UNUSED_PARAMETER")
    private fun handleCancelScope(event: IntentionEvent) {
        scope.cancel()
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleUnregisteredError(throwable: Throwable) {
        cardDataComposite.currentState = UiState.ERROR
        model.atmCardReceived?.let(cardDataComposite::addAtmCard)
        cardDataComposite.addAlertBanner(model.state.alertBannerInfo)
        cardDataComposite.addTimerBuddyTip(model.state.timerInfo)
        updateUiData()
    }

    private fun sendAnalyticLoadEvent() {
        val data: Map<String, Any?> = createDataForAnalytic()
        sendAnalyticEvent(AnalyticEvent.POPUP, data)
    }

    private fun createDataForAnalytic(): Map<String, Any?> {

        val subprocessType = when {
            model.isMainHolder -> Constant.MAIN_HOLDER_FOR_ANALYTICS
            else -> Constant.ADDITIONAL_FOR_ANALYTICS
        }

        val isAllDataVisible = isDecrypted && model.isCvvError.not()
        val questionOne = if (isAllDataVisible) AnalyticsConstant.TRUE else AnalyticsConstant.FALSE
        val questionTwo = if (model.isCardLocked) AnalyticsConstant.TRUE else AnalyticsConstant.FALSE
        val questionThree = if (model.isPurchasesDisabled) AnalyticsConstant.TRUE else AnalyticsConstant.FALSE

        return mapOf(
            AnalyticsConstant.TYPE_SUBPROCESS to subprocessType,
            AnalyticsConstant.QUESTION_ONE to questionOne,
            AnalyticsConstant.QUESTION_TWO to questionTwo,
            AnalyticsConstant.QUESTION_THREE to questionThree,
        )
    }

    private fun sendAnalyticEvent(event: AnalyticEvent, data: Map<String, Any?> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        analyticModel.sendEvent(eventData)
    }

    private fun sendAnalyticClickEvent(label: String) {
        sendAnalyticGenericClickEvent(AnalyticEvent.CLICK, label)
    }

    private fun sendAnalyticGenericClickEvent(clickEvent: AnalyticEvent, label: String) {
        val data: MutableMap<String, Any?> = createDataForAnalytic().toMutableMap()
        data[AnalyticsConstant.EVENT_LABEL] = label
        sendAnalyticEvent(clickEvent, data)
    }

    private fun sendAnalyticClickInformativePopupEvent(label: String) {
        sendAnalyticGenericClickEvent(AnalyticEvent.CLICK_INFORMATIVE_POPUP, label)
    }

    companion object {
        private const val TOTAL_DURATION_TIMER_IN_SECONDS: Int = 180
        private const val COUNT_DOWN_INTERVAL_IN_SECONDS: Int = 1
    }
}
