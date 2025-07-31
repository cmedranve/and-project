package pe.com.scotiabank.blpm.android.ui.list.items.skeleton.doubleended

import androidx.annotation.LayoutRes
import com.scotiabank.enhancements.handling.InstanceHandler
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.ViewDoubleEndedSkeletonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.BindingCallbackOfItemView
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier

class AdapterFactoryOfDoubleEndedSkeleton: FactoryOfPortableAdapter<UiEntityOfDoubleEndedSkeleton, ViewDoubleEndedSkeletonItemBinding>() {

    override val layoutRes: Int
        @LayoutRes
        get() = R.layout.view_double_ended_skeleton_item

    override val bindingCallback: BindingCallbackOfItemView<ViewDoubleEndedSkeletonItemBinding> by lazy {
        BindingCallbackOfItemView(ViewDoubleEndedSkeletonItemBinding::bind)
    }

    override val bindingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfDoubleEndedSkeleton, ViewDoubleEndedSkeletonItemBinding>> by lazy {
        InstanceHandler(UiBinderOfDoubleEndedSkeleton::delegateBinding)
    }

    override val stateRecyclingHandler: InstanceHandler<UiEntityCarrier<UiEntityOfDoubleEndedSkeleton, ViewDoubleEndedSkeletonItemBinding>> by lazy {
        InstanceHandler(StateSaverOfDoubleEndedSkeleton::save)
    }
}
