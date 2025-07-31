package pe.com.scotiabank.blpm.android.ui.list.items.rewardcard

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewRewardCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfRewardCard<D: Any>: FactoryOfPortableAdapter<UiEntityOfRewardCard<D>, ViewRewardCardItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_reward_card_item

    override val bindingCallback: BindingCallbackOfItemView<ViewRewardCardItemBinding> by lazy {
        BindingCallbackOfItemView(ViewRewardCardItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfRewardCard<D>, ViewRewardCardItemBinding>> by lazy {
        InstanceHandler(UiBinderOfRewardCard::delegateBinding)
    }
}
