package pe.com.scotiabank.blpm.android.ui.list.items.buttons.pillbutton

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewPillButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfPillButton<D: Any>: FactoryOfPortableAdapter<UiEntityOfPillButton<D>, ViewPillButtonItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_pill_button_item

    override val bindingCallback: BindingCallbackOfItemView<ViewPillButtonItemBinding> by lazy {
        BindingCallbackOfItemView(ViewPillButtonItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfPillButton<D>, ViewPillButtonItemBinding>> by lazy {
        InstanceHandler(UiBinderOfPillButton::delegateBinding)
    }
}
