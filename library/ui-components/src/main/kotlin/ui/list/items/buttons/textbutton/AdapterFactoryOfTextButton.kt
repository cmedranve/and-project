package pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewTextButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfTextButton<D: Any>: FactoryOfPortableAdapter<UiEntityOfTextButton<D>, ViewTextButtonItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_text_button_item

    override val bindingCallback: BindingCallbackOfItemView<ViewTextButtonItemBinding> by lazy {
        BindingCallbackOfItemView(ViewTextButtonItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfTextButton<D>, ViewTextButtonItemBinding>> by lazy {
        InstanceHandler(UiBinderOfTextButton::delegateBinding)
    }
}
