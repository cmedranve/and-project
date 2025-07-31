package pe.com.scotiabank.blpm.android.ui.list.items.quickaction

import androidx.appcompat.widget.AppCompatImageView
import pe.com.scotiabank.blpm.android.ui.databinding.ViewQuickActionItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiBinderOfText
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfQuickAction {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfQuickAction<D>, ViewQuickActionItemBinding>
    ) {
        val entity: UiEntityOfQuickAction<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfQuickAction<D>,
        binding: ViewQuickActionItemBinding
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindIcon(entity, binding.ivIcon)
        UiBinderOfText.bindTextProperties(entity.entityOfText, binding.tvLabel)
        UiBinderOfClickCallback.bindNonClickableOrClickableBackground(entity, entity.receiver, binding.qa)
    }

    @JvmStatic
    private fun <D: Any> bindIcon(entity: UiEntityOfQuickAction<D>, ivIcon: AppCompatImageView) {
        ivIcon.setImageResource(entity.iconRes)
    }
}
