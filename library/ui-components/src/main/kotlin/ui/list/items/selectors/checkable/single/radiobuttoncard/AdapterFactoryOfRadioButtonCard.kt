package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.radiobuttoncard

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewRadioButtonCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton

class AdapterFactoryOfRadioButtonCard<D: Any>: FactoryOfPortableAdapter<UiEntityOfCheckableButton<D>, ViewRadioButtonCardItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_radio_button_card_item

    override val bindingCallback: BindingCallbackOfItemView<ViewRadioButtonCardItemBinding> by lazy {
        BindingCallbackOfItemView(ViewRadioButtonCardItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonCardItemBinding>> by lazy {
        InstanceHandler(UiBinderOfRadioButtonCard::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonCardItemBinding>> by lazy {
        InstanceHandler(StateSaverOfRadioButtonCard::save)
    }
}
