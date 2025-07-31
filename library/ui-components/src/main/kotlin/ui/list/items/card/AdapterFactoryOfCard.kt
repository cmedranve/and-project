package pe.com.scotiabank.blpm.android.ui.list.items.card

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfCard<D: Any>: FactoryOfPortableAdapter<UiEntityOfCard<D>, ViewCardItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_card_item

    override val bindingCallback: BindingCallbackOfItemView<ViewCardItemBinding> by lazy {
        BindingCallbackOfItemView(ViewCardItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCard<D>, ViewCardItemBinding>> by lazy {
        InstanceHandler(UiBinderOfCard::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCard<D>, ViewCardItemBinding>> by lazy {
        InstanceHandler(StateSaverOfCard::save)
    }
}
