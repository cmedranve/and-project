package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewEditPasswordItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfPassword<D: Any>: FactoryOfPortableAdapter<UiEntityOfPassword<D>, ViewEditPasswordItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_edit_password_item

    override val bindingCallback: BindingCallbackOfItemView<ViewEditPasswordItemBinding> by lazy {
        BindingCallbackOfItemView(ViewEditPasswordItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfPassword<D>, ViewEditPasswordItemBinding>> by lazy {
        InstanceHandler(UiBinderOfPassword::delegateBinding)
    }
}
