package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import androidx.core.util.Consumer
import com.google.android.material.chip.ChipGroup
import com.scotiabank.canvascore.selectors.StaticChipsComponent
import pe.com.scotiabank.blpm.android.ui.databinding.ViewStaticChipsComponentItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiEntityOfError
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.attemptBindToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfStaticChipsComponent {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfStaticChipsComponent<D>, ViewStaticChipsComponentItemBinding>
    ) {
        val entity: UiEntityOfStaticChipsComponent<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        entity: UiEntityOfStaticChipsComponent<D>,
        binding: ViewStaticChipsComponentItemBinding
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        entity.title?.let(binding.sccChoices::setTitle)
        entity.subtitle?.let(binding.sccChoices::setSubtitle)
        attemptBindError(entity, binding)
        attemptBindToolTip(entity.toolTipEntity, binding, binding.sccChoices::setToolTip)
        bindChipGroup(entity, binding.sccChoices)
    }

    @JvmStatic
    private fun <D: Any> attemptBindError(
        entity: UiEntityOfStaticChipsComponent<D>,
        binding: ViewStaticChipsComponentItemBinding
    ) {
        val errorEntity: UiEntityOfError = entity.errorEntity ?: return

        binding.sccChoices.setError(errorEntity.errorRes)
        binding.sccChoices.setAccessibilityErrorLabel(errorEntity.accessibilityErrorLabel)
    }

    @JvmStatic
    private fun <D: Any> bindChipGroup(
        entity: UiEntityOfStaticChipsComponent<D>,
        sccChoices: StaticChipsComponent
    ) {
        val chipGroup: ChipGroup = FinderOfChipGroup.attemptFind(sccChoices) ?: return

        sccChoices.staticChipClicked = {}
        chipGroup.isSelectionRequired = false
        if (chipGroup.childCount == 0) {
            createChips(entity.isGoingToHideChipMargins, entity.chipEntitiesByChipText, sccChoices)
        }
        UiBinderOfChip.bindChips(entity.chipEntitiesByChipText, chipGroup)
        bindClickCallback(entity.onChipClicked, sccChoices)
        chipGroup.isSelectionRequired = entity.isSelectionRequired
    }

    @JvmStatic
    private fun <D: Any> createChips(
        isGoingToHideChipMargins: Boolean,
        chipEntitiesByChipText: Map<String, UiEntityOfChip<D>>,
        sccChoices: StaticChipsComponent
    ) {
        val chipEntities: Collection<UiEntityOfChip<D>> = chipEntitiesByChipText.values
        chipEntities.forEach { chipEntity ->
            sccChoices.createChip(chipEntity.text, chipEntity.isChecked, isGoingToHideChipMargins)
        }
    }

    @JvmStatic
    private fun bindClickCallback(
        clickCallback: Consumer<String?>,
        sccChoices: StaticChipsComponent
    ) {
        sccChoices.staticChipClicked = { chipText -> clickCallback.accept(chipText) }
    }
}
