package pe.com.scotiabank.blpm.android.client.biometric.enrollment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.weakreference.getEmptyWeak
import com.scotiabank.proofofkey.auth.core.base.BiometricBuilder
import com.scotiabank.proofofkey.auth.core.base.BiometricPokManager
import com.scotiabank.proofofkey.auth.core.entities.EnrollmentFieldModel
import com.scotiabank.proofofkey.auth.utilities.BiometricUtils
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokBiometricException
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokCryptoException
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokEnrollmentException
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokSecurityException
import com.scotiabank.proofofkey.auth.utilities.error.sealed.PokNetworkError
import com.scotiabank.proofofkey.auth.utilities.listener.BiometricEnrollmentCallback
import com.scotiabank.proofofkey.auth.utilities.listener.BiometricTXPromptCallback
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.base.NewBaseViewModel
import pe.com.scotiabank.blpm.android.client.base.subscribers.BaseSubscriberKt
import pe.com.scotiabank.blpm.android.client.biometric.BiometricEnrollmentMapper.toReversedOperations
import pe.com.scotiabank.blpm.android.client.biometric.BiometricEnrollmentMapper.transformToBiometricConfigurationModel
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.digitalkeybiometricV2.DigitalKeyBiometricUiModelV2
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.digitalkeybiometricV2.PreRegisterResponseModelV2
import pe.com.scotiabank.blpm.android.client.biometric.util.createBiometricPromptFieldModel
import pe.com.scotiabank.blpm.android.client.biometric.util.updateModel
import pe.com.scotiabank.blpm.android.client.model.biometric.BiometricConfigurationModel
import pe.com.scotiabank.blpm.android.client.shield.wipeMemoryByFinishingProcess
import pe.com.scotiabank.blpm.android.client.util.SharedPreferencesUtil
import pe.com.scotiabank.blpm.android.client.util.Util
import pe.com.scotiabank.blpm.android.client.util.biometric.BiometricErrorMessageFactory.getMessageFrom
import pe.com.scotiabank.blpm.android.client.util.biometric.clearBiometricInformation
import pe.com.scotiabank.blpm.android.client.util.biometric.hasEnrolledCamsId
import pe.com.scotiabank.blpm.android.data.domain.interactor.DigitalKeyValidationUseCase
import pe.com.scotiabank.blpm.android.data.domain.interactor.LoginUseCase
import pe.com.scotiabank.blpm.android.data.domain.interactor.ResendOtpUseCase
import pe.com.scotiabank.blpm.android.data.domain.interactor.SmartKeyJoyUseCase
import pe.com.scotiabank.blpm.android.data.domain.interactor.biometric.BiometricPreRegisterUseCase
import pe.com.scotiabank.blpm.android.data.entity.ErrorMessageOtpEntity
import pe.com.scotiabank.blpm.android.data.entity.OtpEntity
import pe.com.scotiabank.blpm.android.data.entity.ProfileEntity
import pe.com.scotiabank.blpm.android.data.entity.SmartKeyVerifyRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.biometric.BiometricPreRegisterResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.biometric.RegisterBiometricRequestEntity
import pe.com.scotiabank.blpm.android.data.util.Constant
import java.lang.ref.WeakReference

class EnrollmentBiometricViewModel(
    private val appModel: AppModel,
    private val pushOtpFlowChecker: PushOtpFlowChecker,
    private val smartKeyJoyUseCase: SmartKeyJoyUseCase,
    private val digitalKeyValidationUseCase: DigitalKeyValidationUseCase,
    private val biometricPreRegisterUseCase: BiometricPreRegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val resendOtpUseCase: ResendOtpUseCase
) : NewBaseViewModel(),
    BiometricEnrollmentCallback, BiometricTXPromptCallback {

    internal var otpSent: MutableLiveData<Boolean> = MutableLiveData()
    private val uiModel: DigitalKeyBiometricUiModelV2 = DigitalKeyBiometricUiModelV2()
    private val uiModelLiveData: MutableLiveData<DigitalKeyBiometricUiModelV2> = MutableLiveData()
    private val preRegisterResponseModelLiveData: MutableLiveData<PreRegisterResponseModelV2> =
        MutableLiveData()
    private val preRegisterResponseModel: PreRegisterResponseModelV2 = PreRegisterResponseModelV2()
    private val optOk: MutableLiveData<String> = MutableLiveData()
    private val otpError: MutableLiveData<Throwable> = MutableLiveData()
    private val otpDismiss: MutableLiveData<Boolean> = MutableLiveData()
    private val synchronizeEnrollment: MutableLiveData<Boolean> = MutableLiveData()
    private var instanceReceiver: InstanceReceiver? = null
    private var weakAppContext: WeakReference<Context?> = getEmptyWeak()
    private var biometricConfigurationModel: BiometricConfigurationModel =
        BiometricConfigurationModel()

    fun setUpUi(
        instanceReceiver: InstanceReceiver,
        configurationModel: BiometricConfigurationModel,
        weakAppContext: WeakReference<Context?>
    ) {
        this.instanceReceiver = instanceReceiver
        this.biometricConfigurationModel = configurationModel
        this.weakAppContext = weakAppContext
    }


    override fun onCleared() {
        super.onCleared()
        digitalKeyValidationUseCase.unsubscribe()
        smartKeyJoyUseCase.unsubscribe()
        biometricPreRegisterUseCase.unsubscribe()
        loginUseCase.unsubscribe()
    }

    fun sendOtp() {
        setLoadingV2(true)
        val smartKeySubscriber: BaseSubscriberKt<ErrorMessageOtpEntity> =
            BaseSubscriberKt(::onSuccessGetSmartKey, ::showErrorMessage)
        smartKeyJoyUseCase.smartKey(smartKeySubscriber)
    }

    fun getOtpSent(): LiveData<Boolean> {
        return otpSent
    }

    fun validateOtpCode(code: String?) {
        uiModel.isButtonContinueEnabled = Util.validCode(code)
        uiModel.isShowErrorMessage = !Util.validCode(code)
        uiModelLiveData.value = uiModel
    }

    //send OTP to validate in server side
    fun sendOtp(smartKeyVerifyRequestEntity: SmartKeyVerifyRequestEntity?) {
        setLoadingV2(true)
        val otpSubscriber: BaseSubscriberKt<ErrorMessageOtpEntity> =
            BaseSubscriberKt(::onSuccesOTPSubscriber, ::onError)
        digitalKeyValidationUseCase.verifyOtpPok(
            otpSubscriber,
            smartKeyVerifyRequestEntity
        )
    }

    fun startPreRegisterBiometric(key: String?, deviceId: String?) {
        val subscriber: BaseSubscriberKt<BiometricPreRegisterResponseEntity> =
            BaseSubscriberKt(::processResponsePreRegister, ::onError)
        biometricPreRegisterUseCase.preRegisterBiometric(subscriber, key, deviceId)
    }

    private fun processResponsePreRegister(biometricPreRegisterResponseEntity: BiometricPreRegisterResponseEntity?) {
        if (biometricPreRegisterResponseEntity == null) {
            return
        }
        preRegisterResponseModel.updateModel(biometricPreRegisterResponseEntity)
        preRegisterResponseModelLiveData.value = preRegisterResponseModel
    }

    private fun synchronyEnrollmentBiometric(
        device: RegisterBiometricRequestEntity.Device?,
        configurationModel: BiometricConfigurationModel
    ) {
        val registerBiometricRequestEntity = RegisterBiometricRequestEntity()
        registerBiometricRequestEntity.type = Constant.AUTHENTICATOR_TYPE
        registerBiometricRequestEntity.subtype = Constant.AUTHENTICATOR_SUBTYPE
        registerBiometricRequestEntity.device = device
        registerBiometricRequestEntity.operations = configurationModel.toReversedOperations()
        val subscriber: BaseSubscriberKt<String> = BaseSubscriberKt(::onSuccessSync, ::onErrorSync)
        biometricPreRegisterUseCase.synchronyEnrollmentBiometric(
            subscriber, registerBiometricRequestEntity
        )
    }

    fun updateProfile() {
        val userProfileSubscriber: BaseSubscriberKt<ProfileEntity> =
            BaseSubscriberKt(::onSuccessGetUserProfile, ::onError)
        loginUseCase.userProfile(userProfileSubscriber)
    }

    private fun onSuccessGetUserProfile(profileEntity: ProfileEntity) {
        appModel.profile.isProfileUpdated = true
        if (profileEntity.authenticators != null && profileEntity.authenticators.size > 0) {
            val authenticator = profileEntity.authenticators[0]
            appModel.profile.biometricConfigurationModel =
                authenticator.transformToBiometricConfigurationModel()
        }
    }

    private fun onError(throwable: Throwable) {
        otpError.value = throwable
        showErrorMessage(throwable)
    }

    private fun onSuccesOTPSubscriber(errorMessageOtpEntity: ErrorMessageOtpEntity) {
        if (errorMessageOtpEntity.key.isNotEmpty()) {
            setOptOk(errorMessageOtpEntity.key)
        } else {
            showErrorMessage(IllegalArgumentException())
        }
    }

    private fun onErrorSync(s: Throwable) {
        setSynchronizeEnrollment(false)
    }

    private fun onSuccessSync(s: String) {
        setSynchronizeEnrollment(true)
    }


    fun getUiModelLiveData(): LiveData<DigitalKeyBiometricUiModelV2?> {
        return uiModelLiveData
    }

    fun getOptOk(): LiveData<String> {
        return optOk
    }

    fun setOptOk(key: String) {
        optOk.postValue(key)
    }

    fun getPreRegisterOk(): LiveData<PreRegisterResponseModelV2> {
        return preRegisterResponseModelLiveData
    }

    fun getSynchronizeEnrollment(): LiveData<Boolean> {
        return synchronizeEnrollment
    }

    fun setSynchronizeEnrollment(state: Boolean) {
        synchronizeEnrollment.postValue(state)
    }

    private fun onSuccessGetSmartKey(error: ErrorMessageOtpEntity) {
        otpSent.value = true
    }

    override fun onPokNetworkError(pokNetworkError: PokNetworkError) {
        setLoadingV2(false)
        if (pokNetworkError is PokNetworkError.NetworkNoInternet) {
            val title =
                weakAppContext.get()?.getString(R.string.biometric_title) ?: Constant.STRING_EMPTY
            val messageFrom = weakAppContext.get()?.getMessageFrom(pokNetworkError)
            messageFrom?.let { message ->
                instanceReceiver?.receive(message)
            }
        } else {
            instanceReceiver?.receive(Constant.STRING_EMPTY)
        }
    }

    override fun onBiometricTXPromptError(codeError: Int, s: CharSequence) {
        setLoadingV2(false)
        showErrorPokSnackBar()
    }

    private fun showErrorPokSnackBar() {
        setLoadingV2(false)
        instanceReceiver?.receive(Constant.ERROR_BIOMETRIC)
    }

    override fun onBiometricTXPromptFailed(s: String) {
        instanceReceiver?.receive(Constant.STRING_EMPTY)
        setLoadingV2(false)
    }

    override fun onBiometricTXPromptSuccessful(s: String) {
        setLoadingV2(true)
        val device = RegisterBiometricRequestEntity.Device()
        try {
            device.id = BiometricUtils.getDeviceId(weakAppContext.get())
            device.authenticatorKey = weakAppContext.get()
                ?.let { BiometricUtils.getAuthenticatorKey(it) }
        } catch (e: PokBiometricException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: PokCryptoException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        setLoadingV2(true)
        synchronyEnrollmentBiometric(device, biometricConfigurationModel)
    }

    private fun launchBiometricPrompt(
        model: PreRegisterResponseModelV2,
        biometricManager: BiometricPokManager
    ) {
        try {
            if (model.isNeedRegister) {
                clearBiometric()
                val authenticationFieldModel = EnrollmentFieldModel(
                    appModel.profile.id.toCharArray(),
                    model.correlationId.toCharArray(),
                    model.authCode.toCharArray()
                )
                biometricManager.enrollment(authenticationFieldModel)
            } else {
                biometricManager.launchTXPrompt(pe.com.scotiabank.blpm.android.client.util.Constant.LOGIN_TRX_BIOMETRIC)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            SharedPreferencesUtil.clearValueLoginTrxEnrolled(weakAppContext.get())
            instanceReceiver?.receive(Constant.STRING_EMPTY)
        }
    }

    fun clearBiometric() {
        try {
            BiometricUtils.clearSensitiveBiometricData(weakAppContext.get())
            SharedPreferencesUtil.clearValueLoginTrxEnrolled(weakAppContext.get())
        } catch (e: PokBiometricException) {
            instanceReceiver?.receive(Constant.STRING_EMPTY)
        } catch (e: PokCryptoException) {
            setLoadingV2(false)
            instanceReceiver?.receive(MutableDataOfShowPokCryptoException(e))
        }
    }

    override fun onPokSecurityException(pokSecurityException: PokSecurityException) {
        instanceReceiver?.receive(Constant.STRING_EMPTY)
        wipeMemoryByFinishingProcess()
    }

    fun showBiometricPrompt(model: PreRegisterResponseModelV2) {
        try {
            generateBiometricManager(model)
        } catch (e: PokBiometricException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            SharedPreferencesUtil.clearValueLoginTrxEnrolled(weakAppContext.get())
            instanceReceiver?.receive(Constant.STRING_EMPTY)
        }
    }

    fun generateBiometricManager(model: PreRegisterResponseModelV2) {
        weakAppContext.get()?.let { context ->
            val biometricPromptFieldModel = context.createBiometricPromptFieldModel()
            val biometricManager = BiometricBuilder(context)
                .setPromptFieldModel(biometricPromptFieldModel)
                .setCallback(this)
                .setURL(pe.com.scotiabank.blpm.android.client.util.Constant.EMPTY_STRING)
                .build()
            if (!context.hasEnrolledCamsId(appModel.profile.id)) {
                model.isNeedRegister = true
                clearBiometricInformation(context)
            }
            launchBiometricPrompt(model, biometricManager)
        }
    }

    override fun onPokBiometricException(e: PokBiometricException) {
        showErrorPokSnackBar()
        instanceReceiver?.receive(Constant.STRING_EMPTY)
    }

    override fun onEnrollSuccess() {
        val device = RegisterBiometricRequestEntity.Device()
        try {
            saveEnrollment(device)
        } catch (e: PokBiometricException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: PokCryptoException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        setLoadingV2(true)

        synchronyEnrollmentBiometric(device, biometricConfigurationModel)
    }

    private fun saveEnrollment(device: RegisterBiometricRequestEntity.Device) {
        weakAppContext.get()?.let { context ->
            device.id = BiometricUtils.getDeviceId(context)
            device.authenticatorKey = BiometricUtils.getAuthenticatorKey(context)
            SharedPreferencesUtil.saveLoginTrxEnrolled(
                context,
                BiometricUtils.getDeviceId(context)
            )
        }

    }

    override fun onEnrollFailed(e: PokEnrollmentException?) {
        instanceReceiver?.receive(Constant.STRING_EMPTY)
        // Do Analytics
    }

    override fun onEnrollError(codeError: Int, s: CharSequence?) {
        showErrorPokSnackBar()
    }

    fun getOtpError(): LiveData<Throwable> {
        return otpError
    }

    fun onDismissOtpDialog(dismiss: Boolean) {
        otpDismiss.value = dismiss
    }

    fun getDismissOtp(): LiveData<Boolean> {
        return otpDismiss
    }

    fun cleanViewModel() {
        uiModelLiveData.value = DigitalKeyBiometricUiModelV2()
        otpSent.value = false
        optOk.value = Constant.STRING_EMPTY
        preRegisterResponseModelLiveData.value =
            PreRegisterResponseModelV2().apply { correlationId = Constant.STRING_EMPTY }
    }

    fun resendOtp(otpEntity: OtpEntity) {
        setLoadingV2(true)
        resendOtpUseCase.resendOtp(
            getBaseSubscriberKt { _: ErrorMessageOtpEntity -> onSuccessResendOtpUseCase() },
            otpEntity
        )
    }

    private fun onSuccessResendOtpUseCase() {
        setLoadingV2(false)
    }

    fun isPushOtpEnabled() = pushOtpFlowChecker.isPushOtpEnabled
}
