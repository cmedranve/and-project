package pe.com.scotiabank.blpm.android.ui.list.items.selectors.slider

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewSliderItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfSlider: FactoryOfPortableAdapter<UiEntityOfSlider, ViewSliderItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_slider_item

    override val bindingCallback: BindingCallbackOfItemView<ViewSliderItemBinding> by lazy {
        BindingCallbackOfItemView(ViewSliderItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfSlider, ViewSliderItemBinding>> by lazy {
        InstanceHandler(UiBinderOfSlider::delegateBinding)
    }
}
