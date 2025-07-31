package pe.com.scotiabank.blpm.android.ui.list.items.buttons.quickactionbutton

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewQuickActionButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfQuickActionButton<D: Any>: FactoryOfPortableAdapter<UiEntityOfQuickActionButton<D>, ViewQuickActionButtonItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_quick_action_button_item

    override val bindingCallback: BindingCallbackOfItemView<ViewQuickActionButtonItemBinding> by lazy {
        BindingCallbackOfItemView(ViewQuickActionButtonItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfQuickActionButton<D>, ViewQuickActionButtonItemBinding>> by lazy {
        InstanceHandler(UiBinderOfQuickActionButton::delegateBinding)
    }
}
