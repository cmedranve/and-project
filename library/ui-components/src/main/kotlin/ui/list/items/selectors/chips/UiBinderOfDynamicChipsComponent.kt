package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import android.widget.HorizontalScrollView
import androidx.core.util.Consumer
import com.google.android.material.chip.ChipGroup
import com.scotiabank.canvascore.selectors.DynamicChipsComponent
import pe.com.scotiabank.blpm.android.ui.databinding.ViewDynamicChipsComponentItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiEntityOfError
import pe.com.scotiabank.blpm.android.ui.list.items.horizontalscrollview.FinderOfHorizontalScrollView
import pe.com.scotiabank.blpm.android.ui.list.items.horizontalscrollview.ScrollOffset
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.attemptBindToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfDynamicChipsComponent {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfDynamicChipsComponent<D>, ViewDynamicChipsComponentItemBinding>
    ) {
        val entity: UiEntityOfDynamicChipsComponent<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfDynamicChipsComponent<D>,
        binding: ViewDynamicChipsComponentItemBinding
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        entity.title?.let(binding.dccChoices::setTitle)
        entity.subtitle?.let(binding.dccChoices::setSubtitle)
        attemptBindError(entity, binding)
        attemptBindToolTip(entity.toolTipEntity, binding, binding.dccChoices::setToolTip)
        bindChipGroup(entity, binding.dccChoices)
        ScrollOffset.restoreOffsetFromStart(entity, binding.dccChoices)
    }

    @JvmStatic
    private fun <D: Any> attemptBindError(
        entity: UiEntityOfDynamicChipsComponent<D>,
        binding: ViewDynamicChipsComponentItemBinding
    ) {
        val errorEntity: UiEntityOfError = entity.errorEntity ?: return

        binding.dccChoices.setError(errorEntity.errorRes)
        binding.dccChoices.setAccessibilityErrorLabel(errorEntity.accessibilityErrorLabel)
    }

    @JvmStatic
    private fun <D: Any> bindChipGroup(
        entity: UiEntityOfDynamicChipsComponent<D>,
        dccChoices: DynamicChipsComponent
    ) {
        val hsv: HorizontalScrollView = FinderOfHorizontalScrollView.attemptFind(dccChoices) ?: return
        val chipGroup: ChipGroup = FinderOfChipGroup.attemptFind(hsv) ?: return

        dccChoices.chipClicked = {}
        chipGroup.isSelectionRequired = false
        if (chipGroup.childCount == 0) {
            createChips(entity.chipEntitiesByChipText, dccChoices)
        }
        UiBinderOfChip.bindChips(entity.chipEntitiesByChipText, chipGroup)
        bindClickCallback(entity.onChipClicked, dccChoices)
        chipGroup.isSelectionRequired = entity.isSelectionRequired
    }

    @JvmStatic
    private fun <D: Any> createChips(
        chipEntitiesByChipText: Map<String, UiEntityOfChip<D>>,
        dccChoices: DynamicChipsComponent
    ) {
        val chipEntities: Collection<UiEntityOfChip<D>> = chipEntitiesByChipText.values
        chipEntities.forEach { chipEntity ->
            dccChoices.createChip(chipEntity.text, chipEntity.isChecked)
        }
    }

    @JvmStatic
    private fun bindClickCallback(
        clickCallback: Consumer<String?>,
        dccChoices: DynamicChipsComponent
    ) {
        dccChoices.chipClicked = { chipText -> clickCallback.accept(chipText) }
    }
}
