package pe.com.scotiabank.blpm.android.ui.list.items.avatar.leading

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewLeadingAvatarItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfLeadingAvatar<D: Any>: FactoryOfPortableAdapter<UiEntityOfLeadingAvatar<D>, ViewLeadingAvatarItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_leading_avatar_item

    override val bindingCallback: BindingCallbackOfItemView<ViewLeadingAvatarItemBinding> by lazy {
        BindingCallbackOfItemView(ViewLeadingAvatarItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfLeadingAvatar<D>, ViewLeadingAvatarItemBinding>> by lazy {
        InstanceHandler(UiBinderOfLeadingAvatar::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfLeadingAvatar<D>, ViewLeadingAvatarItemBinding>> by lazy {
        InstanceHandler(StateSaverOfLeadingAvatar::save)
    }
}
