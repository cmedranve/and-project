package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.non

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal
import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.analytics.factories.operation.token.validation.ValidationInformation
import pe.com.scotiabank.blpm.android.analytics.util.AnalyticsConstant
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.analytics.AnalyticEventData
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfIntCreation
import pe.com.scotiabank.blpm.android.client.base.carrier.HolderOfStringCreation
import pe.com.scotiabank.blpm.android.client.base.carrier.destinationCarrierOf
import pe.com.scotiabank.blpm.android.client.base.dialog.StaticDataCanvasDialogModal
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.*
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapter
import pe.com.scotiabank.blpm.android.client.base.receipt.ReceiptActivity
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.transfer.TransferEvent
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.analytics.RechargeAnalytics
import pe.com.scotiabank.blpm.android.data.exception.GenericException
import java.lang.ref.WeakReference

class ValidationViewModel<R: Any, S: BaseSummaryModel>(
    private val appModel: AppModel,
    private val weakResources: WeakReference<Resources?>,
    private val dataOnBackDialog: DataOnBackDialog,
    private val confirmModel: ConfirmModel<R>,
    private val canvasEntity: CanvasConfirmationEntity,
    private val summaryAdapter: SummaryAdapter<R, S>,
    private val frequentOperationModel: FrequentOperationModel<R, S>,
    private val holderOfStringCreation: HolderOfStringCreation,
    private val holderOfIntCreation: HolderOfIntCreation,
    private val analyticModel: AnalyticModel,
) : NewBaseViewModel() {

    private val _liveDataOfCanvasEntity: MutableLiveData<CanvasConfirmationEntity> = MutableLiveData()
    val liveDataOfCanvasEntity: LiveData<CanvasConfirmationEntity>
        get() = _liveDataOfCanvasEntity

    private val _liveEnablingOfConfirmButton: MutableLiveData<Boolean> = MutableLiveData()
    val liveEnablingOfConfirmButton: LiveData<Boolean>
        get() = _liveEnablingOfConfirmButton

    private val transactionId: String
        get() = canvasEntity.transactionId

    private var receiverOfViewModelEvents: InstanceReceiver? = null

    private val callbackOfPrimaryButton: Runnable by lazy {
        Runnable(::onYesButtonClickedCanvasDialogModal)
    }

    fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
        sendAnalyticEvent(ValidationEvent.SCREEN)
        _liveDataOfCanvasEntity.postValue(canvasEntity)
        _liveEnablingOfConfirmButton.postValue(true)
    }

    private fun sendAnalyticEvent(event: ValidationEvent, data: Map<String, Any?> = emptyMap()) {
        val eventData = AnalyticEventData(event, data)
        analyticModel.accept(eventData)
    }

    fun onBack() {
        sendClickEvent(AnalyticLabel.BACK.value)
        receiverOfViewModelEvents?.receive(createStaticDataCanvasDialogModal())
    }

    private fun sendClickEvent(label: String) {
        val data: Map<String, Any?> = mapOf(AnalyticsConstant.EVENT_LABEL to label)
        sendAnalyticEvent(ValidationEvent.CLICK, data)
    }

    fun onConfirmClicked(description: String) = viewModelScope.launch {
        setLoadingV2(true)
        sendOnConfirmClicked(description)
        tryConfirm(description)
    }

    private fun sendOnConfirmClicked(description: String) {
        val data: Map<String, Any?> = mapOf(AnalyticsConstant.DESCRIPTION to description)
        sendAnalyticEvent(ValidationEvent.CONFIRM, data)
    }

    private suspend fun tryConfirm(description: String) = try {
        val responseEntity: R = confirmModel.confirm(
            transactionId = transactionId,
            authTracking = Constant.EMPTY_STRING,
            authId = Constant.EMPTY_STRING,
            description = description,
        )
        val summary: S = summaryAdapter.adapt(responseEntity)
        summary.isDisableMiddleButton = frequentOperationModel.verify(responseEntity, summary)
        onSuccessfulConfirmation(summary, description)
    } catch (throwable: Throwable) {
        showError(throwable)
    }

    private fun showError(throwable: Throwable) {
        if (throwable is GenericException) {
            receiverOfViewModelEvents?.receive(createErrorCanvasDialogModal(throwable))
            setLoadingV2(false)
            return
        }
        showErrorMessage(throwable)
    }

    private fun createErrorCanvasDialogModal(throwable: GenericException): StaticDataCanvasDialogModal {
        val primaryButtonLabel: String = weakResources.get()?.getString(
            pe.com.scotiabank.blpm.android.client.R.string.understood
        ).orEmpty()

        val attrsCanvasDialogModal = AttrsCanvasDialogModal(
            title = throwable.title,
            textBody = throwable.message,
            primaryButtonLabel = primaryButtonLabel,
        )
        return StaticDataCanvasDialogModal(
            attrsCanvasDialogModal = attrsCanvasDialogModal,
        )
    }

    private fun onSuccessfulConfirmation(summary: S, description: String) {
        val carrier: CarrierOfActivityDestination = destinationCarrierOf(ReceiptActivity::class.java) {
            ReceiptActivity.PARAM_SUMMARY to summary
            AnalyticsConstant.STATUS to holderOfStringCreation.findBy(AnalyticsConstant.STATUS)
            AnalyticsConstant.TYPE_PAY to holderOfStringCreation.findBy(AnalyticsConstant.TYPE_PAY)
            AnalyticsConstant.TYPE_OF_ORIGIN_ACCOUNT to holderOfStringCreation.findBy(AnalyticsConstant.TYPE_OF_ORIGIN_ACCOUNT)
            AnalyticsConstant.ORIGIN_CURRENCY to holderOfStringCreation.findBy(AnalyticsConstant.ORIGIN_CURRENCY)
            AnalyticsConstant.TYPE_OF_DESTINATION_ACCOUNT to holderOfStringCreation.findBy(AnalyticsConstant.TYPE_OF_DESTINATION_ACCOUNT)
            AnalyticsConstant.DESTINATION_CURRENCY to holderOfStringCreation.findBy(AnalyticsConstant.DESTINATION_CURRENCY)
            AnalyticsConstant.AMOUNT to holderOfStringCreation.findBy(AnalyticsConstant.AMOUNT)
            AnalyticsConstant.COMPANY to holderOfStringCreation.findBy(AnalyticsConstant.COMPANY)
            AnalyticsConstant.TYPE_PROCESS to holderOfStringCreation.findBy(AnalyticsConstant.TYPE_PROCESS)
            ValidationInformation.OPERATION_NAME to analyticModel.validationInformationName
            ReceiptActivity.PARAM_AUTH_MODE to AnalyticsConstant.HYPHEN_STRING
            AnalyticsConstant.DESCRIPTION to if (description.isBlank()) AnalyticsConstant.FALSE else AnalyticsConstant.TRUE
            AnalyticsConstant.TYPE_MOVEMENT to holderOfStringCreation.findBy(AnalyticsConstant.TYPE_MOVEMENT)
            ReceiptActivity.PARAM_INSTALLMENTS_NUMBER to holderOfIntCreation.findBy(ReceiptActivity.PARAM_INSTALLMENTS_NUMBER)
            AnalyticsConstant.INSTALLMENTS_NUMBER to holderOfStringCreation.findBy(AnalyticsConstant.INSTALLMENTS_NUMBER)
            RechargeAnalytics.TYPE_VOUCHER_RECHARGES to holderOfStringCreation.findBy(RechargeAnalytics.TYPE_VOUCHER_RECHARGES)
            RechargeAnalytics.AMOUNT_RECHARGES to holderOfStringCreation.findBy(AnalyticsConstant.AMOUNT)
        }
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(carrier)
    }

    private fun createStaticDataCanvasDialogModal(): StaticDataCanvasDialogModal {
        val title: String = weakResources.get()?.getString(
            pe.com.scotiabank.blpm.android.client.R.string.cancel_this_operation_x,
            dataOnBackDialog.title
        ).orEmpty()

        val textBody: String = weakResources.get()?.getString(
            pe.com.scotiabank.blpm.android.client.R.string.are_you_sure_to_cancel_this_operation,
            dataOnBackDialog.message
        ).orEmpty()

        val primaryButtonLabel: String = weakResources.get()?.getString(
            pe.com.scotiabank.blpm.android.client.R.string.yes
        ).orEmpty()

        val secondaryButtonLabel: String = weakResources.get()?.getString(
            pe.com.scotiabank.blpm.android.client.R.string.no
        ).orEmpty()

        val attrsCanvasDialogModal = AttrsCanvasDialogModal(
            title = title,
            textBody = textBody,
            primaryButtonLabel = primaryButtonLabel,
            secondaryButtonLabel = secondaryButtonLabel,
        )
        return StaticDataCanvasDialogModal(
            attrsCanvasDialogModal = attrsCanvasDialogModal,
            callbackOfPrimaryButton = callbackOfPrimaryButton,
        )
    }

    private fun onYesButtonClickedCanvasDialogModal() {
        appModel.receiveEvent(TransferEvent.SCREEN)
    }
}
