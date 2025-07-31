package pe.com.scotiabank.blpm.android.ui.list.items.avatar

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewAvatarItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfAvatar: FactoryOfPortableAdapter<UiEntityOfAvatar, ViewAvatarItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_avatar_item

    override val bindingCallback: BindingCallbackOfItemView<ViewAvatarItemBinding> by lazy {
        BindingCallbackOfItemView(ViewAvatarItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfAvatar, ViewAvatarItemBinding>> by lazy {
        InstanceHandler(UiBinderOfAvatar::delegateBinding)
    }
}
