package pe.com.scotiabank.blpm.android.ui.list.items.buddytip

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewBuddyTipItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfBuddyTip: FactoryOfPortableAdapter<UiEntityOfBuddyTip, ViewBuddyTipItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_buddy_tip_item

    override val bindingCallback: BindingCallbackOfItemView<ViewBuddyTipItemBinding> by lazy {
        BindingCallbackOfItemView(ViewBuddyTipItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfBuddyTip, ViewBuddyTipItemBinding>> by lazy {
        InstanceHandler(UiBinderOfBuddyTip::delegateBinding)
    }
}
