package pe.com.scotiabank.blpm.android.client.base.receipt

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.databinding.DialogAddFrequentListBinding
import java.lang.ref.WeakReference

class AddFrequentListDialog : DialogFragment() {

    companion object {

        private const val CREDIT_CARD_NUMBER = "CREDIT_CARD_NUMBER"

        @JvmStatic
        fun newInstance(creditCardNumber: String): AddFrequentListDialog? {
            val addListDialog = AddFrequentListDialog()
            addListDialog.arguments = bundleOf(CREDIT_CARD_NUMBER to creditCardNumber)
            return addListDialog
        }

    }

    private var _weakListener: WeakReference<DialogAddListener?>? = null
    // This property is only valid between onAttach and onDetach.
    // It should be replaced by view model later to notify changes to activity/other fragment.
    private val weakListener get() = _weakListener!!

    private var creditCardNumber: String = ""

    private var _binding: DialogAddFrequentListBinding? = null
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
        creditCardNumber = args.getString(CREDIT_CARD_NUMBER, "")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater: LayoutInflater = requireActivity().layoutInflater
        _binding = DialogAddFrequentListBinding.inflate(inflater, null, false)
        setUpTvAddTc(binding.tvAddTc)
        return createAlertDialog()
    }

    private fun setUpTvAddTc(tvAddTc: TextView) = with (tvAddTc) {
        text = getString(R.string.add_credit_card, creditCardNumber)
    }

    private fun createAlertDialog(): AlertDialog {
        val title: String = getString(R.string.add_my_credit_card).replace(":", "")
        val yesAddText: String = getString(R.string.cc_frequent_yes)
        val notNowText: String = getString(R.string.cc_frequent_no)
        val view: View = binding.root
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setPositiveButton(yesAddText, DialogInterface.OnClickListener(::onYesAddClicked))
            .setNegativeButton(notNowText, DialogInterface.OnClickListener(::onNotNowClicked))
            .setView(view)
            .create()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onYesAddClicked(dialog: DialogInterface, which: Int) {
        weakListener.get()?.let(::sendTextEntered)
    }

    private fun sendTextEntered(listener: DialogAddListener) {
        listener.onTextEntered(creditCardNumber)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onNotNowClicked(dialog: DialogInterface, which: Int) {
        weakListener.get()?.let(::onDismissDialog)
        dialog.dismiss()
    }

    private fun onDismissDialog(listener: DialogAddListener) {
        listener.onDismissDialog()
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
