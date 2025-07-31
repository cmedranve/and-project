package pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewOneColumnImageItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfOneColumnImage: FactoryOfPortableAdapter<UiEntityOfOneColumnImage, ViewOneColumnImageItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_one_column_image_item

    override val bindingCallback: BindingCallbackOfItemView<ViewOneColumnImageItemBinding> by lazy {
        BindingCallbackOfItemView(ViewOneColumnImageItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfOneColumnImage, ViewOneColumnImageItemBinding>> by lazy {
        InstanceHandler(UiBinderOfOneColumnImage::delegateBinding)
    }
}
