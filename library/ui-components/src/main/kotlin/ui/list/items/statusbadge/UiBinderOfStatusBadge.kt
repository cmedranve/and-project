package pe.com.scotiabank.blpm.android.ui.list.items.statusbadge

import com.scotiabank.canvascore.views.StatusBadge
import pe.com.scotiabank.blpm.android.ui.databinding.ViewStatusBadgeItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfStatusBadge {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfStatusBadge, ViewStatusBadgeItemBinding>) {
        val entity: UiEntityOfStatusBadge = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(entity: UiEntityOfStatusBadge, binding: ViewStatusBadgeItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        binding.root.gravity = entity.gravity
        bindContent(entity, binding.sBadge)
    }

    @JvmStatic
    private fun bindContent(entity: UiEntityOfStatusBadge, sBadge: StatusBadge) {
        sBadge.text = entity.text
        sBadge.setType(entity.type)
    }
}
