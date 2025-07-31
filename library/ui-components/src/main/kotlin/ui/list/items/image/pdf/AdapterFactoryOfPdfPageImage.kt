package pe.com.scotiabank.blpm.android.ui.list.items.image.pdf

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewPdfPageImageItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfPdfPageImage: FactoryOfPortableAdapter<UiEntityOfPdfPageImage, ViewPdfPageImageItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_pdf_page_image_item

    override val bindingCallback: BindingCallbackOfItemView<ViewPdfPageImageItemBinding> by lazy {
        BindingCallbackOfItemView(ViewPdfPageImageItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfPdfPageImage, ViewPdfPageImageItemBinding>> by lazy {
        InstanceHandler(UiBinderOfPdfPageImage::delegateBinding)
    }
}
