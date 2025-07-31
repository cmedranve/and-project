package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.backtype

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewNavigationButtonBackItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.UiEntityOfNavigationButton

class AdapterFactoryOfNavigationButtonBack<D: Any>: FactoryOfPortableAdapter<UiEntityOfNavigationButton<D>, ViewNavigationButtonBackItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_navigation_button_back_item

    override val bindingCallback: BindingCallbackOfItemView<ViewNavigationButtonBackItemBinding> by lazy {
        BindingCallbackOfItemView(ViewNavigationButtonBackItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfNavigationButton<D>, ViewNavigationButtonBackItemBinding>> by lazy {
        InstanceHandler(UiBinderOfNavigationButtonBack::delegateBinding)
    }
}
