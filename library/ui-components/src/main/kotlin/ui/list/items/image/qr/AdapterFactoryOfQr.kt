package pe.com.scotiabank.blpm.android.ui.list.items.image.qr

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewQrItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfQr: FactoryOfPortableAdapter<UiEntityOfQr, ViewQrItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_qr_item

    override val bindingCallback: BindingCallbackOfItemView<ViewQrItemBinding> by lazy {
        BindingCallbackOfItemView(ViewQrItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfQr, ViewQrItemBinding>> by lazy {
        InstanceHandler(UiBinderOfQr::delegateBinding)
    }
}
