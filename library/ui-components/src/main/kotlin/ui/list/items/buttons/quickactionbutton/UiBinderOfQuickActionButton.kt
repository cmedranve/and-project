package pe.com.scotiabank.blpm.android.ui.list.items.buttons.quickactionbutton

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.scotiabank.canvascore.buttons.QuickActionButton
import pe.com.scotiabank.blpm.android.ui.databinding.ViewQuickActionButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfQuickActionButton {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfQuickActionButton<D>, ViewQuickActionButtonItemBinding>,
    ) {
        val entity: UiEntityOfQuickActionButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfQuickActionButton<D>,
        binding: ViewQuickActionButtonItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        binding.root.gravity = entity.gravity

        bindTitle(entity, binding.qActionButton)
        bindTitleSize(entity, binding.qActionButton)
        bindTitleColor(entity, binding.qActionButton)
        bindIcon(entity, binding.qActionButton)

        attemptBindWhiteBorder(entity, binding.qActionButton)
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, binding.qActionButton)
    }

    @JvmStatic
    private fun <D: Any> bindTitle(
        entity: UiEntityOfQuickActionButton<D>,
        qActionButton: QuickActionButton,
    ) {
        qActionButton.isEnabled = entity.isEnabled
        qActionButton.setTitle(entity.title)
    }

    @JvmStatic
    private fun <D: Any> bindTitleSize(
        entity: UiEntityOfQuickActionButton<D>,
        qActionButton: QuickActionButton,
    ) {
        val resources: Resources = qActionButton.resources
        val titleSize: Float = resources.getDimension(entity.titleSizeId)
        if (qActionButton.getTextView()?.textSize == titleSize) return

        qActionButton.getTextView()?.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
    }

    @JvmStatic
    private fun <D: Any> bindTitleColor(
        entity: UiEntityOfQuickActionButton<D>,
        qActionButton: QuickActionButton,
    ) {
        @ColorInt val titleColor: Int = ContextCompat.getColor(qActionButton.context, entity.titleColorId)
        if (qActionButton.getTextView()?.currentTextColor == titleColor) return

        qActionButton.setTitleColor(titleColor)
    }

    @JvmStatic
    private fun <D: Any> bindIcon(
        entity: UiEntityOfQuickActionButton<D>,
        qActionButton: QuickActionButton,
    ) = qActionButton.setIcon(entity.iconId)

    @JvmStatic
    private fun <D: Any> attemptBindWhiteBorder(
        entity: UiEntityOfQuickActionButton<D>,
        qActionButton: QuickActionButton,
    ) {
        if (entity.isWhiteBorderNeeded.not()) return

        qActionButton.setWhiteBorderColor()
    }
}
