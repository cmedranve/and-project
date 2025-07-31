package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.checkboxcard

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCheckBoxCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton

class AdapterFactoryOfCheckBoxCard<D: Any>: FactoryOfPortableAdapter<UiEntityOfCheckableButton<D>, ViewCheckBoxCardItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_check_box_card_item

    override val bindingCallback: BindingCallbackOfItemView<ViewCheckBoxCardItemBinding> by lazy {
        BindingCallbackOfItemView(ViewCheckBoxCardItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewCheckBoxCardItemBinding>> by lazy {
        InstanceHandler(UiBinderOfCheckBoxCard::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewCheckBoxCardItemBinding>> by lazy {
        InstanceHandler(StateSaverOfCheckBoxCard::save)
    }
}
