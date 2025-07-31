package pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewEditTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfEditText<D: Any>: FactoryOfPortableAdapter<UiEntityOfEditText<D>, ViewEditTextItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_edit_text_item

    override val bindingCallback: BindingCallbackOfItemView<ViewEditTextItemBinding> by lazy {
        BindingCallbackOfItemView(ViewEditTextItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfEditText<D>, ViewEditTextItemBinding>> by lazy {
        InstanceHandler(UiBinderOfEditText::delegateBinding)
    }
}
