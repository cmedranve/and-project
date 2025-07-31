package pe.com.scotiabank.blpm.android.ui.list.items.swipe

import androidx.annotation.ColorRes
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfSwipe(
    state: SwipeState,
    @ColorRes backgroundColorRes: Int = R.color.canvascore_background,
    @ColorRes colorSchemaRes: List<Int> = listOf(R.color.canvascore_brand_red),
    val receiver: InstanceReceiver? = null,
    override val id: Long = randomLong(),
    changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfSwipe>,
    ChangingState by changingState,
    Recycling by recycling
{

    var state: SwipeState by Delegates.observable(
        state,
        ::onChangeOfNonEntityProperty
    )

    var backgroundColorRes: Int by Delegates.observable(
        backgroundColorRes,
        ::onChangeOfNonEntityProperty
    )

    var colorSchemaRes: List<Int> by Delegates.observable(
        colorSchemaRes,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfSwipe,
    ): Boolean = isUnmodified
            && state == other.state
            && backgroundColorRes == other.backgroundColorRes
            && colorSchemaRes == other.colorSchemaRes
}
