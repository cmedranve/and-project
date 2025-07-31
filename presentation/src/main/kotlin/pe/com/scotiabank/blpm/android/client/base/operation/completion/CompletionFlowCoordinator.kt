package pe.com.scotiabank.blpm.android.client.base.operation.completion

import android.content.Context
import android.content.res.Resources
import androidx.core.util.Consumer
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
import com.scotiabank.errorhandling.StoreOfSuspendingErrorHandling
import com.scotiabank.errorhandling.SuspendingReceiverOfError
import com.scotiabank.errorhandling.SuspendingReceivingAgentOfError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.operation.token.validation.ValidationInformation
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.Coordinator
import pe.com.scotiabank.blpm.android.client.base.CoordinatorImpl
import pe.com.scotiabank.blpm.android.client.base.FinishingCoordinator
import pe.com.scotiabank.blpm.android.client.base.MutableLiveHolder
import pe.com.scotiabank.blpm.android.client.base.NavigationIntention
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEvent
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalEventCarrier
import pe.com.scotiabank.blpm.android.client.base.operation.confirmation.carrier.CarrierForConfirmation
import pe.com.scotiabank.blpm.android.client.base.operation.confirmation.carrier.CarrierFromConfirmationConsumer
import pe.com.scotiabank.blpm.android.client.base.operation.confirmation.screen.ConfirmationCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapter
import pe.com.scotiabank.blpm.android.client.base.permission.CarrierOfPermissionResult
import pe.com.scotiabank.blpm.android.client.base.permission.PermissionResult
import pe.com.scotiabank.blpm.android.client.base.receipt.ReceiptActivity
import pe.com.scotiabank.blpm.android.client.base.session.SessionEvent
import pe.com.scotiabank.blpm.android.client.base.state.UiStateHolder
import pe.com.scotiabank.blpm.android.client.host.shared.Hub
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.PushKeyCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.error.OtpErrorType
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushError
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.digitaltoken.shared.OtpPushSuccess
import pe.com.scotiabank.blpm.android.client.nosession.newenrollment.notification.turnon.NotificationPermissionCoordinatorFactory
import pe.com.scotiabank.blpm.android.client.tasknav.clearThenNavigateToHost
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.permissions.NotificationsUtil
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.data.exception.ForceUpdateException
import pe.com.scotiabank.blpm.android.data.exception.GenericException
import pe.com.scotiabank.blpm.android.data.exception.MaintenanceException
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.ui.list.coordinator.NavigationArrangement
import pe.com.scotiabank.blpm.android.ui.list.coordinator.ObserverAction
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import kotlin.coroutines.cancellation.CancellationException

class CompletionFlowCoordinator<T: Any, S: BaseSummaryModel>(
    hub: Hub,
    private val appModel: AppModel,
    private val weakResources: WeakReference<Resources?>,
    private val weakAppContext: WeakReference<Context?>,
    private val transactionId: String,
    private val isValidatedWithPush: Boolean,
    private val carrierFromConfirmationConsumer: CarrierFromConfirmationConsumer,
    private val analyticData: OperationAnalyticData,
    private val confirmationAnalyticConsumer: Consumer<AnalyticEventData<*>>,
    private val analyticConsumerForPushKey: Consumer<AnalyticEventData<*>>,
    private val confirmModel: ConfirmModel<T>,
    private val summaryAdapter: SummaryAdapter<in T, in S>,
    private val frequentOperationModel: FrequentOperationModel<in T, in S>,
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
            ModalEventCarrier::class,
            InstancePredicate(::filterOnAcceptModalClicked),
            InstanceHandler(::handleAcceptModelClicked)
        )
        .build()
    override val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val suspendingHandlingStore: StoreOfSuspendingHandling = StoreOfSuspendingHandling.Builder()
        .add(
            FinishingCoordinator::class,
            InstancePredicate(::filterInBackEvent),
            SuspendingHandlerOfInstance(::handleBackEvent)
        )
        .add(
            CarrierForConfirmation::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleClickOnConfirm)
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

    private val storeOfSuspendingErrorHandling: StoreOfSuspendingErrorHandling = StoreOfSuspendingErrorHandling.Builder()
        .putHandlerByType(
            FinishedSessionException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .putHandlerByType(
            ForceUpdateException::class,
            SuspendingHandlerOfInstance(::showErrorMessage),
        )
        .addHandlerByInstance(
            HttpResponseException::class,
            InstancePredicate(::filterInInvalidRequestError),
            SuspendingHandlerOfInstance(::handleInvalidRequestError),
        )
        .putHandlerByType(
            HttpResponseException::class,
            SuspendingHandlerOfInstance(::handleOtherError)
        )
        .addHandlerByInstance(
            GenericException::class,
            InstancePredicate(::filterInAnySubType),
            SuspendingHandlerOfInstance(::handleIncorrectDataError),
        )
        .putHandlerByType(
            MaintenanceException::class,
            SuspendingHandlerOfInstance(::handleMaintenanceError),
        )
        .setHandlerForUnregistered(
            SuspendingHandlerOfInstance(::handleUnregisteredError),
        )
        .setHandlerForCatchingAll(
            SuspendingHandlerOfInstance(FirebaseCrashlytics.getInstance()::recordException),
        )
        .build()
    private val errorReceiver: SuspendingReceiverOfError = SuspendingReceivingAgentOfError(
        store = storeOfSuspendingErrorHandling,
    )

    private val weakSelf: WeakReference<out Coordinator?> = WeakReference(this)

    private val confirmationCoordinatorFactory: ConfirmationCoordinatorFactory by lazy {
        ConfirmationCoordinatorFactory(
            hub = hub,
            analyticConsumer = confirmationAnalyticConsumer,
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

    private val pushKeyCoordinatorFactory: PushKeyCoordinatorFactory by lazy {
        PushKeyCoordinatorFactory(
            hub = hub,
            parentScope = scope,
            embeddedDataName = String.EMPTY,
            weakParent = weakSelf,
            hideErrorPage = false,
        )
    }

    private val factoryOfModalDataHolder = FactoryOfModalDataHolder(
        dispatcherProvider = dispatcherProvider,
        receiver = selfReceiver,
        weakResources = weakResources,
    )

    private var description: String = String.EMPTY

    override suspend fun start() = withContext(scope.coroutineContext) {
        val child = confirmationCoordinatorFactory.create(
            titleText = weakResources.get()?.getString(R.string.title_activity_confirmation).orEmpty(),
            carrier = carrierFromConfirmationConsumer,
        )
        addChild(child)
        userInterface.receive(NavigationArrangement.ADD_SCREEN)
        child.start()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        Unit
    }

    private fun filterInBackEvent(finishingChild: FinishingCoordinator): Boolean {
        val intention: NavigationIntention = finishingChild.data as? NavigationIntention ?: return false
        return intention == NavigationIntention.BACK
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleBackEvent(finishingChild: FinishingCoordinator) {
        receiveEvent(NavigationIntention.BACK)
    }

    private suspend fun handleClickOnConfirm(carrier: CarrierForConfirmation) {
        description = carrier.description

        if (isValidatedWithPush) {
            requirePushValidation()
            return
        }

        mutableLiveHolder.notifyMainLoadingVisibility(true)
        tryConfirm(authTracking = String.EMPTY, authId = String.EMPTY)
    }

    private suspend fun requirePushValidation() {
        val isNotificationsEnabled: Boolean = NotificationsUtil.areNotificationsEnabled(weakAppContext)
        if (isNotificationsEnabled) openPushValidation() else goToNotificationPermission()
    }

    private suspend fun goToNotificationPermission() {
        val child: Coordinator = notificationPermissionCoordinatorFactory.create(Unit)
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

    @Suppress("UNUSED_PARAMETER")
    private suspend fun handleNotificationPermissionGranted(carrier: CarrierOfPermissionResult) {
        removeChild(currentChild)
        userInterface.receive(NavigationArrangement.REMOVE_SCREEN)
        currentChild.updateUiData()
        userInterface.receive(ObserverAction.REGISTER_AGAIN)
        openPushValidation()
    }

    private suspend fun openPushValidation() {
        val child = pushKeyCoordinatorFactory.create(
            transactionId = transactionId,
            analyticConsumer = analyticConsumerForPushKey,
            analyticAdditionalData = Unit,
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

    private fun filterInOtpPushGenericError(finishingChild: FinishingCoordinator): Boolean {
        val otpPushError: OtpPushError = finishingChild.data as? OtpPushError ?: return false
        return OtpErrorType.filterInSomethingWentWrong(type = otpPushError.otpError)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleOtpPushGenericError(finishingCoordinator: FinishingCoordinator) {
        userInterface.receive(NavigationIntention.CLOSE)
    }

    private fun filterInOtpPushMaxAttemptsError(finishingChild: FinishingCoordinator): Boolean {
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

        mutableLiveHolder.notifyMainLoadingVisibility(true)
        tryConfirm(data.authTracking, data.authId)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun tryConfirm(authTracking: String, authId: String) = try {
        val responseEntity: T = confirmModel.confirm(
            authTracking = authTracking,
            authToken = authId,
            transactionId = transactionId,
            description = description,
        )
        val summary: S = summaryAdapter.adapt(responseEntity) as S
        summary.isDisableMiddleButton = frequentOperationModel.verify(
            responseEntity = responseEntity,
            summary = summary,
            )
        onSuccessfulConfirmation(summary)
    } catch (throwable: Throwable) {
        errorReceiver.receive(throwable)
    }

    private fun onSuccessfulConfirmation(summary: S) {
        val descriptionTag: String = if (description.isBlank()) AnalyticsBaseConstant.FALSE else AnalyticsBaseConstant.TRUE

        val carrier: CarrierOfActivityDestination = destinationCarrierOf(ReceiptActivity::class.java) {
            ReceiptActivity.PARAM_SUMMARY to summary
            AnalyticsBaseConstant.TYPE_OF_ORIGIN_ACCOUNT to analyticData.typeOfOriginProduct
            AnalyticsBaseConstant.ORIGIN_CURRENCY to analyticData.originCurrency
            AnalyticsBaseConstant.TYPE_OF_DESTINATION_ACCOUNT to analyticData.typeOfDestinationProduct
            AnalyticsBaseConstant.DESTINATION_CURRENCY to analyticData.destinationCurrency
            AnalyticsBaseConstant.AMOUNT to analyticData.amountText
            AnalyticsConstant.COMPANY to analyticData.company
            AnalyticsBaseConstant.TYPE_PROCESS to analyticData.processType
            ValidationInformation.OPERATION_NAME to analyticData.information.name
            AnalyticsBaseConstant.DESCRIPTION to descriptionTag
            AnalyticsBaseConstant.TYPE_OF_MOVEMENT to analyticData.movementType
            ReceiptActivity.PARAM_INSTALLMENTS_NUMBER to analyticData.installmentsNumber
            AnalyticsConstant.STATUS to analyticData.status
        }

        mutableLiveHolder.notifyMainLoadingVisibility(false)
        userInterface.receive(carrier)
    }

    private suspend fun handleUnregisteredError(throwable: Throwable) {
        if (throwable is CancellationException) return
        hideKeyboard()
        showModalData(modalData = ModalData.GENERIC_ERROR)
    }

    private suspend fun showModalData(modalData: ModalData) {
        mutableLiveHolder.notifyMainLoadingVisibility(false)
        val dataHolder: ModalDataHolder = factoryOfModalDataHolder.createBy(modalData)
        userInterface.receive(dataHolder)
    }

    private fun filterOnAcceptModalClicked(carrier: ModalEventCarrier): Boolean {
        val event: ModalEvent = carrier.event
        return ModalEvent.PRIMARY_CLICKED == event
    }

    @Suppress("UNUSED_PARAMETER")
    private fun handleAcceptModelClicked(carrier: ModalEventCarrier) {
        receiveEvent(NavigationIntention.BACK)
    }

    private suspend fun handleIncorrectDataError(throwable: GenericException) {
        val dataHolder: ModalDataHolder = factoryOfModalDataHolder.createBy(
            title = throwable.title,
            message = throwable.message
        )
        userInterface.receive(dataHolder)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
    }

    private fun filterInInvalidRequestError(
        throwable: HttpResponseException,
    ): Boolean = HttpURLConnection.HTTP_BAD_REQUEST == throwable.httpCode
            && Constant.INVALID_REQUEST_ERROR.isSameErrorTypeAs(throwable)

    private fun String.isSameErrorTypeAs(throwable: HttpResponseException): Boolean {
        val type: String = getErrorType(throwable) ?: return false
        return contentEquals(type)
    }

    private fun getErrorType(throwable: HttpResponseException): String? {
        val body: PeruErrorResponseBody? = throwable.body
        return body?.type?.let(::String)
    }

    private suspend fun handleInvalidRequestError(throwable: HttpResponseException) {

        val dataHolder: ModalDataHolder = factoryOfModalDataHolder.createBy(throwable)
        userInterface.receive(dataHolder)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
    }

    private suspend fun handleOtherError(throwable: HttpResponseException) {

        val dataHolder: ModalDataHolder = factoryOfModalDataHolder.createBy(throwable)
        userInterface.receive(dataHolder)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
    }

    private suspend fun handleMaintenanceError(throwable: MaintenanceException) {
        val dataHolder: ModalDataHolder = factoryOfModalDataHolder.createBy(
            title = String.EMPTY,
            message = throwable.message.orEmpty(),
        )
        userInterface.receive(dataHolder)
        mutableLiveHolder.notifyMainLoadingVisibility(false)
    }
}
