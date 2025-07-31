package pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewDropdownSelectorItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfDropdownSelector<D: Any>: FactoryOfPortableAdapter<UiEntityOfDropdownSelector<D>, ViewDropdownSelectorItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_dropdown_selector_item

    override val bindingCallback: BindingCallbackOfItemView<ViewDropdownSelectorItemBinding> by lazy {
        BindingCallbackOfItemView(ViewDropdownSelectorItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfDropdownSelector<D>, ViewDropdownSelectorItemBinding>> by lazy {
        InstanceHandler(UiBinderOfDropdownSelector::delegateBinding)
    }
}
