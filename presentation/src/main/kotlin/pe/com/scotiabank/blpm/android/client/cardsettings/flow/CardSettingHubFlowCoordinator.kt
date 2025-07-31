package pe.com.scotiabank.blpm.android.client.cardsettings.flow

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.scotiabank.enhancements.handling.InstancePredicate
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.handling.StoreOfSuspendingHandling
import com.scotiabank.enhancements.handling.SuspendingHandlerOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceiverOfInstance
import com.scotiabank.enhancements.handling.SuspendingReceivingAgentOfInstance
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.DataForPushOtpVerification
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.InformationActivity
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActionDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.permission.CarrierOfPermissionResult
import pe.com.scotiabank.blpm.android.client.base.permission.PermissionResult
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.cardsettings.CardSettingsConstants
import pe.com.scotiabank.blpm.android.client.cardsettings.hub.CardSettingAction
import pe.com.scotiabank.blpm.android.client.cardsettings.hub.HubCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.CardSettingsActivity
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.CardInfoForSetting
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.personal.flow.CardSettingsFlowCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.cardsettings.travel.CardSettingsTravelChooseDateActivity
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.model.CardDetailModel
import pe.com.scotiabank.blpm.android.client.nosession.documentobjectidentifier.UriHolder
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.PushKeyCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.error.OtpErrorType
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushError
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushSuccess
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.notification.turnon.NotificationPermissionCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.tasknav.clearThenNavigateToHost
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.permissions.NotificationsUtil
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class CardSettingHubFlowCoordinator(
    private val hub: Hub,
    private val appModel: AppModel,
    private val retrofit: Retrofit,
    private val uriHolder: UriHolder,
    private val textProvider: TextProvider,
    private val weakAppContext: WeakReference<Context?>,
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
            CardDetailModel::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleCardDetail)
        )
        .add(
            CardInfoForSetting::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleCard)
        )
        .add(
            CardSettingAction::class,
            InstancePredicate(::filterInCallNow),
            SuspendingHandlerOfInstance(::handleCallNow)
        )
        .add(
            CardSettingAction::class,
            InstancePredicate(::filterInRegisterTravel),
            SuspendingHandlerOfInstance(::handleRegisterTravel)
        )
        .add(
            CardSettingAction::class,
            InstancePredicate(::filterInWhyDoIHaveToRegister),
            SuspendingHandlerOfInstance(::handleWhyDoIHaveToRegister)
        )
        .add(
            DataForPushOtpVerification::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleRequiringOtpPushVerification)
        )
        .add(
            CarrierOfPermissionResult::class,
            InstancePredicate(::filterInNotificationPermissionDenied),
            SuspendingHandlerOfInstance(::handleNotificationPermissionDenied)
        )
        .add(
            CarrierOfPermissionResult::class,
            InstancePredicate(::filterInNotificationPermissionGranted),
            SuspendingHandlerOfInstance(::handleNotificationPermissionGranted)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOtpPushDismissed),
            SuspendingHandlerOfInstance(::handleOtpPushDismissed)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOtpPushGenericError),
            SuspendingHandlerOfInstance(::handleOtpPushGenericError)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInOtpPushMaxAttemptsError),
            SuspendingHandlerOfInstance(::handleOtpPushMaxAttemptsError)
        )
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInSuccessOtpPush),
            SuspendingHandlerOfInstance(::handleSuccessOtpPush)
        )
        .build()
    override val selfSuspendingReceiver: SuspendingReceiverOfInstance = SuspendingReceivingAgentOfInstance(
        store = suspendingHandlingStore,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val hubFactory: HubCoordinatorFactory by lazy {
        HubCoordinatorFactory(
            hub = hub,
            retrofit = retrofit,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }
    
    private val newSettingsFactory: CardSettingsFlowCoordinatorFactory by lazy {
        CardSettingsFlowCoordinatorFactory(
            hub = hub,
            retrofit = retrofit,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val notificationPermissionCoordinatorFactory: NotificationPermissionCoordinatorFactory by lazy {
        NotificationPermissionCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            weakParent = weakSelf,
        )
    }

    private val pushKeyCoordinatorFactory by lazy {
        PushKeyCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            embeddedDataName = Constant.ANALYTICS_PUSH_DATA,
            weakParent = weakSelf,
            hideErrorPage = false,
        )
    }

    override suspend fun start() = withContext(scope.coroutineContext) {
        val child: Coordinator = hubFactory.create()
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        child.start()
    }

    private suspend fun handleCardDetail(cardDetail: CardDetailModel) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = CardSettingsActivity::class.java,
        ) {
            CardSettingsConstants.CARD_SETTINGS_DETAIL to cardDetail
        }

        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(carrier)
    }

    private suspend fun handleCard(cardInfo: CardInfoForSetting) {
        val child: Coordinator = newSettingsFactory.create(cardInfo)
        addChild(child)
        child.start()
    }

    private fun filterInCallNow(
        action: CardSettingAction,
    ): Boolean = CardSettingAction.CALL_NOW == action

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleCallNow(action: CardSettingAction) {
        val uri: Uri = uriHolder.callUri
        val carrier = CarrierOfActionDestination(uriDestination = uri, action = Intent.ACTION_DIAL)
        userInterface.receive(carrier)
    }

    private fun filterInRegisterTravel(
        action: CardSettingAction,
    ): Boolean = CardSettingAction.REGISTER_TRAVEL == action

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleRegisterTravel(action: CardSettingAction) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = CardSettingsTravelChooseDateActivity::class.java,
        )
        userInterface.receive(carrier)
    }

    private fun filterInWhyDoIHaveToRegister(
        action: CardSettingAction,
    ): Boolean = CardSettingAction.WHY_DO_I_HAVE_TO_REGISTER_TRAVEL == action

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleWhyDoIHaveToRegister(action: CardSettingAction) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(
            screenDestination = InformationActivity::class.java,
        ) {
            InformationActivity.INFORMATION_TITLE to textProvider.informationScreenTitle
            InformationActivity.INFORMATION_MESSAGE to textProvider.informationScreenMessage
        }
        userInterface.receive(carrier)
    }

    private suspend fun handleRequiringOtpPushVerification(data: DataForPushOtpVerification) {
        val isNotificationsEnabled: Boolean = NotificationsUtil.areNotificationsEnabled(weakAppContext)
        if (isNotificationsEnabled) goToOtpPushVerification(data) else goToNotificationPermission(data)
    }

    private suspend fun goToNotificationPermission(data: DataForPushOtpVerification) {
        val child: Coordinator = notificationPermissionCoordinatorFactory.create(data)
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
    }

    private fun filterInNotificationPermissionDenied(
        carrier: CarrierOfPermissionResult,
    ): Boolean = PermissionResult.NOT_GRANTED == carrier.result

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleNotificationPermissionDenied(carrier: CarrierOfPermissionResult) {
        mutableLiveHolder.notifyMainLoadingVisibility(true)
        appModel.receive(SessionEvent.ENDING)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        weakAppContext.get()?.let(::clearThenNavigateToHost)
    }

    private fun filterInNotificationPermissionGranted(
        carrier: CarrierOfPermissionResult,
    ): Boolean = PermissionResult.GRANTED == carrier.result

    private suspend fun handleNotificationPermissionGranted(carrier: CarrierOfPermissionResult) {
        val data: DataForPushOtpVerification = carrier.dataFromConsumer as? DataForPushOtpVerification
            ?: return
        removeChild(currentChild)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        goToOtpPushVerification(data)
    }

    private suspend fun goToOtpPushVerification(data: DataForPushOtpVerification) {
        val child = pushKeyCoordinatorFactory.create(
            transactionId = data.transactionId,
            analyticConsumer = data.analyticConsumer,
            analyticAdditionalData = data.analyticAdditionalData,
            dataFromConsumer = Unit,
        )
        addChild(child)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        child.start()
    }

    private fun filterInOtpPushDismissed(finishingChild: FinishingCoordinator): Boolean {
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInNone(type = otpPushError.otpError)
    }

    private suspend fun handleOtpPushDismissed(finishingCoordinator: FinishingCoordinator) {
        removeChild(finishingCoordinator.coordinator)
    }

    private fun filterInOtpPushGenericError(finishingChild: FinishingCoordinator): Boolean{
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInSomethingWentWrong(type = otpPushError.otpError)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOtpPushGenericError(finishingCoordinator: FinishingCoordinator) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInOtpPushMaxAttemptsError(finishingChild: FinishingCoordinator): Boolean{
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInMaxAttempts(type = otpPushError.otpError)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOtpPushMaxAttemptsError(finishingCoordinator: FinishingCoordinator) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInSuccessOtpPush(
        finishingChild: FinishingCoordinator
    ): Boolean = finishingChild.data is OtpPushSuccess

    private suspend fun handleSuccessOtpPush(finishingCoordinator: FinishingCoordinator) {
        val data: OtpPushSuccess = finishingCoordinator.data as? OtpPushSuccess ?: return
        removeChild(currentChild)
        currentDeepChild.receiveFromAncestor(data)
    }
}
