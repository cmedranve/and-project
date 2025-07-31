package pe.com.scotiabank.blpm.android.ui.list.items.progressindicator.linear

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewProgressIndicatorItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfProgressIndicator<D: Any> : FactoryOfPortableAdapter<UiEntityOfProgressIndicator<D>, ViewProgressIndicatorItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_progress_indicator_item

    override val bindingCallback: BindingCallbackOfItemView<ViewProgressIndicatorItemBinding> by lazy {
        BindingCallbackOfItemView(ViewProgressIndicatorItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfProgressIndicator<D>, ViewProgressIndicatorItemBinding>> by lazy {
        InstanceHandler(UiBinderOfProgressIndicator::delegateBinding)
    }
}
