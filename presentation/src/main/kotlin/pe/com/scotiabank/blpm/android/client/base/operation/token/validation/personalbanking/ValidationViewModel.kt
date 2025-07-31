package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal
import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import com.scotiabank.canvaspe.smartkey.SmartKeyEntity
import com.scotiabank.enhancements.encoding.mapToCharArray
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.proofofkey.auth.utilities.constant.ConstantBiometric
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokBiometricException
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokSecurityException
import com.scotiabank.proofofkey.auth.utilities.error.sealed.PokNetworkError
import com.scotiabank.proofofkey.auth.utilities.listener.BiometricTXPromptCallback
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
import pe.com.scotiabank.blpm.android.client.base.confirmation.ConfirmationDataMapper
import pe.com.scotiabank.blpm.android.client.base.dialog.StaticDataCanvasDialogModal
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationModel
import pe.com.scotiabank.blpm.android.client.base.operation.token.request.personalbanking.TokenRequestModelForPersonalBanking
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.*
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.summary.SummaryAdapter
import pe.com.scotiabank.blpm.android.client.base.receipt.ReceiptActivity
import pe.com.scotiabank.blpm.android.client.base.session.entities.Profile
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.model.FactorModel
import pe.com.scotiabank.blpm.android.client.model.security.SecurityAuthModel
import pe.com.scotiabank.blpm.android.client.security.SecurityAuthUtil
import pe.com.scotiabank.blpm.android.client.transfer.TransferEvent
import pe.com.scotiabank.blpm.android.client.transfer.TransferUtil
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.analytics.RechargeAnalytics
import pe.com.scotiabank.blpm.android.client.util.biometric.BiometricErrorMessageFactory.getErrorCodeFrom
import pe.com.scotiabank.blpm.android.client.util.biometric.BiometricErrorMessageFactory.getMessageFrom
import pe.com.scotiabank.blpm.android.client.util.biometric.BiometricErrorMessageFactory.getMessageFromSecurityApi
import pe.com.scotiabank.blpm.android.client.util.biometric.BiometricErrorMessageFactory.getTxMessage
import pe.com.scotiabank.blpm.android.client.util.biometric.clearBiometricInformation
import pe.com.scotiabank.blpm.android.client.util.exception.ErrorMessageFactory
import pe.com.scotiabank.blpm.android.client.util.exception.RetrofitException
import pe.com.scotiabank.blpm.android.data.domain.exception.DefaultErrorBundle
import pe.com.scotiabank.blpm.android.data.domain.exception.ErrorBundle
import pe.com.scotiabank.blpm.android.data.exception.GenericException
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip
import java.lang.ref.WeakReference

class ValidationViewModel<R: Any, S: BaseSummaryModel>(
    private val appModel: AppModel,
    private val weakAppContext: WeakReference<Context?>,
    private val weakResources: WeakReference<Resources?>,
    private val dataOnBackDialog: DataOnBackDialog,
    private val helperForAuthError: HelperForAuthErrorDialog,
    private val factoryOfBiometricBuddyTip: FactoryOfBiometricBuddyTip,
    private val tokenRequestModel: TokenRequestModelForPersonalBanking,
    private val smartKeyModel: SmartKeyModel,
    private val validationModel: ValidationModel,
    private val confirmModel: ConfirmModel<R>,
    private val canvasEntity: CanvasConfirmationEntity,
    private var securityAuth: SecurityAuthModel,
    private val summaryAdapter: SummaryAdapter<R, S>,
    private val frequentOperationModel: FrequentOperationModel<R, S>,
    private val holderOfStringCreation: HolderOfStringCreation,
    private val holderOfIntCreation: HolderOfIntCreation,
    private val analyticModel: AnalyticModel,
) : NewBaseViewModel(), BiometricTXPromptCallback {

    private val _liveDataOfCanvasEntity: MutableLiveData<CanvasConfirmationEntity> = MutableLiveData()
    val liveDataOfCanvasEntity: LiveData<CanvasConfirmationEntity>
        get() = _liveDataOfCanvasEntity

    private val _liveDataOfSmartKeyEntities: MutableLiveData<List<SmartKeyEntity>> = MutableLiveData()
    val liveDataOfSmartKeyEntities: LiveData<List<SmartKeyEntity>>
        get() = _liveDataOfSmartKeyEntities

    private val profile: Profile
        get() = appModel.profile

    private val factors: List<FactorModel>
        get() = profile.factorModels

    private val factorEntities: List<SmartKeyEntity> = ConfirmationDataMapper.transformFactorModels(
        factors
    )
    private var channelSelectedForOtp: OtpAuthTracking = factors
        .filter(::filterInDefaultChannel)
        .map(::toOtpAuthTracking)
        .firstOrNull()
        ?: OtpAuthTracking.NONE

    private val authModeForOtp: String
        get() = when (channelSelectedForOtp.option) {
            Constant.PHONE_OTP -> AnalyticsConstant.AUTH_MODE_OTP_SMS
            Constant.EMAIL_OTP -> AnalyticsConstant.AUTH_MODE_OTP_EMAIL
            else -> AnalyticsConstant.HYPHEN_STRING
        }

    private val isOtpPreferred: Boolean
        get() = SecurityAuthUtil.isOTPType(securityAuth.type)
    private val authMode: String
        get() = if (isOtpPreferred) authModeForOtp else AnalyticsConstant.OTP_BIOMETRIC

    private val _liveEntityOfBiometricBuddyTip: MutableLiveData<UiEntityOfBuddyTip> = MutableLiveData()
    val liveEntityOfBiometricBuddyTip: LiveData<UiEntityOfBuddyTip>
        get() = _liveEntityOfBiometricBuddyTip

    private val entityOfBiometricBuddyTip: UiEntityOfBuddyTip by lazy {
        factoryOfBiometricBuddyTip.create()
    }

    private val _liveVisibilityOfBiometricBuddyTip: MutableLiveData<Boolean> = MutableLiveData()
    val liveVisibilityOfBiometricBuddyTip: LiveData<Boolean>
        get() = _liveVisibilityOfBiometricBuddyTip

    private val _liveEnablingOfConfirmButton: MutableLiveData<Boolean> = MutableLiveData()
    val liveEnablingOfConfirmButton: LiveData<Boolean>
        get() = _liveEnablingOfConfirmButton

    private val transactionId: String
        get() = canvasEntity.transactionId

    private var description: String = Constant.EMPTY_STRING

    private var receiverOfViewModelEvents: InstanceReceiver? = null

    private val callbackOfPrimaryButton: Runnable by lazy {
        Runnable(::onYesButtonClickedCanvasDialogModal)
    }

    private fun filterInDefaultChannel(factor: FactorModel): Boolean = factor.isDefaultAuth

    private fun toOtpAuthTracking(
        factor: FactorModel,
    ): OtpAuthTracking = OtpAuthTracking.identifyBy(factor.type)

    fun setUpUi(receiverOfViewModelEvents: InstanceReceiver) {
        this.receiverOfViewModelEvents = receiverOfViewModelEvents
        if (isOtpPreferred) {
            sendScreenEvent()
            setUpOtp()
            return
        }
        sendScreenEvent()
        setUpBiometric()
    }

    private fun setUpOtp() {
        canvasEntity.isShowSmartKey = true
        _liveDataOfCanvasEntity.postValue(canvasEntity)
        _liveDataOfSmartKeyEntities.postValue(factorEntities)
        _liveEntityOfBiometricBuddyTip.postValue(entityOfBiometricBuddyTip)
        _liveVisibilityOfBiometricBuddyTip.postValue(false)
        _liveEnablingOfConfirmButton.postValue(false)
    }

    private fun sendScreenEvent() {
        val data: Map<String, Any?> = mapOf(AnalyticsConstant.AUTH_MODE to authModeForOtp)
        sendAnalyticEvent(ValidationEvent.SCREEN, data)
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
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.AUTH_MODE to authMode,
            AnalyticsConstant.EVENT_LABEL to label,
        )
        sendAnalyticEvent(ValidationEvent.CLICK, data)
    }

    fun onSmartKeyEntered(isDigitalKeyValid: Boolean) {
        val isOtpPreferred: Boolean = SecurityAuthUtil.isOTPType(securityAuth.type)
        if (isOtpPreferred) {
            _liveEnablingOfConfirmButton.postValue(isDigitalKeyValid)
        }
    }

    fun confirmWithOtp(otpValue: String) = viewModelScope.launch {
        setLoadingV2(true)
        sendOnConfirmClicked()
        tryConfirm(
            challengeType = Constant.SECURITY_AUTH_OTP,
            challengeValue = otpValue,
            authTracking = channelSelectedForOtp.authTracking,
        )
    }

    private fun sendOnConfirmClicked() {
        val data: Map<String, Any?> = mapOf(
            AnalyticsConstant.AUTH_MODE to authMode,
            AnalyticsConstant.DESCRIPTION to description,
        )
        sendAnalyticEvent(ValidationEvent.CONFIRM, data)
    }

    fun requestNewOtpValueFor(option: String) = viewModelScope.launch {
        setLoadingV2(true)
        sendOptionEvent(option)
        channelSelectedForOtp = OtpAuthTracking.identifyBy(option)
        tryRequestNewOtpValueFor(option)
    }

    private fun sendOptionEvent(option: String) {
        val eventLabel: String = findEventLabelFor(option)
        sendClickEvent(eventLabel)
    }

    private fun findEventLabelFor(option: String): String = when (option) {
        channelSelectedForOtp.option -> AnalyticLabel.SEND_AGAIN.value
        Constant.PHONE_OTP -> AnalyticLabel.SEND_BY_SMS.value
        Constant.EMAIL_OTP -> AnalyticLabel.SEND_BY_EMAIL.value
        else -> AnalyticsConstant.HYPHEN_STRING
    }

    private suspend fun tryRequestNewOtpValueFor(option: String) = try {
        smartKeyModel.apply(input = option.mapToCharArray())
        onNewOtpValueSuccessfullyRequested()
    } catch (throwable: Throwable) {
        showErrorMessage(throwable)
    }

    private fun onNewOtpValueSuccessfullyRequested() {
        setLoadingV2(false)
        receiverOfViewModelEvents?.receive(ValidationIntention.SHOW_OTP_MESSAGE)
    }

    fun onConfirmClicked(description: String) {
        this.description = description
        val isBiometric: Boolean = weakAppContext.get()?.let(::isBiometricNonceValid) ?: false
        val event: Any = if (isBiometric) securityAuth else ValidationIntention.GRAB_OTP_VALUE
        receiverOfViewModelEvents?.receive(event)
    }

    private fun isBiometricNonceValid(
        appContext: Context,
    ): Boolean = TransferUtil.validateBiometricNonce(profile, securityAuth.nonce, appContext)

    private fun setUpBiometric() {
        canvasEntity.isShowSmartKey = false
        _liveDataOfCanvasEntity.postValue(canvasEntity)
        _liveDataOfSmartKeyEntities.postValue(emptyList())
        _liveEntityOfBiometricBuddyTip.postValue(entityOfBiometricBuddyTip)
        _liveVisibilityOfBiometricBuddyTip.postValue(true)
        _liveEnablingOfConfirmButton.postValue(true)
    }

    override fun onPokBiometricException(pokBiometricException: PokBiometricException) {
        val message: String = weakAppContext.get()?.getMessageFrom(pokBiometricException).orEmpty()
        val data = DataOfAuthErrorDialog(helperForAuthError.title, message, helperForAuthError.textForPositiveButton)
        receiverOfViewModelEvents?.receive(data)
    }

    override fun onPokNetworkError(pokNetworkError: PokNetworkError) {
        if (pokNetworkError !is PokNetworkError.NetworkNoInternet) return

        val message: String = weakAppContext.get()?.getMessageFrom(pokNetworkError).orEmpty()
        val data = DataOfAuthErrorDialog(helperForAuthError.title, message, helperForAuthError.textForPositiveButton)
        receiverOfViewModelEvents?.receive(data)
    }

    override fun onPokSecurityException(pokSecurityException: PokSecurityException) {
        val message: String = weakAppContext.get()?.getMessageFrom(pokSecurityException).orEmpty()
        val data = DataOfAuthErrorDialog(helperForAuthError.title, message, helperForAuthError.textForPositiveButton)
        receiverOfViewModelEvents?.receive(data)
    }

    override fun onBiometricTXPromptError(errorCode: Int, errString: CharSequence) {
        if (ConstantBiometric.FINGERPRINT_WAS_CANCELED_BY_USER == errorCode) return

        if (Constant.BIOMETRIC_WAS_CHANGED == errorCode) {
            modifyFingerPrint()
        }

        val message: String = weakAppContext.get()?.getTxMessage(errorCode).orEmpty()
        val data = DataOfAuthErrorDialog(helperForAuthError.title, message, helperForAuthError.textForPositiveButton)
        receiverOfViewModelEvents?.receive(data)
    }

    /***
     * Clear the keystore when customer modifies its fingerprints (remove all or add a new fingerprint)
     * First: Clear sensitive biometric data
     * Second: Close Loading
     * Finally: Show error message
     */
    private fun modifyFingerPrint() {
        weakAppContext.get()?.let(::clearBiometricInformation)
        _liveEnablingOfConfirmButton.postValue(false)
    }

    override fun onBiometricTXPromptFailed(errorCode: String) {
        // no-op
    }

    override fun onBiometricTXPromptSuccessful(value: String) {
        confirmWithBiometric(value)
    }

    fun onTriedShowingBiometricPrompt(exception: Exception) {
        val errorCode: Int = weakAppContext.get()?.getErrorCodeFrom(exception) ?: Constant.ZERO
        if (ConstantBiometric.FINGERPRINT_WAS_CANCELED_BY_USER == errorCode) return

        if (Constant.BIOMETRIC_WAS_CHANGED == errorCode) {
            modifyFingerPrint()
        }

        val message: String = weakAppContext.get()?.getMessageFrom(exception).orEmpty()
        val data = DataOfAuthErrorDialog(helperForAuthError.title, message, helperForAuthError.textForPositiveButton)
        receiverOfViewModelEvents?.receive(data)
    }

    fun changeToOtpTokenType() = viewModelScope.launch {
        setLoadingV2(true)
        tryRequestToken()
    }

    private suspend fun tryRequestToken() = try {
        securityAuth = tokenRequestModel.requestToken(
            transactionId = transactionId,
            type = Constant.SECURITY_AUTH_TYPE_TOKEN,
            deviceId = Constant.EMPTY_STRING,
        )
        onSuccessfulRequestedToken()
    } catch (throwable: Throwable) {
        showError(throwable)
    }

    private fun showError(throwable: Throwable) {
        if (throwable is GenericException) {
            receiverOfViewModelEvents?.receive(createErrorCanvasDialogModal(throwable))
            setLoadingV2(false)
            return
        }

        val exception: Exception = throwable as? Exception
            ?: return setLoadingV2(false)

        val errorBundle: ErrorBundle = DefaultErrorBundle(exception)

        val retrofitException: RetrofitException = weakAppContext.get()
            ?.let { appContext -> ErrorMessageFactory.createWithCode(appContext, errorBundle.exception) }
            ?: return setLoadingV2(false)

        val errorMessage: String = weakAppContext.get()
            ?.getMessageFromSecurityApi(retrofitException)
            ?: return setLoadingV2(false)

        val errorCode: String = retrofitException.responseCode
        val data: DataOfErrorDialog = createDialogDataFromServiceError(
            helper = helperForAuthError,
            errorCode = errorCode,
            errorMessage = errorMessage,
            clearCallback = ::modifyFingerPrint,
        )
        if (data is DataOfAuthErrorDialog) {
            setLoadingV2(false)
            receiverOfViewModelEvents?.receive(data)
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

    private fun onSuccessfulRequestedToken() {
        setLoadingV2(false)
        setUpOtp()
    }

    private fun confirmWithBiometric(challengeValue: String) = viewModelScope.launch {
        setLoadingV2(true)
        sendOnConfirmClicked()
        tryConfirm(
            challengeType = Constant.SECURITY_AUTH_SIGNED_NONCE,
            challengeValue = challengeValue,
            authTracking = Constant.AUTH_TRACKING_BIOMETRIC_HEADER,
        )
    }

    private suspend fun tryConfirm(
        challengeType: String,
        challengeValue: String,
        authTracking: String,
    ) = try {
        validationModel.validate(
            transactionId = transactionId,
            authId = securityAuth.id,
            challengeType = challengeType,
            challengeValue = challengeValue,
        )
        val responseEntity: R = confirmModel.confirm(
            transactionId = transactionId,
            authTracking = authTracking,
            authId = securityAuth.id,
            description = description,
        )
        val summary: S = summaryAdapter.adapt(responseEntity)
        summary.isDisableMiddleButton = frequentOperationModel.verify(responseEntity, summary)
        onSuccessfulConfirmation(summary)
    } catch (throwable: Throwable) {
        showError(throwable)
    }

    private fun onSuccessfulConfirmation(summary: S) {
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
            ReceiptActivity.PARAM_AUTH_MODE to authMode
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
