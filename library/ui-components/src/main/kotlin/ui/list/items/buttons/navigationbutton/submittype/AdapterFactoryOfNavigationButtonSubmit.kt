package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.submittype

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewNavigationButtonSubmitItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.UiEntityOfNavigationButton

class AdapterFactoryOfNavigationButtonSubmit<D: Any>: FactoryOfPortableAdapter<UiEntityOfNavigationButton<D>, ViewNavigationButtonSubmitItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_navigation_button_submit_item

    override val bindingCallback: BindingCallbackOfItemView<ViewNavigationButtonSubmitItemBinding> by lazy {
        BindingCallbackOfItemView(ViewNavigationButtonSubmitItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfNavigationButton<D>, ViewNavigationButtonSubmitItemBinding>> by lazy {
        InstanceHandler(UiBinderOfNavigationButtonSubmit::delegateBinding)
    }
}
