package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.radiobutton

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewRadioButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckableButton

class AdapterFactoryOfRadioButton<D: Any>: FactoryOfPortableAdapter<UiEntityOfCheckableButton<D>, ViewRadioButtonItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_radio_button_item

    override val bindingCallback: BindingCallbackOfItemView<ViewRadioButtonItemBinding> by lazy {
        BindingCallbackOfItemView(ViewRadioButtonItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonItemBinding>> by lazy {
        InstanceHandler(UiBinderOfRadioButton::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCheckableButton<D>, ViewRadioButtonItemBinding>> by lazy {
        InstanceHandler(StateSaverOfRadioButton::save)
    }
}
