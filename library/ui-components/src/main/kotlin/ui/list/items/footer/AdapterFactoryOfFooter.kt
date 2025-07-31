package pe.com.scotiabank.blpm.android.ui.list.items.footer

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewFooterItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfFooter: FactoryOfPortableAdapter<UiEntityOfFooter, ViewFooterItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_footer_item

    override val bindingCallback: BindingCallbackOfItemView<ViewFooterItemBinding> by lazy {
        BindingCallbackOfItemView(ViewFooterItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfFooter, ViewFooterItemBinding>> by lazy {
        InstanceHandler(UiBinderOfFooter::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfFooter, ViewFooterItemBinding>> by lazy {
        InstanceHandler(StateSaverOfFooter::save)
    }
}
