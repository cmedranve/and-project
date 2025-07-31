package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfChip<D: Any>(
    val text: String,
    val data: D? = null,
    isEnabled: Boolean = true,
    isCheckable: Boolean = true,
    override val id: Long = randomLong(),
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfChip<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    var isCheckable: Boolean by Delegates.observable(
        isCheckable,
        ::onChangeOfNonEntityProperty
    )

    var mutableIsChecked: Boolean by Delegates.observable(
        false,
        ::onChangeOfNonEntityProperty
    )
    val isChecked: Boolean
        get() = mutableIsChecked

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfChip<D>
    ): Boolean = isUnmodified
            && text.contentEquals(other.text)
            && isEnabled == other.isEnabled
            && isChecked == other.isChecked
            && isCheckable && other.isCheckable
}
