package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewRecyclerItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfRecycler: FactoryOfPortableAdapter<UiEntityOfRecycler, ViewRecyclerItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_recycler_item

    override val bindingCallback: BindingCallbackOfItemView<ViewRecyclerItemBinding> by lazy {
        BindingCallbackOfItemView(ViewRecyclerItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfRecycler, ViewRecyclerItemBinding>> by lazy {
        InstanceHandler(UiBinderOfRecycler::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfRecycler, ViewRecyclerItemBinding>> by lazy {
        InstanceHandler(StateSaverOfRecycler::save)
    }
}
