package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.recyclerview.widget.RecyclerView
import com.scotiabank.canvascore.dialog.CanvasDialogModal
import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import com.scotiabank.canvaspe.edittext.CanvasEditText
import com.scotiabank.canvaspe.listener.CanvasChangedTextWatcher
import com.scotiabank.canvaspe.smartkey.CanvasSmartKey.CanvasSmartKeyListener
import com.scotiabank.canvaspe.smartkey.SmartKeyEntity
import com.scotiabank.enhancements.handling.*
import com.scotiabank.proofofkey.auth.core.base.BiometricBuilder
import com.scotiabank.proofofkey.auth.core.base.BiometricPokManager
import com.scotiabank.proofofkey.auth.core.entities.BiometricPromptFieldModel
import com.scotiabank.proofofkey.auth.utilities.constant.ConstantBiometric
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokBiometricException
import com.scotiabank.proofofkey.auth.utilities.error.exception.PokCryptoException
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.BaseBindingActivity
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfActivity
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.ComposerOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.feedFrom
import pe.com.scotiabank.blpm.android.client.base.carrier.putAllFrom
import pe.com.scotiabank.blpm.android.client.base.dialog.StaticDataCanvasDialogModal
import pe.com.scotiabank.blpm.android.client.base.operation.token.validation.ValidationIntention
import pe.com.scotiabank.blpm.android.client.databinding.ActivityPersonalTokenValidationBinding
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.model.security.SecurityAuthModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.ConfirmationViewModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.ConfirmationViewModelFactory
import pe.com.scotiabank.blpm.android.client.transfer.TransferEvent
import pe.com.scotiabank.blpm.android.client.util.PatternFilter
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.color.ColorUtil
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.listener.AlertDialogListener
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.util.initialize
import javax.inject.Inject

class PersonalTokenValidationActivity : BaseBindingActivity<ActivityPersonalTokenValidationBinding>() {

    private val creationExtras = MutableCreationExtras()

    @Inject
    lateinit var confirmationViewModelFactory: ConfirmationViewModelFactory
    private val confirmationViewModel: ConfirmationViewModel by viewModels(
        extrasProducer = ::creationExtras,
        factoryProducer = ::confirmationViewModelFactory,
    )

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ValidationViewModel<out Any, out BaseSummaryModel> by viewModels(
        extrasProducer = ::creationExtras,
        factoryProducer = ::viewModelFactory,
    )

    private val handlingStore: HandlingStore = HandlingStore.Builder()
        .add(
            SecurityAuthModel::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showBiometricPrompt)
        )
        .add(
            DataOfAuthErrorDialog::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::openDialogOfBiometricError)
        )
        .add(
            ValidationIntention::class,
            InstancePredicate(::filterInGrabOtpValue),
            InstanceHandler(::grabOtpValue)
        )
        .add(
            ValidationIntention::class,
            InstancePredicate(::filterInShowOtpMessage),
            InstanceHandler(::showOtpMessage)
        )
        .add(
            CarrierOfActivityDestination::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::goToActivityDestination)
        )
        .add(
            StaticDataCanvasDialogModal::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showCanvasDialogModalFinishOperation)
        )
        .add(
            StaticDataOfBottomSheetList::class,
            InstancePredicate(::filterInAnySubType),
            InstanceHandler(::showDetailDialog)
        )
        .build()
    private val selfReceiver: InstanceReceiver = InstanceReceivingAgent(handlingStore)

    private val smartTokenWatcher: CanvasChangedTextWatcher = CanvasChangedTextWatcher(
        ::onSmartKeyEntered
    )

    override fun getBindingInflater(): BindingInflaterOfActivity<ActivityPersonalTokenValidationBinding> {
        return BindingInflaterOfActivity(ActivityPersonalTokenValidationBinding::inflate)
    }

    override fun getToolbarTitle(): String = getString(TITLE)

    override fun additionalInitializer() {
        creationExtras.feedFrom(intent)
        setUpObservers()
        setUpListeners()
        confirmationViewModel.setUpUi(selfReceiver)
        confirmationViewModel.setUpFromNew()
        viewModel.setUpUi(selfReceiver)
        setUpOnBackPressedDispatcher()
    }

    private fun setUpObservers() {
        viewModel.getLoadingV2().observe(this, Observer(::observeShowHideLoading))
        viewModel.getErrorMessageV2().observe(this, Observer(::observeErrorMessage))
        viewModel.liveDataOfCanvasEntity.observe(this, Observer(::observeCanvasEntity))
        viewModel.liveDataOfSmartKeyEntities.observe(this, Observer(::observeSmartKeyEntities))
        viewModel.liveEntityOfBiometricBuddyTip.observe(this, Observer(::observeEntityOfBiometricBuddyTip))
        viewModel.liveVisibilityOfBiometricBuddyTip.observe(this, Observer(::observeVisibilityOfBiometricBuddyTip))
        viewModel.liveEnablingOfConfirmButton.observe(this, Observer(::observeEnablingOfConfirmButton))
    }

    private fun setUpOnBackPressedDispatcher() {
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBackEvent()
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun handleBackEvent() {
        appModel.receiveEvent(TransferEvent.SCREEN)
        finish()
    }

    private fun observeCanvasEntity(entity: CanvasConfirmationEntity) {
        if (entity.isShowSeeDetail) {
            binding.cc.setEntity(entity, OnClickListener(::onSeeDetailClicked))
        } else {
            binding.cc.setEntity(entity)
        }
        binding.cc.setFontFamily(appModel.lightTypeface)
        binding.cc.changeWarningTextColor(ColorUtil.getBlackColor(applicationContext))
        setUpOperationDescription()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSeeDetailClicked(view: View) {
        confirmationViewModel.onDetailClicked()
    }

    private fun setUpOperationDescription() {
        val inputFilters: Array<InputFilter> = arrayOf(
            InputFilter.LengthFilter(30),
            PatternFilter(regex = Constant.ALPHANUMERIC_SPACE_ACCENTS_PATTERN.toRegex()),
        )
        val cetOperationDescription: CanvasEditText = binding.cc.findViewById(R.id.cet_operation_description)
        cetOperationDescription.setCanvasMaxLength(30, inputFilters)
    }

    private fun observeSmartKeyEntities(entities: List<SmartKeyEntity>) {
        if (entities.isEmpty()) {
            binding.cc.setDigitalKeyListener(entities, null)
            return
        }
        binding.cc.setDigitalKeyListener(entities, CanvasSmartKeyListener(::requestNewOtpValueFor))
    }

    private fun observeEntityOfBiometricBuddyTip(entity: UiEntityOfBuddyTip) {
        binding.btPokAdvertisement.initialize(entity.descriptionBuilder, entity.iconRes)
    }

    private fun observeVisibilityOfBiometricBuddyTip(isToBeShown: Boolean) {
        binding.btPokAdvertisement.visibility = if (isToBeShown) View.VISIBLE else View.GONE
    }

    private fun observeEnablingOfConfirmButton(enable: Boolean) {
        binding.cbConfirm.isEnabled = enable
    }

    private fun setUpListeners() {
        setAlertDialogListener(AlertDialogListener(::onClickPositive))
        setErrorDialogListener(AlertDialogListener(::onClickPositive))
        binding.cc.setCanvasChangedTextWatcher(smartTokenWatcher)
        binding.cbConfirm.setOnClickListener(OnClickListener(::onConfirmClicked))
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onConfirmClicked(view: View) {
        viewModel.onConfirmClicked(binding.cc.operationDescription)
    }

    /***
     * Input: SecurityAuthModel having a String nonce to sign with private key
     * (Customer should have an enrollment)
     * Show: BiometricPrompt Dialog. Client should use launchTXPrompt just to sign the nonce.
     * Then it will implement 3 methods: onBiometricTXPromptError, onBiometricTXPromptFailed
     * and onBiometricTXPromptSuccessful
     */
    private fun showBiometricPrompt(securityAuth: SecurityAuthModel) {
        val biometricPromptFieldModel = BiometricPromptFieldModel(
            getString(R.string.biometric_transfer_other_account_prompt_title),
            Constant.EMPTY_STRING,
            getString(R.string.biometric_transfer_other_account_prompt_description),
            getString(R.string.biometric_cancel_button)
        )
        try {
            val biometricManager: BiometricPokManager = BiometricBuilder(this)
                .setPromptFieldModel(biometricPromptFieldModel)
                .setCallback(viewModel)
                .setURL(Constant.EMPTY_STRING)
                .build()
            biometricManager.launchTXPrompt(securityAuth.nonce)
        } catch (exception: PokBiometricException) {
            viewModel.onTriedShowingBiometricPrompt(exception)
        } catch (exception: PokCryptoException) {
            viewModel.onTriedShowingBiometricPrompt(exception)
        }
    }

    private fun openDialogOfBiometricError(data: DataOfErrorDialog) {
        AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
            .setTitle(data.title)
            .setMessage(data.message)
            .setPositiveButton(data.textForPositiveButton) { _: DialogInterface?, _: Int ->
                onAcceptClicked(data.errorCode)
            }
            .setCancelable(false)
            .show()
    }

    private fun onAcceptClicked(errorCode: Int) {
        when (errorCode) {
            ConstantBiometric.FINGERPRINT_WAS_MODIFIED -> finish()
            Constant.BIOMETRIC_WAS_CHANGED -> finish()
            Constant.FINISH_CODE -> finish()
            ConstantBiometric.FINGERPRINT_WAS_CANCELED_BY_MANY_ATTEMPTS -> viewModel.changeToOtpTokenType()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onSmartKeyEntered(p0: View?, p1: CharSequence?, p2: Int, p3: Int, p4: Int) {
        viewModel.onSmartKeyEntered(binding.cc.isDigitalKeyValid)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun requestNewOtpValueFor(option: String, otp: String) {
        viewModel.requestNewOtpValueFor(option)
    }

    private fun filterInGrabOtpValue(
        intention: ValidationIntention,
    ): Boolean = ValidationIntention.GRAB_OTP_VALUE == intention

    @Suppress("UNUSED_PARAMETER")
    private fun grabOtpValue(intention: ValidationIntention) {
        viewModel.confirmWithOtp(binding.cc.digitalKey)
    }

    private fun filterInShowOtpMessage(
        intention: ValidationIntention,
    ): Boolean = ValidationIntention.SHOW_OTP_MESSAGE == intention

    @Suppress("UNUSED_PARAMETER")
    private fun showOtpMessage(intention: ValidationIntention) {
        showMessageDialog(binding.cc.showDigitalKeyMessage())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (android.R.id.home == item.itemId) {
            viewModel.onBack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    private fun showCanvasDialogModalFinishOperation(staticDataCanvasDialogModal: StaticDataCanvasDialogModal) {
        CanvasDialogModal.newInstance(staticDataCanvasDialogModal.attrsCanvasDialogModal).apply {
            primaryButtonEvent = {
                staticDataCanvasDialogModal.callbackOfPrimaryButton?.run()
                finish()
            }
        }.also { canvasDialogModal ->
            canvasDialogModal.show(supportFragmentManager, CanvasDialogModal.TAG_NAME)
        }
    }

    @SuppressLint("InflateParams")
    private fun showDetailDialog(data: StaticDataOfBottomSheetList) {
        val layoutInflater: LayoutInflater = LayoutInflater.from(this)
        val rvBsItems: RecyclerView = layoutInflater
            .inflate(R.layout.recycler_view_material, null) as? RecyclerView
            ?: return
        ComposerOfBottomSheetList.compose(
            recyclerView = rvBsItems,
            fragmentManager = supportFragmentManager,
            staticData = data,
            liveCompounds = confirmationViewModel.liveCompounds,
            horizontalPaddingRes = com.scotiabank.canvascore.R.dimen.canvascore_margin_12,
        )
    }

    private fun onClickPositive(code: Int) {
        if (isBackAllowable(code)) finish()
    }

    private fun isBackAllowable(code: Int): Boolean = Constant.BACK_CODE == code && !isFinishing

    private fun goToActivityDestination(carrier: CarrierOfActivityDestination) {
        val intent = Intent(this, carrier.screenDestination)
            .putAllFrom(carrier)

        startActivity(intent)
    }

    companion object {

        private val TITLE: Int
            get() = R.string.title_activity_confirmation
    }
}
