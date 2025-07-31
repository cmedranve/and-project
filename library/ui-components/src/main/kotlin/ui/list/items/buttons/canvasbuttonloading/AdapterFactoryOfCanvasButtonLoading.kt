package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCanvasButtonLoadingItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfCanvasButtonLoading<D: Any>: FactoryOfPortableAdapter<UiEntityOfCanvasButtonLoading<D>, ViewCanvasButtonLoadingItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_canvas_button_loading_item

    override val bindingCallback: BindingCallbackOfItemView<ViewCanvasButtonLoadingItemBinding> by lazy {
        BindingCallbackOfItemView(ViewCanvasButtonLoadingItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCanvasButtonLoading<D>, ViewCanvasButtonLoadingItemBinding>> by lazy {
        InstanceHandler(UiBinderOfCanvasButtonLoading::delegateBinding)
    }
}
