package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.non

import android.annotation.SuppressLint
import android.content.Intent
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.recyclerview.widget.RecyclerView
import com.scotiabank.canvascore.dialog.CanvasDialogModal
import com.scotiabank.canvaspe.confirmation.entity.CanvasConfirmationEntity
import com.scotiabank.canvaspe.edittext.CanvasEditText
import com.scotiabank.enhancements.handling.*
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.BaseBindingActivity
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfActivity
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.ComposerOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.bottomsheet.list.StaticDataOfBottomSheetList
import pe.com.scotiabank.blpm.android.client.base.carrier.CarrierOfActivityDestination
import pe.com.scotiabank.blpm.android.client.base.carrier.feedFrom
import pe.com.scotiabank.blpm.android.client.base.carrier.putAllFrom
import pe.com.scotiabank.blpm.android.client.base.dialog.StaticDataCanvasDialogModal
import pe.com.scotiabank.blpm.android.client.databinding.ActivityNonTokenValidationBinding
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.ConfirmationViewModel
import pe.com.scotiabank.blpm.android.client.payment.institutions.ConfirmationViewModelFactory
import pe.com.scotiabank.blpm.android.client.transfer.TransferEvent
import pe.com.scotiabank.blpm.android.client.util.PatternFilter
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.color.ColorUtil
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.filterInAnySubType
import pe.com.scotiabank.blpm.android.client.util.listener.AlertDialogListener
import javax.inject.Inject

class NonTokenValidationActivity : BaseBindingActivity<ActivityNonTokenValidationBinding>() {

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

    override fun getBindingInflater(): BindingInflaterOfActivity<ActivityNonTokenValidationBinding> {
        return BindingInflaterOfActivity(ActivityNonTokenValidationBinding::inflate)
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

    private fun observeEnablingOfConfirmButton(enable: Boolean) {
        binding.cbConfirm.isEnabled = enable
    }

    private fun setUpListeners() {
        setAlertDialogListener(AlertDialogListener(::onClickPositive))
        setErrorDialogListener(AlertDialogListener(::onClickPositive))
        binding.cbConfirm.setOnClickListener(OnClickListener(::onConfirmClicked))
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

    @Suppress("UNUSED_PARAMETER")
    private fun onConfirmClicked(view: View) {
        viewModel.onConfirmClicked(binding.cc.operationDescription)
    }

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
