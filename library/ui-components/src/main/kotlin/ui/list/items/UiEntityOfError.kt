package pe.com.scotiabank.blpm.android.ui.list.items

import androidx.annotation.StringRes
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfError(
    @StringRes val errorRes: Int,
    val accessibilityErrorLabel: String,
    override val id: Long = randomLong(),
    changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfError>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfError
    ): Boolean = isUnmodified
            && errorRes == other.errorRes
            && accessibilityErrorLabel.contentEquals(other.accessibilityErrorLabel)
}
