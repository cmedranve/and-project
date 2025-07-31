package pe.com.scotiabank.blpm.android.client.base.receipt

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import pe.com.scotiabank.blpm.android.analytics.AnalyticsDataGateway
import pe.com.scotiabank.blpm.android.analytics.AnalyticsEvent
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.analytics.factories.receipt.mylist.ReceiptMyListFactory
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.databinding.DialogAddListBinding
import pe.com.scotiabank.blpm.android.client.util.UiUtil
import java.lang.ref.WeakReference

class AddListDialog : DialogFragment() {

    companion object {

        private const val OPERATION_NAME_TYPE = "OPERATION_NAME_TYPE"
        private const val OPERATION_TITLE = "OPERATION_TITLE"
        private const val IS_TRANSFER = "IS_TRANSFER"

        @JvmStatic
        fun newInstance(operationName: String, title: String, isTransfer: Boolean): AddListDialog {
            val addListDialog = AddListDialog()
            addListDialog.arguments = bundleOf(
                OPERATION_NAME_TYPE to operationName,
                OPERATION_TITLE to title,
                IS_TRANSFER to isTransfer
            )
            return addListDialog
        }

    }

    private var _weakListener: WeakReference<DialogAddListener?>? = null
    // This property is only valid between onAttach and onDetach.
    // It should be replaced by view model later to notify changes to activity/other fragment.
    private val weakListener get() = _weakListener!!

    private var operationName: String = ""
    private var operationTitle: String = ""
    private var isTransfer: Boolean = false

    private var receiptMyListFactory: ReceiptMyListFactory? = null
    private var analyticsDataGateway: AnalyticsDataGateway? = null
    private var receiptViewModel: ReceiptViewModel? = null

    private var _binding: DialogAddListBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val listener: DialogAddListener = context as DialogAddListener
        _weakListener = WeakReference(listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let(::readArguments)
    }

    private fun readArguments(args: Bundle) {
        operationName = args.getString(OPERATION_NAME_TYPE, "")
        operationTitle = args.getString(OPERATION_TITLE, "")
        isTransfer = args.getBoolean(IS_TRANSFER, false)
    }

    fun setAnalytics(
        analyticsDataGateway: AnalyticsDataGateway,
        receiptMyListFactory: ReceiptMyListFactory
    ) {
        this.analyticsDataGateway = analyticsDataGateway
        this.receiptMyListFactory = receiptMyListFactory
    }

    fun setReceiptViewModel(receiptViewModel: ReceiptViewModel) {
        this.receiptViewModel = receiptViewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater: LayoutInflater = requireActivity().layoutInflater
        _binding = DialogAddListBinding.inflate(inflater, null, false)
        setUpEtOperationName(binding.etOperationName)
        val alertDialog = createAlertDialog()
        attemptSendShowEvent()
        return alertDialog
    }

    private fun setUpEtOperationName(etOperationName: EditText) = with (etOperationName) {
        maxLines = 1
        isSingleLine = true
        filters = createInputFilters()
        setText(operationName)
        setSelection(operationName.length)
        requestFocus()
    }

    private fun createInputFilters(): Array<InputFilter> {
        val maxLength = 30
        val lengthFilter = LengthFilter(maxLength)
        return arrayOf(lengthFilter)
    }

    private fun createAlertDialog(): AlertDialog {
        val title: String = getString(R.string.name_in_my_list).replace(":", "")
        val acceptText: String = getString(R.string.accept)
        val view: View = binding.root
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setPositiveButton(acceptText, DialogInterface.OnClickListener(::onAcceptClicked))
            .setView(view)
            .create()
        alertDialog.window?.let(::setSoftInputModeToWindow)
        return alertDialog
    }

    private fun setSoftInputModeToWindow(window: Window) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun attemptSendShowEvent() {
        if (isTransfer) {
            val popup: AnalyticPopup = AnalyticPopup.ADD_TRANSFER_TO_MY_LIST
            receiptViewModel?.sendPopupEvent(popup.popupName, popup.label)
            return
        }

        val event: AnalyticsEvent = receiptMyListFactory?.showDialogDescriptionAddMyList(operationTitle) ?: return
        analyticsDataGateway?.setCurrentScreen(ReceiptMyListFactory.SCREEN_NAME_RECEIPT_POPUP_ADD_MY_LIST)
        analyticsDataGateway?.sendEventV2(event)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onAcceptClicked(dialog: DialogInterface, which: Int) {
        weakListener.get()?.let(::sendTextEntered)
    }

    private fun sendTextEntered(listener: DialogAddListener) {
        UiUtil.hideKeyboard(requireContext(), binding.etOperationName)
        sendClickEvent(AnalyticsBaseConstant.ACCEPT)
        val textEntered: String = binding.etOperationName.text.toString().trim()
        listener.onTextEntered(textEntered)
    }

    private fun sendClickEvent(eventLabel: String) {
        if (isTransfer) {
            val popup: AnalyticPopup = AnalyticPopup.ADD_TRANSFER_TO_MY_LIST
            receiptViewModel?.sendClickPopupEvent(popup.popupName, eventLabel)
            return
        }

        val event: AnalyticsEvent = receiptMyListFactory?.onClickDialogDescriptionAddMyList(
            operationTitle,
            eventLabel
        ) ?: return
        analyticsDataGateway?.sendEventV2(event)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        _weakListener = null
    }
}
