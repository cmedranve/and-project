package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary

import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfStaticChipsComponent

class CollectorOfFrequentOperationType(
    private val paddingEntity: UiEntityOfPadding,
    private val types: Collection<FrequentOperationType>,
) {

    private val _chipEntitiesByText: LinkedHashMap<String, UiEntityOfChip<FrequentOperationType>> = LinkedHashMap()
    val chipEntitiesByText: Map<String, UiEntityOfChip<FrequentOperationType>>
        get() = _chipEntitiesByText

    fun collect(
        controller: SelectionControllerOfChipsComponent<FrequentOperationType>,
    ): List<UiEntityOfStaticChipsComponent<FrequentOperationType>> {

        types.associateByTo(_chipEntitiesByText, ::byTextAsKey, ::toChipEntity)

        val collectionEntity = UiEntityOfStaticChipsComponent(
            paddingEntity = paddingEntity,
            controller = controller,
            _chipEntitiesByChipText = _chipEntitiesByText,
            isSelectionRequired = true,
        )
        controller.setComponentEntity(collectionEntity)

        return listOf(collectionEntity)
    }

    private fun byTextAsKey(type: FrequentOperationType): String = type.displayText

    private fun toChipEntity(
        type: FrequentOperationType,
    ): UiEntityOfChip<FrequentOperationType> = UiEntityOfChip(text = type.displayText, data = type)
}