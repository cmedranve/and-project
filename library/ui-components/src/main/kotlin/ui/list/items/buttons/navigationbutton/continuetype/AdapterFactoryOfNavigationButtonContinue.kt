package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.continuetype

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewNavigationButtonContinueItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.UiEntityOfNavigationButton

class AdapterFactoryOfNavigationButtonContinue<D: Any>: FactoryOfPortableAdapter<UiEntityOfNavigationButton<D>, ViewNavigationButtonContinueItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_navigation_button_continue_item

    override val bindingCallback: BindingCallbackOfItemView<ViewNavigationButtonContinueItemBinding> by lazy {
        BindingCallbackOfItemView(ViewNavigationButtonContinueItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfNavigationButton<D>, ViewNavigationButtonContinueItemBinding>> by lazy {
        InstanceHandler(UiBinderOfNavigationButtonContinue::delegateBinding)
    }
}
