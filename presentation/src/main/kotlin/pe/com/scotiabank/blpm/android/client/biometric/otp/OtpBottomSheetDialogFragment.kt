package pe.com.scotiabank.blpm.android.client.biometric.otp

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.biometric.BiometricManager
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.scotiabank.canvascore.utils.isNotEmptyNorBlank
import com.scotiabank.canvaspe.smartkey.CanvasSmartKey
import com.scotiabank.canvaspe.smartkey.SmartKeyEntity
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.factories.biometric.fullscreen.AnalyticsBiometricFullScreenConstant
import pe.com.scotiabank.blpm.android.analytics.factories.biometric.fullscreen.BiometricFullScreenFactory
import pe.com.scotiabank.blpm.android.analytics.firebase.FirebaseAnalyticsDataGateway
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.confirmation.ConfirmationDataMapper
import pe.com.scotiabank.blpm.android.client.biometric.enrollment.EnrollmentBiometricView
import pe.com.scotiabank.blpm.android.client.biometric.enrollment.EnrollmentBiometricViewModel
import pe.com.scotiabank.blpm.android.client.biometric.enrollment.EnrollmentBiometricViewModelFactory
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.digitalkeybiometricV2.DigitalKeyBiometricUiModelV2
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.digitalkeybiometricV2.PreRegisterResponseModelV2
import pe.com.scotiabank.blpm.android.client.databinding.BottomModalOtpBinding
import pe.com.scotiabank.blpm.android.client.model.biometric.BiometricConfigurationModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.SharedPreferencesUtil
import pe.com.scotiabank.blpm.android.client.util.Util
import pe.com.scotiabank.blpm.android.client.util.lifecycle.LifecycleUtil
import pe.com.scotiabank.blpm.android.data.entity.OtpEntity
import pe.com.scotiabank.blpm.android.data.entity.SmartKeyVerifyRequestEntity
import javax.inject.Inject


class OtpBottomSheetDialogFragment : BottomSheetDialogFragment(),
    CanvasSmartKey.CanvasSmartKeyListener {

    companion object {
        const val CONFIG_MODEL = "config_model"
        const val FUNC_TYPE = "func_type"
        const val PREVIOUS_SECTION = "previous_section"
        const val DARK = "globo-negro"
        const val DELAY_ANIMATION_SUCCESS = 2200.toLong()

        fun newInstance(args: Bundle?): OtpBottomSheetDialogFragment =
            OtpBottomSheetDialogFragment().apply {
                arguments = args
            }
    }

    private var smartKey: List<SmartKeyEntity> = emptyList()

    @Inject
    lateinit var enrollmentBiometricViewModelFactory: EnrollmentBiometricViewModelFactory

    @Inject
    lateinit var appModel: AppModel

    @Inject
    lateinit var biometricFullScreenFactory: BiometricFullScreenFactory

    private val analyticsDataGateway: AnalyticsDataGateway by lazy {
        FirebaseAnalyticsDataGateway(
            FirebaseAnalytics.getInstance(requireActivity()),
            requireActivity()
        )
    }

    private lateinit var configurationModel: BiometricConfigurationModel

    private var functionalType = AnalyticsBiometricFullScreenConstant.FunctionType.POK_ACTIVATE

    private var previousSection: String? = null

    private lateinit var binding: BottomModalOtpBinding

    private lateinit var viewModel: EnrollmentBiometricViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomModalOtpBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
        configurationModel = requireArguments().get(CONFIG_MODEL) as BiometricConfigurationModel
        functionalType = requireArguments().getString(FUNC_TYPE) ?: Constant.EMPTY_STRING
        previousSection = requireArguments().getString(PREVIOUS_SECTION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            enrollmentBiometricViewModelFactory
        )[EnrollmentBiometricViewModel::class.java]
        LifecycleUtil.runOnResume(this, ::setupAnalytics)
        setCskOptions()
        setUpListeners()
        setUpObservers()
    }

    private fun setupAnalytics() {
        analyticsDataGateway.setCurrentScreen(BiometricFullScreenFactory.SCREEN_NAME_OTP)
        biometricFullScreenFactory.run {
            createOtoBottomSheetLoadScreen().let(analyticsDataGateway::sendEventV2)
            createOtpBottomSheetLoadScreenEvent(functionalType, previousSection).let(
                analyticsDataGateway::sendEventV2
            )
        }
    }

    private fun sendOnClickEvent(eventLabel: String) = with(biometricFullScreenFactory) {
        createOnClickOtpBottomSheetEvent(
            eventLabel,
            functionalType,
            previousSection
        ).let(analyticsDataGateway::sendEventV2)
    }

    private fun sendOnSuccessEvent() = with(biometricFullScreenFactory) {
        createOnSuccessOtpBottomSheetEvent(
            functionalType,
            previousSection
        ).let(analyticsDataGateway::sendEventV2)
    }

    private fun setCskOptions() {
        if (appModel.isOpenedSession) {
            smartKey = ConfirmationDataMapper.transformFactorModels(appModel.profile.factorModels)
            binding.cskKey.setOptions(smartKey)
            binding.cskKey.setCanvasSmartKeyListener(this)
            val actualAuth = smartKey.first { key -> key.isDefaultAuth }
            binding.tvDescription.text =
                getString(R.string.biometric_otp_bottomsheet_message, actualAuth.value)
        }
    }

    private fun setUpObservers() {
        viewModel.getUiModelLiveData().observe(this) { uiModel: DigitalKeyBiometricUiModelV2? ->
            observeUiModel(uiModel)
        }
        viewModel.getOptOk().observe(this) { key: String ->
            startPreRegister(key)
        }
        viewModel.getPreRegisterOk().observe(this) { model: PreRegisterResponseModelV2 ->
            showBiometricPrompt(model)
        }
    }

    private fun showBiometricPrompt(model: PreRegisterResponseModelV2) {
        if (model.correlationId.isBlank()) {
            return
        }
        viewModel.setLoadingV2(false)
        binding.success.isVisible = true
        sendOnSuccessEvent()
        lifecycleScope.launch(Dispatchers.Main) {
            delay(DELAY_ANIMATION_SUCCESS)
            if (isBiometricAvailable()) {
                (requireActivity() as? EnrollmentBiometricView)?.showBiometricPrompt(model)
            }
            dismiss()
        }
    }

    private fun isBiometricAvailable(): Boolean {
        val statusCodeBiometric: Int = BiometricManager.from(requireContext()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        return statusCodeBiometric == BiometricManager.BIOMETRIC_SUCCESS
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    private fun setUpListeners() {
        binding.cskKey.setCanvasChangedTextWatcher { _, _, _, _, _ ->
            viewModel.validateOtpCode(
                binding.cskKey.code.trim()
            )
        }
        binding.cskKey.editText.setOnEditorActionListener { view, actionId, _ ->
            onImeActionPressed(
                view,
                actionId
            )
        }
        binding.buttonClose.setOnClickListener {
            sendOnClickEvent(AnalyticsBiometricFullScreenConstant.EventLabel.CLOSE)
            viewModel.onDismissOtpDialog(true)
            dismiss()

        }
    }

    private fun onImeActionPressed(view: View, actionId: Int): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Util.hideKeyboard(requireContext(), view)
        }
        return false
    }

    private fun observeUiModel(uiModel: DigitalKeyBiometricUiModelV2?) {
        if (uiModel == null) return
        binding.cskKey.setErrorMessage(false, Constant.EMPTY_STRING)
        binding.cskKey.setErrorMessage(
            uiModel.isShowErrorMessage,
            getErrorMessage(uiModel)
        )
        if (uiModel.isButtonContinueEnabled) {
            onContinueOTPValidation()
        }
    }

    private fun getErrorMessage(uiModel: DigitalKeyBiometricUiModelV2): String? {
        return if (uiModel.isShowErrorMessage) getString(R.string.smart_key_intent_1) else Constant.EMPTY_STRING
    }

    private fun startPreRegister(key: String) {
        val deviceId = SharedPreferencesUtil.getValueLoginTrxEnrolled(requireActivity())
        if (key.isNotEmptyNorBlank()) {
            viewModel.startPreRegisterBiometric(
                key,
                deviceId ?: Constant.EMPTY_STRING
            )
        }
    }

    private fun onContinueOTPValidation() {
        Util.hideKeyboard(requireContext(), view)
        val requestEntity = SmartKeyVerifyRequestEntity()
        requestEntity.otpCode = binding.cskKey.code.trim()
        sendOnClickEvent(AnalyticsBiometricFullScreenConstant.EventLabel.SMARTKEY)
        viewModel.sendOtp(requestEntity)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        binding.cskKey.editText.setText(Constant.EMPTY_STRING)
        viewModel.onDismissOtpDialog(true)
    }

    override fun sendOtp(option: String?, otp: String?) {
        if (option == null) {
            return
        }
        val eventLabel = getEventLabel(option)
        sendOnClickEvent(eventLabel)
        OtpEntity().apply {
            channel = option
            operation = Constant.IDENTITY
        }.also { otpEntity -> viewModel.resendOtp(otpEntity) }
    }

    private fun getEventLabel(option: String): String {
        return when (option) {
            Constant.PHONE_OTP -> AnalyticsBiometricFullScreenConstant.EventLabel.SEND_PHONE
            Constant.EMAIL_OTP -> AnalyticsBiometricFullScreenConstant.EventLabel.SEND_EMAIL
            else -> Constant.EMPTY_STRING
        }
    }

}
