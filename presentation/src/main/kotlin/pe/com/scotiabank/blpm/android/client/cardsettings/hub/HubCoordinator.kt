package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import android.content.res.Resources
import androidx.core.util.Consumer
import androidx.core.util.Supplier
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
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.blankStateDuration
import pe.com.scotiabank.blpm.android.client.base.CompositeRegistry
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.UiEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEvent
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.canvasbutton.BottomComposite
import pe.com.scotiabank.blpm.android.client.base.canvassnackbar.CanvasSnackbarDataHolder
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.errorstate.CompositeOfErrorState
import pe.com.scotiabank.blpm.android.client.base.errorstate.ControllerOfErrorState
import pe.com.scotiabank.blpm.android.client.base.registry.AvailabilityRegistry
import pe.com.scotiabank.blpm.android.client.base.registry.VisitRegistry
import pe.com.scotiabank.blpm.android.client.base.state.UiState
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.base.toolbar.AppBarComposite
import pe.com.scotiabank.blpm.android.client.cardsettings.CardSettingsConstants
import pe.com.scotiabank.blpm.android.client.cardsettings.detail.EditedCard
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.CardInfoForSetting
import pe.com.scotiabank.blpm.android.client.cardsettings.travel.TravelResult
import pe.com.scotiabank.blpm.android.client.medallia.util.MedalliaConstants
import pe.com.scotiabank.blpm.android.client.newmedallia.MedalliaConfig
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.data.exception.ForceUpdateException
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.BuddyTipEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.pillbutton.UiEntityOfPillButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended.UiEntityOfDoubleEndedImage
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import java.lang.ref.WeakReference
import java.util.concurrent.CancellationException

class HubCoordinator(
    factoryOfAppBarComposite: AppBarComposite.Factory,
    factoryOfMainTopComposite: MainTopComposite.Factory,
    factoryOfErrorBottomComposite: BottomComposite.Factory,
    private val isNewDetailScreen: Boolean,
    weakResources: WeakReference<Resources?>,
    appModel: AppModel,
    private val groupModel: SuspendingFunction<Map<Long, Any?>, CardSettingHub>,
    private val detailModel: SuspendingFunction<String, Any>,
    private val textProvider: TextProvider,
    private val idRegistry: IdRegistry,
    visitRegistry: VisitRegistry,
    private val availabilityRegistry: AvailabilityRegistry,
    private val analyticConsumer: Consumer<AnalyticEventData<*>>,
    private val analyticConsumerForSuccess: Consumer<AnalyticEventData<*>>,
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
            UiEntityOfCanvasButton::class,
            InstancePredicate(::filterInClickOnRetry),
            InstanceHandler(::handleClickOnRetry)
        )
        .add(
            UiEntityOfDoubleEndedImage::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnCardDetail)
        )
        .add(
            EditedCard::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::onEditedCard)
        )
        .add(
            BuddyTipEventCarrier::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnCallNow)
        )
        .add(
            UiEntityOfPillButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnRegisterTravel)
        )
        .add(
            TravelResult::class,
            InstancePredicate(::filterInRegisteredTravel),
            InstanceHandler(::onRegisteredTravel)
        )
        .add(
            UiEntityOfTextButton::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::handleClickOnWhyRegisterTravel)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val storeOfSuspendingHandling: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            UiEvent::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::notifyUi)
        )
        .add(
            CardSettingHub::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleSuccessfulCardSettingHub)
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
            SuspendingHandlerOfInstance(::handleUnregisteredError),
        )
        .build()
    private val selfErrorReceiverOnScreenCreated: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = errorHandlingStoreOnScreenCreated,
    )

    private val errorHandlingStoreOnUiClicked: StoreOfSuspendingErrorHandling = storeBuilderOfSuspendingErrorHandling
        .setHandlerForUnregistered(
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .build()
    private val selfErrorReceiverOnUiClicked: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = errorHandlingStoreOnUiClicked,
    )

    private val appBarComposite: AppBarComposite = factoryOfAppBarComposite
        .create(
            receiver = selfReceiver,
        )
        .setHome(
            isEnabled = true,
            iconRes = com.scotiabank.canvascore.R.drawable.canvascore_icon_back,
            titleText = weakResources.get()?.getString(R.string.menu_setting_or_lock_cards).orEmpty(),
            titleAppearanceRes = com.scotiabank.canvascore.R.style.canvascore_style_subtitle2,
        )

    private val mainTopComposite: MainTopComposite = factoryOfMainTopComposite.create(selfReceiver)

    private val errorTopComposite: CompositeOfErrorState
        get() = mainTopComposite.compositeOfErrorState

    private val errorBottomComposite: BottomComposite = factoryOfErrorBottomComposite
        .create(
            receiver = selfReceiver,
            visibilitySupplierForCanvasButton = Supplier(errorTopComposite::isErrorIdleVisible),
        )
        .addCanvasButton(
            id = idRegistry.retryButtonId,
            isEnabled = true,
            text = weakResources.get()?.getString(R.string.try_again).orEmpty(),
        )

    override val compositeRegistry: CompositeRegistry = CompositeRegistry(
        toolbarComposite = appBarComposite,
        mainTopComposites = listOf(mainTopComposite),
        mainBottomComposites = listOf(errorBottomComposite),
    )

    override val medalliaConfig: MedalliaConfig = MedalliaConfig(
        dispatcherProvider = dispatcherProvider,
        appModel = appModel,
        isInterceptEnabled = true,
    )

    private val controller: ControllerOfErrorState<CardSettingHub> = ControllerOfErrorState(
        dataFunction = groupModel,
        receiverOfErrorStateEvents = selfSuspendingReceiver,
        errorReceiverOfErrorStateEvents = selfErrorReceiverOnScreenCreated,
        retryButtonId = idRegistry.retryButtonId,
        visitRegistry = visitRegistry,
        availabilityRegistry = availabilityRegistry,
        errorTopComposite = errorTopComposite,
        errorBottomComposite = errorBottomComposite,
    )

    override suspend fun start() = withContext(scope.coroutineContext) {
        showSkeletonLoading()
        getCardSettingHub()
    }

    private suspend fun showSkeletonLoading() {
        uiStateHolder.currentState = UiState.LOADING
        updateUiData()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnToolbarIcon(entity: UiEntityOfToolbar) {
        hideKeyboard()
        receiveEvent(NavigationIntention.BACK)
    }

    private suspend fun getCardSettingHub() {
        controller.tryGetting(inputData = emptyMap())
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleUnregisteredError(throwable: Throwable) {
        if (throwable is CancellationException) return
        uiStateHolder.currentState = UiState.ERROR
        controller.showErrorState()
    }

    @Suppress("UNUSED_PARAMETER")
    private suspend fun notifyUi(event: UiEvent) {
        updateUiData()
    }

    private fun filterInClickOnRetry(
        entity: UiEntityOfCanvasButton<*>,
    ): Boolean = idRegistry.retryButtonId == entity.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleClickOnRetry(entity: UiEntityOfCanvasButton<*>) = scope.launch {
        controller.retryGetting(inputData = emptyMap())
    }

    private suspend fun handleSuccessfulCardSettingHub(cardSettingHub: CardSettingHub) {
        sendAnalyticScreenEvent(cardSettingHub)
        uiStateHolder.currentState = UiState.BLANK
        updateUiData()
        delay(duration = blankStateDuration)
        uiStateHolder.currentState = UiState.SUCCESS
        cardSettingHub.groups.forEach(mainTopComposite::addAtmCardGroup)
        updateUiData()
    }

    private fun sendAnalyticScreenEvent(cardSettingHub: CardSettingHub) {
        val data: Map<String, Any?> = mapOf(
            Constant.DATA to cardSettingHub
        )
        sendAnalyticEvent(AnalyticEvent.SCREEN, data)
    }

    private fun sendAnalyticEvent(event: AnalyticEvent, data: Map<String, Any?> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        analyticConsumer.accept(eventData)
    }

    private fun handleClickOnCardDetail(entity: UiEntityOfDoubleEndedImage<*>) = scope.launch {
        val card: Card = entity.data as? Card
            ?: return@launch availabilityRegistry.setAvailabilityForAll(false)

        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.cardGroupId)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)
        mutableLiveHolder.notifyMainLoadingVisibility(true)

        if (isNewDetailScreen) {

            val cardInfo = CardInfoForSetting(
                id = card.id,
                cardType = card.cardType,
                name = card.name,
            )

            return@launch goToPersonalCardSettings(cardInfo)
        }

        tryGettingCardSettingDetail(card)
    }

    private fun goToPersonalCardSettings(cardInfo: CardInfoForSetting) {
        weakParent.get()?.receiveFromChild(cardInfo)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private suspend fun tryGettingCardSettingDetail(card: Card) = try {
        val cardDetail: Any = detailModel.apply(card.id)
        onCardSettingDetailRetrieved(cardDetail)
        availabilityRegistry.setAvailabilityForAll(true)
    } catch (throwable: Throwable) {
        selfErrorReceiverOnUiClicked.receive(throwable)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private suspend fun onCardSettingDetailRetrieved(cardDetail: Any) {
        sendClickEventForCard(cardDetail)
        weakParent.get()?.receiveFromChild(cardDetail)
    }

    private fun sendClickEventForCard(cardDetail: Any) {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.EVENT_LABEL to CardSettingsConstants.CARD_LABEL,
            CardSettingsConstants.CARD_SETTINGS_DETAIL to cardDetail,
        )
        sendAnalyticEvent(AnalyticEvent.CLICK, data)
    }

    private fun onEditedCard(editedCard: EditedCard) = scope.launch {
        sendScreenEventForEditedCard(editedCard)
        val dataHolder = CanvasSnackbarDataHolder(
            icon = com.scotiabank.icons.functional.R.drawable.ic_checkmark_default_white_18,
            message = textProvider.snackbarMessageForEditedCard,
        )
        userInterface.receive(dataHolder)
        setMedalliaCustomParam(MedalliaConstants.FLOW_CARD_SETTINGS)
    }

    private fun sendScreenEventForEditedCard(editedCard: EditedCard) {
        val data: Map<String, Any?> = mapOf(CardSettingsConstants.EDITED_CARD to editedCard)
        val eventData = AnalyticEventData(AnalyticEvent.SCREEN, data)
        analyticConsumerForSuccess.accept(eventData)
    }

    private suspend fun setMedalliaCustomParam(flow: String) {
        medalliaConfig.setCustomParameter(MedalliaConstants.CUSTOM_PARAM_FLOW, flow)
    }

    private fun handleClickOnCallNow(carrier: BuddyTipEventCarrier) = scope.launch {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.cardGroupId)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)

        val action: CardSettingAction = carrier.entity.data as? CardSettingAction
            ?: return@launch availabilityRegistry.setAvailabilityForAll(false)

        sendClickEventForOtherOptions(action.analyticLabel)
        weakParent.get()?.receiveFromChild(action)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private fun sendClickEventForOtherOptions(label: String) {
        val data: Map<String, Any?> = mapOf(AnalyticsConstant.EVENT_LABEL to label)
        sendAnalyticEvent(AnalyticEvent.CLICK, data)
    }

    private fun handleClickOnRegisterTravel(entity: UiEntityOfPillButton<*>) = scope.launch {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.cardGroupId)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)

        val action: CardSettingAction = entity.data as? CardSettingAction
            ?: return@launch availabilityRegistry.setAvailabilityForAll(false)

        sendClickEventForOtherOptions(action.analyticLabel)
        weakParent.get()?.receiveFromChild(action)
        availabilityRegistry.setAvailabilityForAll(true)
    }

    private fun filterInRegisteredTravel(
        result: TravelResult,
    ): Boolean = TravelResult.SUCCESS == result

    @Suppress("UNUSED_PARAMETER")
    private fun onRegisteredTravel(result: TravelResult) = scope.launch {
        val dataHolder = CanvasSnackbarDataHolder(
            icon = com.scotiabank.icons.functional.R.drawable.ic_checkmark_default_white_18,
            message = textProvider.snackbarMessageForRegisteredTravel,
        )
        userInterface.receive(dataHolder)
        setMedalliaCustomParam(MedalliaConstants.FLOW_TRAVEL_SETTINGS)
    }

    private fun handleClickOnWhyRegisterTravel(entity: UiEntityOfTextButton<*>) = scope.launch {
        val isAvailable: Boolean = availabilityRegistry.isAvailable(idRegistry.cardGroupId)
        if (isAvailable.not()) return@launch

        availabilityRegistry.setAvailabilityForAll(false)

        val action: CardSettingAction = entity.data as? CardSettingAction
            ?: return@launch availabilityRegistry.setAvailabilityForAll(false)

        sendClickEventForOtherOptions(action.analyticLabel)
        weakParent.get()?.receiveFromChild(action)
        availabilityRegistry.setAvailabilityForAll(true)
    }
}
