package pe.com.scotiabank.blpm.android.client.base.receipt

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.BaseBindingFragment
import pe.com.scotiabank.blpm.android.client.base.BindingInflaterOfFragment
import pe.com.scotiabank.blpm.android.client.databinding.FragmentVoucherBinding
import pe.com.scotiabank.blpm.android.client.model.BannerModel
import pe.com.scotiabank.blpm.android.client.model.VoucherBodyModel
import pe.com.scotiabank.blpm.android.client.model.VoucherModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.medallia.util.MedalliaConstants
import pe.com.scotiabank.blpm.android.client.medallia.setup.MedalliaFacade
import pe.com.scotiabank.blpm.android.client.util.formatHtmlText
import pe.com.scotiabank.blpm.android.ui.util.initialize
import javax.inject.Inject

/**
 * Created by Scotiabank Peru.
 */
class VoucherFragment : BaseBindingFragment<FragmentVoucherBinding>() {

    companion object {
        const val VOUCHER_MODEL_KEY = "debit_model_key"
        const val HIDE_EMAIL_SECTION_VOUCHER = "show_only_share"

        @JvmStatic
        fun newInstance(
            voucherModel: VoucherModel?,
            hideEmailSection: Boolean,
            bannerModel: BannerModel?
        ): VoucherFragment {
            val fragment = VoucherFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(VOUCHER_MODEL_KEY, voucherModel)
                putBoolean(HIDE_EMAIL_SECTION_VOUCHER, hideEmailSection)
                putParcelable(Constant.BANNER, bannerModel)
            }
            return fragment
        }
    }

    private val viewModel: VoucherViewModel by lazy {
        ViewModelProvider(this)[VoucherViewModel::class.java]
    }

    /**
     * @return transaction confirmation number
     */
    val transactionConfirmationNumber: String?
        get() = viewModel.voucherModel.value?.operationNumber

    var medalliaFacade: MedalliaFacade? = null
        @Inject set

    override fun getBindingInflater(): BindingInflaterOfFragment<FragmentVoucherBinding> {
        return BindingInflaterOfFragment(FragmentVoucherBinding::inflate)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        medalliaFacade?.sendStartingParameter(MedalliaConstants.CUSTOM_PARAM_VOUCHER_VIEWED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        handleArguments()
    }

    private fun handleArguments() {
        var hideEmailSection = false
        var voucherModel: VoucherModel? = null
        var bannerModel: BannerModel? = null
        arguments?.run {
            voucherModel = getParcelable(VOUCHER_MODEL_KEY)
            hideEmailSection = getBoolean(HIDE_EMAIL_SECTION_VOUCHER)
            bannerModel = getParcelable(Constant.BANNER)
        }
        if (appModel.isOpenedSession) {
            viewModel.setupInformation(voucherModel, hideEmailSection, bannerModel)
        }
    }

    private fun setupObservers() {
        viewModel.bannerModel.observe(viewLifecycleOwner, Observer(::attemptSetupBuddyTip))
        viewModel.hideEmail.observe(viewLifecycleOwner, Observer(::observeHideEmail))
        viewModel.voucherModel.observe(viewLifecycleOwner, Observer(::observeVoucherModel))
    }

    private fun observeVoucherModel(voucherModel: VoucherModel?) {
        if (voucherModel == null) return
        setupHeader(voucherModel)
        setupInformation(voucherModel.body)
    }

    private fun observeHideEmail(hideEmail: Boolean) {
        if (!hideEmail) return
        hideEmail()
    }

    override fun getLayout(): Int = R.layout.fragment_voucher

    /**
     * Setup voucher header
     *
     * @param voucherModel
     */
    private fun setupHeader(voucherModel: VoucherModel) = with(binding) {
        tvVoucherTitle.text = voucherModel.title
        tvDateTransaction.text = voucherModel.transactionDate
        if (voucherModel.operationNumber.isNullOrEmpty()) {
            tvOperationNumber.visibility = View.GONE
        }
        tvOperationNumber.text =
            String.format(getString(R.string.operation_x), voucherModel.operationNumber)
        tvEmail.text = appModel.profile.client?.email
    }

    /**
     * Make voucher
     *
     * @param voucherBodyModels entity to use in voucher building
     */
    private fun setupInformation(voucherBodyModels: List<VoucherBodyModel>) {
        // LinearLayout
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        // TextEtiqueta
        val paramsTextLabel = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        paramsTextLabel.setMargins(0, 0, 10, 0)

        // TextDescripcion
        val paramsTextDescription =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                setMargins(10, 0, 0, 0)
                gravity = Gravity.BOTTOM
            }
        for (voucherBodyModel in voucherBodyModels) {
            addVoucherRow(params, paramsTextLabel, voucherBodyModel, paramsTextDescription)
        }
    }

    private fun addVoucherRow(
        params: LinearLayout.LayoutParams,
        paramsTextLabel: LinearLayout.LayoutParams,
        voucherBodyModel: VoucherBodyModel,
        paramsTextDescription: LinearLayout.LayoutParams
    ) {
        val labelTextView = TextView(context).apply {
            layoutParams = paramsTextLabel
            setTextAppearance(R.style.TextViewLayout_LeftVoucher)
            maxLines = 5
            typeface = appModel.regularTypeface
            text = voucherBodyModel.field.formatHtmlText()
            setTextColor(ContextCompat.getColor(context, com.scotiabank.canvascore.R.color.canvascore_brand_black))
            textSize = 13f
            gravity = Gravity.END
        }
        val valueTextView = TextView(context).apply {
            layoutParams = paramsTextDescription
            setTextAppearance(R.style.TextViewLayout_RightVoucher)
            maxLines = 5
            typeface = appModel.lightTypeface
            text = voucherBodyModel.value.formatHtmlText()
            gravity = Gravity.START
        }
        LinearLayout(context).apply {
            layoutParams = params
            orientation = LinearLayout.HORIZONTAL
            setPadding(10, 10, 10, 10)
            addView(labelTextView)
            addView(valueTextView)
        }.also(binding.llVoucherContent::addView)
    }

    /**
     * Hide email section in voucher
     */
    private fun hideEmail() = with(binding) {
        llPoints.visibility = View.GONE
        llEmailConfirmation.visibility = View.GONE
    }

    private fun attemptSetupBuddyTip(banner: BannerModel?) = banner?.let(::setupBuddyTip)

    private fun setupBuddyTip(banner: BannerModel) = with(binding.btMessage) {
        initialize(banner.bannerMessage, banner.bannerIconId)
        visibility = View.VISIBLE
    }

    init {
        retainInstance = true
    }

    override fun initializeInjector() {
        //unused
    }

    /**
     * Used to get root view to share voucher
     *
     * @return root view
     */
    fun getViewWrapperVoucher(): LinearLayout = binding.llWrapperVoucher
}
