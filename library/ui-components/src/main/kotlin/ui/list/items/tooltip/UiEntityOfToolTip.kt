package pe.com.scotiabank.blpm.android.ui.list.items.tooltip

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfToolTip(
    val accessibilityText: String,
    val headlineText: String,
    val contentText: String,
    val buttonText: String,
    override val id: Long = randomLong(),
    changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfToolTip>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfToolTip
    ): Boolean = isUnmodified
            && accessibilityText.contentEquals(other.accessibilityText)
            && headlineText.contentEquals(other.headlineText)
            && contentText.contentEquals(other.contentText)
            && buttonText.contentEquals(other.buttonText)
}
