package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import androidx.annotation.DrawableRes
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfChevron(
    @DrawableRes val iconRes: Int = R.drawable.canvascore_icon_chevron_right_blue,
    val show: Boolean = true,
    override val id: Long = randomLong(),
    changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfChevron>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfChevron
    ): Boolean = isUnmodified
            && iconRes == other.iconRes
            && show == other.show
}
