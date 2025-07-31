package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple.checkbox

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCheckBoxItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton

class AdapterFactoryOfCheckBox<D: Any>: FactoryOfPortableAdapter<UiEntityOfCheckableButton<D>, ViewCheckBoxItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_check_box_item

    override val bindingCallback: BindingCallbackOfItemView<ViewCheckBoxItemBinding> by lazy {
        BindingCallbackOfItemView(ViewCheckBoxItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewCheckBoxItemBinding>> by lazy {
        InstanceHandler(UiBinderOfCheckBox::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewCheckBoxItemBinding>> by lazy {
        InstanceHandler(StateSaverOfCheckBox::save)
    }
}
