package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCanvasButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfCanvasButton<D: Any>: FactoryOfPortableAdapter<UiEntityOfCanvasButton<D>, ViewCanvasButtonItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_canvas_button_item

    override val bindingCallback: BindingCallbackOfItemView<ViewCanvasButtonItemBinding> by lazy {
        BindingCallbackOfItemView(ViewCanvasButtonItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfCanvasButton<D>, ViewCanvasButtonItemBinding>> by lazy {
        InstanceHandler(UiBinderOfCanvasButton::delegateBinding)
    }
}
