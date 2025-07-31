package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.flow

import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.calltoaction.CallToAction
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen.CardSettingsCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.success.SuccessCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.success.SuccessfulSaving
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared.CardSettingsEvent
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import java.lang.ref.WeakReference

class CardSettingsFlowCoordinator (
    private val hub: Hub,
    private val businessCardRepository: BusinessCardsRepository,
    private val businessOtpRepository: BusinessOtpRepository,
    private val card: AtmCardInfo,
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

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInGoToSuccess),
            SuspendingHandlerOfInstance(::handleGoToSuccess)
        )
        .add(
            CallToAction::class,
            InstancePredicate(::filterInUnderstoodAction),
            SuspendingHandlerOfInstance(::handleUnderstoodAction)
        )
        .add(
            CardSettingsEvent::class,
            InstancePredicate(::filterInReturnToCardHub),
            SuspendingHandlerOfInstance(::handleReturnToCardHub)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val settingsCoordinatorFactory: CardSettingsCoordinatorFactory by lazy {
        CardSettingsCoordinatorFactory(
            hub = hub,
            weakParent = weakSelf,
            parentScope = scope,
            businessCardsRepository = businessCardRepository,
            businessOtpRepository = businessOtpRepository,
        )
    }

    private val successCoordinatorFactory: SuccessCoordinatorFactory by lazy {
        SuccessCoordinatorFactory(
            hub = hub,
            weakParent = weakSelf,
            parentScope = scope,
        )
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        val child: Coordinator = settingsCoordinatorFactory.create(card)
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        child.start()
    }

    private fun filterInGoToSuccess(
        finishingChild: FinishingCoordinator,
    ): Boolean = SuccessfulSaving.SUCCESS_DATA == finishingChild.data

    private suspend fun handleGoToSuccess(finishingChild: FinishingCoordinator) {
        val data: SuccessfulSaving = finishingChild.data as? SuccessfulSaving ?: return

        removeChild(finishingChild.coordinator)
        val child: Coordinator = successCoordinatorFactory.create(data)
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInUnderstoodAction(
        callToAction: CallToAction,
    ): Boolean = CallToAction.UNDERSTOOD_PRIMARY.id == callToAction.id

    @Suppress("UNUSED_PARAMETER")
    private fun handleUnderstoodAction(callToAction: CallToAction) {
        receiveEvent(NavigationIntention.BACK)
    }

    private fun filterInReturnToCardHub(
        event: CardSettingsEvent,
    ): Boolean = event == CardSettingsEvent.RETURN_TO_CARD_HUB

    @Suppress("UNUSED_PARAMETER")
    private fun handleReturnToCardHub(event: CardSettingsEvent) {
        receiveEvent(NavigationIntention.BACK)
    }
}
