package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import android.view.View
import androidx.core.view.allViews
import com.google.android.material.chip.ChipGroup

internal object FinderOfChipGroup {

    @JvmStatic
    fun attemptFind(parentView: View): ChipGroup? {
        val children: Sequence<View> = parentView.allViews
        for (anyChild: View in children) {
            if (anyChild is ChipGroup) return anyChild
        }
        return null
    }
}
