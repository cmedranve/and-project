package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import android.view.View
import androidx.core.view.allViews
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

internal object UiBinderOfChip {

    @JvmStatic
    fun <D: Any> bindChips(
        chipEntitiesByChipText: Map<String, UiEntityOfChip<D>>,
        chipGroup: ChipGroup
    ) {
        val children: Sequence<View> = chipGroup.allViews
        for (anyChild: View in children) {
            val chip: Chip = anyChild as? Chip ?: continue
            val chipText: CharSequence = chip.text
            val chipEntity: UiEntityOfChip<D> = chipEntitiesByChipText[chipText.toString()] ?: continue
            bindChip(chipEntity, chip)
        }
    }

    @JvmStatic
    private fun <D: Any> bindChip(chipEntity: UiEntityOfChip<D>, chip: Chip) {
        val isExpectedChecked: Boolean = chipEntity.isChecked == chip.isChecked
        if (isExpectedChecked) {
            bindIfDifferent(chipEntity.isCheckable, chip::isCheckable, chip::setCheckable)
            bindIfDifferent(chipEntity.isEnabled, chip::isEnabled, chip::setEnabled)
            return
        }
        forceBindChecked(chipEntity, chip)
    }

    /**
     * We need to set true at Chip.isCheckable before setting the new Chip.isChecked value.
     *
     * After that, we return Chip.isCheckable to its original state by retrieving the original
     * boolean from UiEntityOfChip.isCheckable.
     * */
    @JvmStatic
    private fun <D: Any> forceBindChecked(chipEntity: UiEntityOfChip<D>, chip: Chip) {
        chip.isEnabled = true
        chip.isCheckable = true

        chip.isChecked = chipEntity.isChecked

        chip.isCheckable = chipEntity.isCheckable
        chip.isEnabled = chipEntity.isEnabled
    }
}
