package pe.com.scotiabank.blpm.android.ui.list.items.alertbanner

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewAlertBannerItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfAlertBanner<D: Any>: FactoryOfPortableAdapter<UiEntityOfAlertBanner<D>, ViewAlertBannerItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_alert_banner_item

    override val bindingCallback: BindingCallbackOfItemView<ViewAlertBannerItemBinding> by lazy {
        BindingCallbackOfItemView(ViewAlertBannerItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfAlertBanner<D>, ViewAlertBannerItemBinding>> by lazy {
        InstanceHandler(UiBinderOfAlertBanner::delegateBinding)
    }
}
