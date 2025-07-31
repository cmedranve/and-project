package pe.com.scotiabank.blpm.android.ui.list.items.inputs.pintext

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewPinTextInputViewBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfPinText<D: Any>: FactoryOfPortableAdapter<UiEntityOfPinText<D>, ViewPinTextInputViewBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_pin_text_input_view

    override val bindingCallback: BindingCallbackOfItemView<ViewPinTextInputViewBinding> by lazy {
        BindingCallbackOfItemView(ViewPinTextInputViewBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfPinText<D>, ViewPinTextInputViewBinding>> by lazy {
        InstanceHandler(UiBinderOfPinText::delegateBinding)
    }
}
