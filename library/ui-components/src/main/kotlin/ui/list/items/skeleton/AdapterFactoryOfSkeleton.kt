package pe.com.scotiabank.blpm.android.ui.list.items.skeleton

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewSkeletonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfSkeleton: FactoryOfPortableAdapter<UiEntityOfSkeleton, ViewSkeletonItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_skeleton_item

    override val bindingCallback: BindingCallbackOfItemView<ViewSkeletonItemBinding> by lazy {
        BindingCallbackOfItemView(ViewSkeletonItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfSkeleton, ViewSkeletonItemBinding>> by lazy {
        InstanceHandler(UiBinderOfSkeleton::delegateBinding)
    }
}
