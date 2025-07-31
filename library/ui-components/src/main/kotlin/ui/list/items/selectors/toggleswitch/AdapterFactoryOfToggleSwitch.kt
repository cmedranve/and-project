package pe.com.scotiabank.blpm.android.ui.list.items.selectors.toggleswitch

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewToggleSwitchItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfToggleSwitch<D: Any>: FactoryOfPortableAdapter<UiEntityOfToggleSwitch<D>, ViewToggleSwitchItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_toggle_switch_item

    override val bindingCallback: BindingCallbackOfItemView<ViewToggleSwitchItemBinding> by lazy {
        BindingCallbackOfItemView(ViewToggleSwitchItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfToggleSwitch<D>, ViewToggleSwitchItemBinding>> by lazy {
        InstanceHandler(UiBinderOfToggleSwitch::delegateBinding)
    }
}
