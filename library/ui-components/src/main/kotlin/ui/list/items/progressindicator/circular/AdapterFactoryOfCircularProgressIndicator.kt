package pe.com.scotiabank.blpm.android.ui.list.items.progressindicator.circular

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCircularProgressIndicatorItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfCircularProgressIndicator<D: Any> : FactoryOfPortableAdapter<UiEntityOfCircularProgressIndicator<D>, ViewCircularProgressIndicatorItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_circular_progress_indicator_item

    override val bindingCallback: BindingCallbackOfItemView<ViewCircularProgressIndicatorItemBinding> by lazy {
        BindingCallbackOfItemView(ViewCircularProgressIndicatorItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCircularProgressIndicator<D>, ViewCircularProgressIndicatorItemBinding>> by lazy {
        InstanceHandler(UiBinderOfCircularProgressIndicator::delegateBinding)
    }
}
