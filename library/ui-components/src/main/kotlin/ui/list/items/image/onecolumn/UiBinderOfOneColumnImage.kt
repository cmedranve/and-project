package pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import pe.com.scotiabank.blpm.android.ui.databinding.ViewOneColumnImageItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiBinderOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfOneColumnImage {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfOneColumnImage, ViewOneColumnImageItemBinding>) {
        val entity: UiEntityOfOneColumnImage = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfOneColumnImage, binding: ViewOneColumnImageItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        attemptBindDrawable(entity.drawableRes, binding.ivColumn)
        UiBinderOfImage.bind(entity.entityOfColumn, binding.ivColumn)
    }

    @JvmStatic
    internal fun attemptBindDrawable(@DrawableRes drawableRes: Int, ivColumn: ImageView) {

        if (ResourcesCompat.ID_NULL == drawableRes) {
            ivColumn.setImageDrawable(null)
            return
        }

        ivColumn.setImageResource(drawableRes)
    }
}
