package pe.com.scotiabank.blpm.android.ui.list.items.statusbadge

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewStatusBadgeItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfStatusBadge : FactoryOfPortableAdapter<UiEntityOfStatusBadge, ViewStatusBadgeItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_status_badge_item

    override val bindingCallback: BindingCallbackOfItemView<ViewStatusBadgeItemBinding> by lazy {
        BindingCallbackOfItemView(ViewStatusBadgeItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfStatusBadge, ViewStatusBadgeItemBinding>> by lazy {
        InstanceHandler(UiBinderOfStatusBadge::delegateBinding)
    }
}
