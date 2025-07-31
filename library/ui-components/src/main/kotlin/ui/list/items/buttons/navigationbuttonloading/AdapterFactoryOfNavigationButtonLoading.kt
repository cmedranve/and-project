package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbuttonloading

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewNavigationButtonLoadingItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfNavigationButtonLoading<D: Any>: FactoryOfPortableAdapter<UiEntityOfNavigationButtonLoading<D>, ViewNavigationButtonLoadingItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_navigation_button_loading_item

    override val bindingCallback: BindingCallbackOfItemView<ViewNavigationButtonLoadingItemBinding> by lazy {
        BindingCallbackOfItemView(ViewNavigationButtonLoadingItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfNavigationButtonLoading<D>, ViewNavigationButtonLoadingItemBinding>> by lazy {
        InstanceHandler(UiBinderOfNavigationButtonLoading::delegateBinding)
    }
}
