package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewDynamicChipsComponentItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfDynamicChipsComponent<D: Any>: FactoryOfPortableAdapter<UiEntityOfDynamicChipsComponent<D>, ViewDynamicChipsComponentItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_dynamic_chips_component_item

    override val bindingCallback: BindingCallbackOfItemView<ViewDynamicChipsComponentItemBinding> by lazy {
        BindingCallbackOfItemView(ViewDynamicChipsComponentItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfDynamicChipsComponent<D>, ViewDynamicChipsComponentItemBinding>> by lazy {
        InstanceHandler(UiBinderOfDynamicChipsComponent::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfDynamicChipsComponent<D>, ViewDynamicChipsComponentItemBinding>> by lazy {
        InstanceHandler(SaverOfOffsetFromStart::delegateSave)
    }
}
