package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewStaticChipsComponentItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfStaticChipsComponent<D: Any>: FactoryOfPortableAdapter<UiEntityOfStaticChipsComponent<D>, ViewStaticChipsComponentItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_static_chips_component_item

    override val bindingCallback: BindingCallbackOfItemView<ViewStaticChipsComponentItemBinding> by lazy {
        BindingCallbackOfItemView(ViewStaticChipsComponentItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfStaticChipsComponent<D>, ViewStaticChipsComponentItemBinding>> by lazy {
        InstanceHandler(UiBinderOfStaticChipsComponent::delegateBinding)
    }
}
