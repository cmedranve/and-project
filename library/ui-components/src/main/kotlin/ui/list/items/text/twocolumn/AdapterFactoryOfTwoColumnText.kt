package pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewTwoColumnTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfTwoColumnText: FactoryOfPortableAdapter<UiEntityOfTwoColumnText, ViewTwoColumnTextItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_two_column_text_item

    override val bindingCallback: BindingCallbackOfItemView<ViewTwoColumnTextItemBinding> by lazy {
        BindingCallbackOfItemView(ViewTwoColumnTextItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfTwoColumnText, ViewTwoColumnTextItemBinding>> by lazy {
        InstanceHandler(UiBinderOfTwoColumnText::delegateBinding)
    }
}
