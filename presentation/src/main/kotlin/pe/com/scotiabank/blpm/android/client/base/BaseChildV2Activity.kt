package pe.com.scotiabank.blpm.android.client.base

import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

@Deprecated(
    "Use BaseBindingActivity<B : ViewBinding> instead.",
    ReplaceWith(
        "BaseBindingActivity<B : ViewBinding>()",
        "pe.com.scotiabank.blpm.android.client.base.BaseBindingActivity"
    )
)
abstract class BaseChildV2Activity<T : ViewDataBinding, V : ViewModel> : BaseBindingActivity<T>() {

    protected lateinit var dataBinding: T

    protected abstract val viewModel: V

    protected abstract val layout: Int
    protected abstract val bindingId: Int

    override fun getBindingInflater(): BindingInflaterOfActivity<T> {
        return BindingInflaterOfActivity(::createDataBinding)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createDataBinding(inflater: LayoutInflater): T {
        dataBinding = DataBindingUtil.setContentView(this, layout)
        dataBinding.setVariable(bindingId, viewModel)
        dataBinding.lifecycleOwner = this
        return dataBinding
    }
}
