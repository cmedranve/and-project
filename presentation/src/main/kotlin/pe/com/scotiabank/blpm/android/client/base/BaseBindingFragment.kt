package pe.com.scotiabank.blpm.android.client.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.EventWrapper
import pe.com.scotiabank.blpm.android.client.util.listener.AlertDialogListener

abstract class BaseBindingFragment<B : ViewBinding> : BaseFragment() {

    private var _binding: B? = null
    // This property is only valid between onCreateView and onDestroyView.
    protected val binding get() = _binding!!

    abstract fun initializeInjector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeInjector()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getBindingInflater().inflate(inflater, container, false)
        return binding.root
    }

    protected abstract fun getBindingInflater(): BindingInflaterOfFragment<B>

    protected open fun observeErrorMessage(baseAppearErrorMessage: EventWrapper<BaseAppearErrorMessage>?) {
        baseAppearErrorMessage?.run {
            if (!hasBeenHandled()) {
                showErrorMessage(contentIfNotHandled.throwable)
            }
        }
    }

    protected open fun observeShowHideLoading(loading: EventWrapper<Boolean>?) {
        loading?.run {
            if (!hasBeenHandled() && contentIfNotHandled) {
                showProgressDialog()
                return
            }
            dismissProgressDialog()
        }
    }

    @JvmOverloads
    protected fun showMessageDialog(message: String?,
                                    title: String? = null,
                                    positiveButton: String? = getString(R.string.accept),
                                    positiveListener: DialogInterface.OnClickListener? = null,
                                    negativeButton: String? = null,
                                    negativeListener: DialogInterface.OnClickListener? = null) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showMessageDialog(message, title, positiveButton, positiveListener, negativeButton, negativeListener)
        }
    }

    @JvmOverloads
    protected fun showErrorMessage(throwable: Throwable?, analyticsMessage: ((String, String) -> Unit)? = null) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showErrorMessage(throwable, analyticsMessage)
        }
    }

    protected fun showProgressDialog() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showProgressDialog()
        }
    }

    protected fun dismissProgressDialog() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).dismissProgressDialog()
        }
    }

    protected fun setQrDialogListener(alertDialogListener: AlertDialogListener?) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).setQrDialogListener(alertDialogListener)
        }
    }

    override fun showDisabledAlert(templateType: String?) {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showDisabledAlert(templateType)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
