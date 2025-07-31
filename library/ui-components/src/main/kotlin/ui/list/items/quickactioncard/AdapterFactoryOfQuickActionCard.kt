package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewQuickActionCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfQuickActionCard<D: Any>: FactoryOfPortableAdapter<UiEntityOfQuickActionCard<D>, ViewQuickActionCardItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_quick_action_card_item

    override val bindingCallback: BindingCallbackOfItemView<ViewQuickActionCardItemBinding> by lazy {
        BindingCallbackOfItemView(ViewQuickActionCardItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfQuickActionCard<D>, ViewQuickActionCardItemBinding>> by lazy {
        InstanceHandler(UiBinderOfQuickActionCard::delegateBinding)
    }
}
