package pe.com.scotiabank.blpm.android.ui.list.items.inputs.centeredcurrencyedittext

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCenteredCurrencyEditTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfCenteredCurrencyEditText<D: Any>: FactoryOfPortableAdapter<UiEntityOfCenteredCurrencyEditText<D>, ViewCenteredCurrencyEditTextItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_centered_currency_edit_text_item

    override val bindingCallback: BindingCallbackOfItemView<ViewCenteredCurrencyEditTextItemBinding> by lazy {
        BindingCallbackOfItemView(ViewCenteredCurrencyEditTextItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCenteredCurrencyEditText<D>, ViewCenteredCurrencyEditTextItemBinding>> by lazy {
        InstanceHandler(UiBinderOfCenteredCurrencyEditText::delegateBinding)
    }
}
