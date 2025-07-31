package pe.com.scotiabank.blpm.android.ui.list.items.skeleton

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams
import androidx.core.view.updateLayoutParams
import com.scotiabank.canvascore.cards.CanvasSkeletonCard
import com.scotiabank.canvascore.layouts.CanvasCoreSkeletonFrameLayout
import pe.com.scotiabank.blpm.android.ui.databinding.ViewSkeletonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfSkeleton {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfSkeleton, ViewSkeletonItemBinding>) {
        val entity: UiEntityOfSkeleton = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfSkeleton, binding: ViewSkeletonItemBinding) {
        binding.root.gravity = entity.gravity
        bindShape(entity.shape, binding.cSkeletonCard)
        bindContent(entity, binding.root, binding.ccSkeletonFrameLayout)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
    }

    @JvmStatic
    internal fun bindShape(shape: SkeletonShape, cSkeletonCard: CanvasSkeletonCard) {
        val drawable: Drawable = AppCompatResources.getDrawable(cSkeletonCard.context, shape.drawableId)
            ?: return
        cSkeletonCard.background = drawable
    }

    @JvmStatic
    internal fun bindContent(
        entity: UiEntityOfSkeleton,
        itemView: LinearLayoutCompat,
        ccSkeletonFrameLayout: CanvasCoreSkeletonFrameLayout,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, itemView)
        updateDimensionsOf(entity, ccSkeletonFrameLayout)
    }

    @JvmStatic
    private fun updateDimensionsOf(
        entity: UiEntityOfSkeleton,
        ccSkeletonFrameLayout: CanvasCoreSkeletonFrameLayout,
    ) {

        val resources: Resources = ccSkeletonFrameLayout.resources
        val width: Int = computeWidth(entity, resources)
        val heightInPixels: Int = resources.getDimensionPixelSize(entity.height)

        ccSkeletonFrameLayout.updateLayoutParams<LayoutParams> {
            this.width = width
            this.height = heightInPixels
        }
    }

    @JvmStatic
    private fun computeWidth(entity: UiEntityOfSkeleton, resources: Resources): Int {
        if (entity.isStretchedWidth) return LayoutParams.MATCH_PARENT

        return resources.getDimensionPixelSize(entity.width)
    }
}
