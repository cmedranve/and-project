package pe.com.scotiabank.blpm.android.ui.list.items.labelbuttonpair

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewLabelButtonPairItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfLabelButtonPair<D: Any>: FactoryOfPortableAdapter<UiEntityOfLabelButtonPair<D>, ViewLabelButtonPairItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_label_button_pair_item

    override val bindingCallback: BindingCallbackOfItemView<ViewLabelButtonPairItemBinding> by lazy {
        BindingCallbackOfItemView(ViewLabelButtonPairItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfLabelButtonPair<D>, ViewLabelButtonPairItemBinding>> by lazy {
        InstanceHandler(UiBinderOfLabelButtonPair::delegateBinding)
    }
}
