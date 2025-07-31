package pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewOneColumnTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfOneColumnText: FactoryOfPortableAdapter<UiEntityOfOneColumnText, ViewOneColumnTextItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_one_column_text_item

    override val bindingCallback: BindingCallbackOfItemView<ViewOneColumnTextItemBinding> by lazy {
        BindingCallbackOfItemView(ViewOneColumnTextItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfOneColumnText, ViewOneColumnTextItemBinding>> by lazy {
        InstanceHandler(UiBinderOfOneColumnText::delegateBinding)
    }
}
