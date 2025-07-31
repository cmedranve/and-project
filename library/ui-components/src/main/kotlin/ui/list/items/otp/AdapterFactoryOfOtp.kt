package pe.com.scotiabank.blpm.android.ui.list.items.otp

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewOtpItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfOtp: FactoryOfPortableAdapter<UiEntityOfOtp, ViewOtpItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_otp_item

    override val bindingCallback: BindingCallbackOfItemView<ViewOtpItemBinding> by lazy {
        BindingCallbackOfItemView(ViewOtpItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfOtp, ViewOtpItemBinding>> by lazy {
        InstanceHandler(UiBinderOfOtp::delegateBinding)
    }
}
