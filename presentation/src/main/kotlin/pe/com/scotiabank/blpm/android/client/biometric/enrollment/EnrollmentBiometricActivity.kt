package pe.com.scotiabank.blpm.android.client.biometric.enrollment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.scotiabank.enhancements.handling.*
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.factories.biometric.fullscreen.AnalyticsBiometricFullScreenConstant
import pe.com.scotiabank.blpm.android.analytics.factories.biometric.fullscreen.BiometricFullScreenFactory
import pe.com.scotiabank.blpm.android.analytics.firebase.FirebaseAnalyticsDataGateway
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.BaseBindingActivity
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfActivity
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.configurationV2.BiometricConfigurationActivityV2
import pe.com.scotiabank.blpm.android.client.biometric.enrollmentV2.digitalkeybiometricV2.PreRegisterResponseModelV2
import pe.com.scotiabank.blpm.android.client.biometric.otp.OtpBottomSheetDialogFragment
import pe.com.scotiabank.blpm.android.client.biometric.util.createBannerMessage
import pe.com.scotiabank.blpm.android.client.databinding.ActivityEnrollmentBiometricBinding
import pe.com.scotiabank.blpm.android.client.model.biometric.BiometricConfigurationModel
import pe.com.scotiabank.blpm.android.client.profilesettings.security.MyAccountSecurityActivity
import pe.com.scotiabank.blpm.android.client.util.*
import pe.com.scotiabank.blpm.android.client.util.biometric.*
import pe.com.scotiabank.blpm.android.client.util.biometric.BiometricErrorMessageFactory.getMessageFrom
import pe.com.scotiabank.blpm.android.client.util.biometric.BiometricErrorMessageFactory.getMessageFromSecurityApi
import pe.com.scotiabank.blpm.android.client.util.exception.ErrorMessageFactory
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.listener.AlertDialogListener
import pe.com.scotiabank.blpm.android.data.domain.exception.DefaultErrorBundle
import pe.com.scotiabank.blpm.android.data.domain.exception.ErrorBundle
import pe.com.scotiabank.blpm.android.client.util.lifecycle.LifecycleUtil
import pe.com.scotiabank.blpm.android.ui.util.initialize
import java.lang.ref.WeakReference
import javax.inject.Inject

class EnrollmentBiometricActivity : BaseBindingActivity<ActivityEnrollmentBiometricBinding>(),
    AlertDialogListener, EnrollmentBiometricView {

    companion object {
        const val KEY_BIOMETRIC_FUNCTIONAL_TYPE = "KEY_BIOMETRIC_FUNCTIONAL_TYPE"
        const val KEY_BIOMETRIC_ANALYTICS_MESSAGE = "KEY_BIOMETRIC_ANALYTICS_MESSAGE"
    }

    @Inject
    lateinit var enrollmentBiometricViewModelFactory: EnrollmentBiometricViewModelFactory

    private var configurationModel: BiometricConfigurationModel? = null
    private var originKeyBiometric: String = Constant.EMPTY_STRING

    @Inject
    lateinit var biometricFullScreenFactory: BiometricFullScreenFactory

    private val analyticsDataGateway: AnalyticsDataGateway by lazy {
        FirebaseAnalyticsDataGateway(FirebaseAnalytics.getInstance(this), this)
    }

    private val viewModel: EnrollmentBiometricViewModel by lazy {
        ViewModelProvider(
            this,
            enrollmentBiometricViewModelFactory
        )[EnrollmentBiometricViewModel::class.java]
    }

    private val weakAppContext: WeakReference<Context?> by lazy {
        WeakReference(this)
    }

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            String::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showErrorPokSnackBar)
        )
        .add(
            MutableDataOfShowPokCryptoException::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showPokCryptoException)
        )
        .build()

    private val instanceReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private var functionalType = AnalyticsBiometricFullScreenConstant.FunctionType.POK_ACTIVATE

    override fun getToolbarTitle(): String = Constant.EMPTY_STRING

    override fun getBindingInflater(): BindingInflaterOfActivity<ActivityEnrollmentBiometricBinding> {
        return BindingInflaterOfActivity(ActivityEnrollmentBiometricBinding::inflate)
    }

    override fun additionalInitializer() {
        SharedPreferencesUtil.setIsPOKFullscreenShowed(this, true)
        setAlertDialogListener(this)
        setupListeners()
        configurationModel =
            intent.getParcelableFrom(BiometricConfigurationActivityV2.ENROLLMENT_CONFIG)
        viewModel.setUpUi(instanceReceiver, getConfigurationModel(), weakAppContext)
        originKeyBiometric =
            intent.getStringExtra(BiometricConstant.ORIGIN_KEY_BIOMETRIC) ?: Constant.EMPTY_STRING
        setTitleText()
        setupObservers()
        setupBuddyTip()
        LifecycleUtil.runOnResume(this, ::setupAnalytics)

    }

    override fun onResume() {
        super.onResume()
        biometricFullScreenFactory.createLoadScreenEvent(functionalType)
            .let(analyticsDataGateway::sendEventV2)
    }

    private fun setupAnalytics() {
        analyticsDataGateway.setCurrentScreen(BiometricFullScreenFactory.SCREEN_NAME_INIT)
        biometricFullScreenFactory.run {
            createLoadScreen().let(analyticsDataGateway::sendEventV2)
        }
    }

    private fun setupBuddyTip() {
        val btPokAdvertisement = binding.btPokAdvertisement
        val descriptionBuilder: SpannableStringBuilder = createBannerMessage(appModel)
        btPokAdvertisement.initialize(descriptionBuilder, R.drawable.ic_il_announcement)
        btPokAdvertisement.visibility = View.VISIBLE
    }

    private fun getConfigurationModel(): BiometricConfigurationModel {
        return configurationModel ?: BiometricConfigurationModel()
    }

    private fun setTitleText() {
        if (viewModel.isPushOtpEnabled()) {
            binding.ctvTitle.text = getString(R.string.biometric_fullscreen_title_push_otp)
            binding.ctvDescription.text = getString(R.string.biometric_fullscreen_sub_title_push_otp)
        } else {
            binding.ctvTitle.text = getString(R.string.biometric_fullscreen_title)
            binding.ctvDescription.text = getString(R.string.biometric_fullscreen_sub_title)
        }
        val isLoginModified = baseContext.wasLoginModified(status = true)
        if (!isLoginModified) {
            //user already has login biometric
            functionalType = AnalyticsBiometricFullScreenConstant.FunctionType.CONFIRM_OPERATIONS
            binding.ctvTitle.text = getString(R.string.biometric_fullscreen_title_tx)
            binding.ctvDescription.text = getString(R.string.biometric_fullscreen_sub_title_tx)
        }
    }

    private fun setupObservers() {
        viewModel.getLoadingV2().observe(this) { loadingV2: EventWrapper<Boolean>? ->
            observeShowHideLoading(loadingV2)
        }
        viewModel.getOtpSent().observe(this) { otpSent: Boolean ->
            showOtpPrompt(otpSent)
        }
        viewModel.getSynchronizeEnrollment().observe(this) { status: Boolean ->
            synchronizeResult(status)
        }
        viewModel.getOtpError().observe(this) { error ->
            observeErrorAuthValidationRequest(error)
            activateContinueButton(true)
            viewModel.cleanViewModel()
            showErrorMessage(error)
        }
        viewModel.getDismissOtp().observe(this) {
            activateContinueButton(true)
            sendOnClickEvent(AnalyticsBiometricFullScreenConstant.EventLabel.CLOSE)
        }
    }

    private fun sendOnClickEvent(eventLabel: String) = with(biometricFullScreenFactory) {
        createOnClickCloseEvent(eventLabel, functionalType).let(analyticsDataGateway::sendEventV2)
    }

    private fun sendOnClickContinueEvent() = with(biometricFullScreenFactory) {
        createOnClickContinueEvent(functionalType).let(analyticsDataGateway::sendEventV2)
    }

    private fun observeErrorAuthValidationRequest(throwable: Throwable) {
        val errorBundle: ErrorBundle = DefaultErrorBundle(throwable as Exception)
        val retrofitException =
            ErrorMessageFactory.createWithCode(applicationContext, errorBundle.exception)
        val errorMessage: String = applicationContext.getMessageFromSecurityApi(retrofitException)
        val errorCode = retrofitException.responseCode
        setDialogTypeError(errorCode, errorMessage)
    }

    private fun setDialogTypeError(errorCode: String, errorMessage: String) {
        when (errorCode) {
            Constant.AUTH_ERR_023, Constant.AUTH_ERR_026, Constant.ERROR_THIRD_TRY -> {
                showMessageDialogInterface(null, errorMessage, Constant.FINISH_CODE)
            }
            else -> showMessageDialog(errorMessage)
        }
    }

    override fun onClickPositive(code: Int) {
        if (code == Constant.FINISH_CODE && !isFinishing) {
            finish()
        }
    }

    override fun getSettingToolbar(): Boolean = false

    private fun setupListeners() {
        binding.ctvSkip.setOnClickListener {
            sendOnClickEvent(AnalyticsBiometricFullScreenConstant.EventLabel.CLOSE)
            finish()
        }
        binding.cbActivate.setOnClickListener { onClickContinueButton() }
        binding.btPokAdvertisement.setOnClickListener { onClickTermsAndConditions() }
    }

    private fun onClickContinueButton() {
        activateContinueButton(false)
        sendOnClickContinueEvent()
        viewModel.sendOtp()
    }

    private fun showOtpPrompt(otpSent: Boolean) {
        if (!otpSent) return
        viewModel.setLoadingV2(false)
        val status = true // always set true to biometric
        getConfigurationModel().isLoginEnabled = status
        getConfigurationModel().isTxEnabled = status
        activateContinueButton(false)
        showBottomSheet()
    }

    private fun showBottomSheet() {
        val bundle = Bundle()
        bundle.putParcelable(OtpBottomSheetDialogFragment.CONFIG_MODEL, configurationModel)
        bundle.putString(OtpBottomSheetDialogFragment.FUNC_TYPE, functionalType)
        val bottomSheetDialogFragment: OtpBottomSheetDialogFragment =
            OtpBottomSheetDialogFragment.newInstance(bundle)
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun onClickTermsAndConditions() {
        sendOnClickEvent(AnalyticsBiometricFullScreenConstant.EventLabel.TERMS_CONDITIONS)
        val url: String = getString(R.string.url_biometric_terms_conditions_v2)
        val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            .setInstantAppsEnabled(true)
            .build()
        val uri: Uri = Uri.parse(url)
        customTabsIntent.launchUrl(this, uri)
    }

    override fun showBiometricPrompt(model: PreRegisterResponseModelV2?) {
        activateContinueButton(false)
        model?.let(viewModel::showBiometricPrompt)
    }

    private fun synchronizeResult(status: Boolean) {
        viewModel.setLoadingV2(false)
        if (status) {
            saveCurrentEnrollment(getConfigurationModel())
            SharedPreferencesUtil.setEnrollWithNewPOKVersion(this, true)
            goToDashboardActivity()
        } else {
            showMessageDialog(
                getString(R.string.biometric_prompt_enrollment_synchronize_error),
                getString(R.string.biometric_title)
            )
            viewModel.clearBiometric()
        }
        viewModel.updateProfile()
    }

    private fun goToDashboardActivity() {
        val intent = Intent()
        val messageSnack = getAppropriateSnackBarMessage()
        val analyticsMessage = getAppropriateAnalyticsMessage()
        intent.putExtra(MyAccountSecurityActivity.KEY_BIOMETRIC_CONFIG_MSG, messageSnack)
        intent.putExtra(MyAccountSecurityActivity.KEY_BIOMETRIC_CONFIG_STATUS, true)
        intent.putExtra(KEY_BIOMETRIC_FUNCTIONAL_TYPE, functionalType)
        intent.putExtra(KEY_BIOMETRIC_ANALYTICS_MESSAGE, analyticsMessage)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun getAppropriateSnackBarMessage() : String =
        if (functionalType == AnalyticsBiometricFullScreenConstant.FunctionType.CONFIRM_OPERATIONS) {
            getString(R.string.message_fingerprint_enabled_tx)
        } else {
            getString(R.string.message_fingerprint_enabled_both)
        }

    private fun getAppropriateAnalyticsMessage(): String =
        if (functionalType == AnalyticsBiometricFullScreenConstant.FunctionType.CONFIRM_OPERATIONS) {
            AnalyticsBiometricFullScreenConstant.Message.CONFIRM_OPERATIONS
        } else {
            AnalyticsBiometricFullScreenConstant.Message.POK_ACTIVATE
        }


    private fun showPokCryptoException(e: MutableDataOfShowPokCryptoException) {
        activateContinueButton(true)
        val message: String = this.getMessageFrom(e.exception)
        showMessageDialog(message, getString(R.string.biometric_title))
    }

    private fun showErrorPokSnackBar(message: String) {
        viewModel.cleanViewModel()
        activateContinueButton(true)
        callCanvasBiometricSnackBarBottom(
            binding.mainScrollContainer,
            status = false,
            message = getString(R.string.biometric_enrollment_failed)
        )
    }
    private fun activateContinueButton(activate: Boolean) {
        binding.cbActivate.isEnabled = activate
    }
}
