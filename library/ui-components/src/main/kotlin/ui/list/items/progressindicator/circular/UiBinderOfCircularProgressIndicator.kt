package pe.com.scotiabank.blpm.android.ui.list.items.progressindicator.circular

import android.content.res.Resources
import androidx.annotation.DimenRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.updateLayoutParams
import com.scotiabank.canvascore.views.CanvasCircularProgressIndicator
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCircularProgressIndicatorItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfCircularProgressIndicator {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfCircularProgressIndicator<D>, ViewCircularProgressIndicatorItemBinding>,
    ) {
        val entity: UiEntityOfCircularProgressIndicator<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfCircularProgressIndicator<D>,
        binding: ViewCircularProgressIndicatorItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindSize(entity.sizeRes, binding.ccpiInfo)
        binding.ccpiInfo.setAttributes(
            initialValue = entity.progressValue,
            maxValue = entity.maxValue,
            color = entity.color,
        )
    }

    @JvmStatic
    internal fun bindSize(@DimenRes sizeRes: Int, circularProgressIndicator: CanvasCircularProgressIndicator) {
        val res: Resources = circularProgressIndicator.resources
        val size: Int = res.getDimensionPixelOffset(sizeRes)
        circularProgressIndicator.updateLayoutParams<LinearLayoutCompat.LayoutParams> {
            width = size
            height = size
        }
    }
}
