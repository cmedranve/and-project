package pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton

import android.content.res.Resources
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.scotiabank.canvascore.R
import com.scotiabank.canvascore.buttons.TextButton
import pe.com.scotiabank.blpm.android.ui.databinding.ViewTextButtonItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

object UiBinderOfTextButton {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfTextButton<D>, ViewTextButtonItemBinding>,
    ) {
        val entity: UiEntityOfTextButton<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfTextButton<D>, binding: ViewTextButtonItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        binding.root.gravity = entity.gravity
        updateMinimumDimensionsOf(binding.tButton)
        bindClickableContent(entity, binding.tButton)
    }

    @JvmStatic
    fun <D: Any> bindTextButton(entity: UiEntityOfTextButton<D>, textButton: TextButton) {
        UiBinderOfPadding.bind(entity.paddingEntity, textButton)
        UiBinderOfWidthParam.bind(child = textButton, expectedFlexGrow = entity.expectedFlexGrow)
        textButton.gravity = entity.gravity
        updateMinimumDimensionsOf(textButton)
        bindClickableContent(entity, textButton)
    }

    @JvmStatic
    internal fun updateMinimumDimensionsOf(tButton: TextButton) {
        val res: Resources = tButton.resources
        val pixels: Int = res.getDimensionPixelOffset(R.dimen.canvascore_margin_32)
        bindIfDifferent(pixels, tButton::getMinWidth, tButton::setMinWidth)
        bindIfDifferent(pixels, tButton::getMinHeight, tButton::setMinHeight)
    }

    @JvmStatic
    internal fun <D: Any> bindClickableContent(entity: UiEntityOfTextButton<D>, tButton: TextButton) {
        tButton.isEnabled = entity.isEnabled
        bindTextContent(entity, tButton)
        bindDrawables(entity, tButton)
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, tButton)

        if (entity.isEnabled) return
        setTintForDisabledState(tButton)
    }

    @JvmStatic
    private fun <D: Any> bindTextContent(entity: UiEntityOfTextButton<D>, tButton: TextButton) {
        tButton.text = entity.text
        @StyleRes val textAppearance: Int = pickAppearanceForEnabledState(entity)
        TextViewCompat.setTextAppearance(tButton, textAppearance)
    }

    @JvmStatic
    @StyleRes
    private fun <D: Any> pickAppearanceForEnabledState(
        entity: UiEntityOfTextButton<D>,
    ): Int = if (entity.isEnabled) entity.appearanceForEnabledState else R.style.canvascore_style_text_button_disabled

    @JvmStatic
    private fun <D: Any> bindDrawables(entity: UiEntityOfTextButton<D>, tButton: TextButton) {

        tButton.setCompoundDrawablesWithIntrinsicBounds(
            entity.drawableStartId,
            ResourcesCompat.ID_NULL,
            entity.drawableEndId,
            ResourcesCompat.ID_NULL,
        )

        val resources: Resources = tButton.resources
        tButton.compoundDrawablePadding = resources.getDimensionPixelSize(entity.drawablePadding)
    }

    @JvmStatic
    private fun setTintForDisabledState(tButton: TextButton) {

        @ColorInt val colorInt: Int = ContextCompat.getColor(tButton.context, R.color.canvascore_gray_550)
        for (drawable in tButton.compoundDrawablesRelative) {
            drawable?.setTint(colorInt)
        }
    }
}
