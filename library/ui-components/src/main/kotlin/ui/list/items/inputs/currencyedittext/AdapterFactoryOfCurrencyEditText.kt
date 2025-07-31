package pe.com.scotiabank.blpm.android.ui.list.items.inputs.currencyedittext

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCurrencyEditTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfCurrencyEditText<D: Any>: FactoryOfPortableAdapter<UiEntityOfCurrencyEditText<D>, ViewCurrencyEditTextItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_currency_edit_text_item

    override val bindingCallback: BindingCallbackOfItemView<ViewCurrencyEditTextItemBinding> by lazy {
        BindingCallbackOfItemView(ViewCurrencyEditTextItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCurrencyEditText<D>, ViewCurrencyEditTextItemBinding>> by lazy {
        InstanceHandler(UiBinderOfCurrencyEditText::delegateBinding)
    }
}
