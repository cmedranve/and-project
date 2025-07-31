package pe.com.scotiabank.blpm.android.ui.list.items.atmcard

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewAtmCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfAtmCard<D: Any>: FactoryOfPortableAdapter<UiEntityOfAtmCard<D>, ViewAtmCardItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_atm_card_item

    override val bindingCallback: BindingCallbackOfItemView<ViewAtmCardItemBinding> by lazy {
        BindingCallbackOfItemView(ViewAtmCardItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfAtmCard<D>, ViewAtmCardItemBinding>> by lazy {
        InstanceHandler(UiBinderOfAtmCard::delegateBinding)
    }
}
