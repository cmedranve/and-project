package pe.com.scotiabank.blpm.android.ui.list.items.quickaction

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewQuickActionItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfQuickAction<D: Any>: FactoryOfPortableAdapter<UiEntityOfQuickAction<D>, ViewQuickActionItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_quick_action_item

    override val bindingCallback: BindingCallbackOfItemView<ViewQuickActionItemBinding> by lazy {
        BindingCallbackOfItemView(ViewQuickActionItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfQuickAction<D>, ViewQuickActionItemBinding>> by lazy {
        InstanceHandler(UiBinderOfQuickAction::delegateBinding)
    }
}
