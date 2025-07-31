package pe.com.scotiabank.blpm.android.client.base.products.newpicking.installmentchips

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfStaticChipsComponent
import java.lang.ref.WeakReference

class CollectorOfInstallmentChipsComponent(
    private val collection: Collection<InstallmentOption>,
    private val default: InstallmentOption,
    private val weakResources: WeakReference<Resources?>,
    private val horizontalPaddingEntity: UiEntityOfPadding,
) {

    private val paddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_30,
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
        )
    }

    fun collect(
        controller: SelectionControllerOfChipsComponent<InstallmentOption>,
    ): List<UiEntityOfStaticChipsComponent<InstallmentOption>> {

        val chipEntitiesByLabel: LinkedHashMap<String, UiEntityOfChip<InstallmentOption>> = LinkedHashMap()
        collection.associateByTo(chipEntitiesByLabel, ::byLabelAsKey, ::toChipEntity)

        val defaultChipEntity: UiEntityOfChip<InstallmentOption> = chipEntitiesByLabel
            .firstNotNullOfOrNull(::findDefault)
            ?: return emptyList()

        val collectionEntity = UiEntityOfStaticChipsComponent(
            paddingEntity = paddingEntity,
            controller = controller,
            _chipEntitiesByChipText = chipEntitiesByLabel,
            isSelectionRequired = true,
            title = weakResources.get()?.getString(R.string.installments_number),
        )
        controller.setComponentEntity(collectionEntity)
        controller.setDefaultChip(defaultChipEntity)

        return listOf(collectionEntity)
    }

    private fun byLabelAsKey(option: InstallmentOption): String = option.label

    private fun toChipEntity(
        option: InstallmentOption,
    ): UiEntityOfChip<InstallmentOption> = UiEntityOfChip(text = option.label, data = option)

    private fun findDefault(
        entry: Map.Entry<String, UiEntityOfChip<InstallmentOption>>,
    ): UiEntityOfChip<InstallmentOption>? {
        val entity: UiEntityOfChip<InstallmentOption> = entry.value
        if (default == entity.data) return entity
        return null
    }
}
