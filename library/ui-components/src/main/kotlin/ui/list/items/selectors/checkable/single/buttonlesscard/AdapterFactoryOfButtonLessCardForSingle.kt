package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single.buttonlesscard

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewButtonLessCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfButtonLessCard
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.StateSaverOfButtonLessCard

class AdapterFactoryOfButtonLessCardForSingle<D: Any>: FactoryOfPortableAdapter<UiEntityOfButtonLessCard<D>, ViewButtonLessCardItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_button_less_card_item

    override val bindingCallback: BindingCallbackOfItemView<ViewButtonLessCardItemBinding> by lazy {
        BindingCallbackOfItemView(ViewButtonLessCardItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfButtonLessCard<D>, ViewButtonLessCardItemBinding>> by lazy {
        InstanceHandler(UiBinderOfButtonLessCard::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfButtonLessCard<D>, ViewButtonLessCardItemBinding>> by lazy {
        InstanceHandler(StateSaverOfButtonLessCard::save)
    }
}
