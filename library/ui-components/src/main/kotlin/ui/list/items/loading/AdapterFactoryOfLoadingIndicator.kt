package pe.com.scotiabank.blpm.android.ui.list.items.loading

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewLoadingIndicatorItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfLoadingIndicator: FactoryOfPortableAdapter<UiEntityOfLoadingIndicator, ViewLoadingIndicatorItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_loading_indicator_item

    override val bindingCallback: BindingCallbackOfItemView<ViewLoadingIndicatorItemBinding> by lazy {
        BindingCallbackOfItemView(ViewLoadingIndicatorItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfLoadingIndicator, ViewLoadingIndicatorItemBinding>> by lazy {
        InstanceHandler(UiBinderOfLoadingIndicator::delegateBinding)
    }
}
