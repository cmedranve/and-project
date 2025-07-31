package pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfDropdownSelectorItem<D: Any>(
    val text: String,
    val data: D? = null,
    override val id: Long = randomLong(),
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfDropdownSelectorItem<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    internal var mutableIsSelected: Boolean by Delegates.observable(
        false,
        ::onChangeOfNonEntityProperty
    )
    val isSelected: Boolean
        get() = mutableIsSelected

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfDropdownSelectorItem<D>
    ): Boolean = isUnmodified && text.contentEquals(other.text)
}
