package pe.com.scotiabank.blpm.android.ui.list.items.skeleton.doubleended

import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.scotiabank.canvascore.cards.CanvasSkeletonCard
import com.scotiabank.canvascore.layouts.CanvasCoreSkeletonFrameLayout
import pe.com.scotiabank.blpm.android.ui.databinding.ViewDoubleEndedSkeletonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiBinderOfRecyclerForOtherType
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiBinderOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfDoubleEndedSkeleton {

    @JvmStatic
    fun delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfDoubleEndedSkeleton, ViewDoubleEndedSkeletonItemBinding>
    ) {
        val entity: UiEntityOfDoubleEndedSkeleton = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun bind(
        carrier: UiEntityCarrier<UiEntityOfDoubleEndedSkeleton, ViewDoubleEndedSkeletonItemBinding>,
        entity: UiEntityOfDoubleEndedSkeleton,
        binding: ViewDoubleEndedSkeletonItemBinding,
    ) {
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)

        bindSide(
            entity = entity.leftSkeletonEntity,
            cSkeletonCard = binding.cSkeletonCardLeft,
            bias = entity.verticalBiasOfLeftSkeleton,
            llSide = binding.llLeft,
            ccSkeletonFrameLayout = binding.ccsFlLeft,
        )

        bindSide(
            entity = entity.rightSkeletonEntity,
            cSkeletonCard = binding.cSkeletonCardRight,
            bias = entity.verticalBiasOfRightSkeleton,
            llSide = binding.llRight,
            ccSkeletonFrameLayout = binding.ccsFlRight,
        )

        UiBinderOfRecyclerForOtherType.bind(
            carrier = carrier,
            entity = entity.centerRecyclerEntity,
            recyclerView = binding.rvCenterItems,
        )
    }

    @JvmStatic
    private fun bindSide(
        entity: UiEntityOfSkeleton,
        cSkeletonCard: CanvasSkeletonCard,
        bias: Float,
        llSide: LinearLayoutCompat,
        ccSkeletonFrameLayout: CanvasCoreSkeletonFrameLayout,
    ) {
        UiBinderOfSkeleton.bindShape(entity.shape, cSkeletonCard)
        llSide.updateLayoutParams<ConstraintLayout.LayoutParams> {
            verticalBias = bias
        }
        UiBinderOfSkeleton.bindContent(entity, llSide, ccSkeletonFrameLayout)
    }
}
