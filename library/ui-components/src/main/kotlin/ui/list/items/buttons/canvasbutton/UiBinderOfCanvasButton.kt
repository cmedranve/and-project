package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton

import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.buttons.CanvasButton
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCanvasButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfCanvasButton {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfCanvasButton<D>, ViewCanvasButtonItemBinding>,
    ) {
        val entity: UiEntityOfCanvasButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfCanvasButton<D>,
        binding: ViewCanvasButtonItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindType(entity, binding.cButton)
        bindContent(entity, binding.cButton)
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, binding.cButton)
    }

    @JvmStatic
    private fun <D: Any> bindType(entity: UiEntityOfCanvasButton<D>, cButton: CanvasButton) {
        if (CanvasButton.PRIMARY == entity.type) {
            cButton.changeToPrimary()
            return
        }
        if (CanvasButton.SECONDARY == entity.type) {
            cButton.changeToSecondary()
        }
    }

    @JvmStatic
    private fun <D: Any> bindContent(entity: UiEntityOfCanvasButton<D>, cButton: CanvasButton) {
        cButton.isEnabled = entity.isEnabled
        cButton.text = entity.text
        cButton.setCompoundDrawablesWithIntrinsicBounds(
            ResourcesCompat.ID_NULL,
            ResourcesCompat.ID_NULL,
            entity.drawableEndId,
            ResourcesCompat.ID_NULL,
        )
    }
}
