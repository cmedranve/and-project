package pe.com.scotiabank.blpm.android.ui.list.items

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfClearButton(
    val isEnabled: Boolean,
    val contentDescription: CharSequence,
    override val id: Long = randomLong(),
    changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfClearButton>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfClearButton
    ): Boolean = isUnmodified
            && isEnabled == other.isEnabled
            && contentDescription.contentEquals(other.contentDescription)
}
