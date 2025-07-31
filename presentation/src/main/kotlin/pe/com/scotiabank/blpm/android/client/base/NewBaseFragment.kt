package pe.com.scotiabank.blpm.android.client.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.EventWrapper
import pe.com.scotiabank.blpm.android.client.util.listener.AlertDialogListener

abstract class NewBaseFragment<D : ViewDataBinding, V : ViewModel> : BaseFragment() {
    protected lateinit var dataBinding: D

    protected abstract val viewModel: V

    abstract fun initializeInjector()
    abstract val bindingVariable: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeInjector()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layout, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.run {
            setVariable(bindingVariable, viewModel)
            executePendingBindings()
            lifecycleOwner = this@NewBaseFragment
        }
    }

    protected open fun observeErrorMessage(baseAppearErrorMessage: EventWrapper<BaseAppearErrorMessage>?) {
        baseAppearErrorMessage?.run {
            if (!hasBeenHandled()) {
                showErrorMessage(contentIfNotHandled.throwable)
            }
        }
    }

    protected fun observeShowHideLoading(loading: EventWrapper<Boolean>?) {
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
}
