package pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewDoubleEndedImageItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfDoubleEndedImage<D: Any>: FactoryOfPortableAdapter<UiEntityOfDoubleEndedImage<D>, ViewDoubleEndedImageItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_double_ended_image_item

    override val bindingCallback: BindingCallbackOfItemView<ViewDoubleEndedImageItemBinding> by lazy {
        BindingCallbackOfItemView(ViewDoubleEndedImageItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfDoubleEndedImage<D>, ViewDoubleEndedImageItemBinding>> by lazy {
        InstanceHandler(UiBinderOfDoubleEndedImage::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfDoubleEndedImage<D>, ViewDoubleEndedImageItemBinding>> by lazy {
        InstanceHandler(StateSaverOfDoubleEndedImage::save)
    }
}
