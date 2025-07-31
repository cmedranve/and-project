package pe.com.scotiabank.blpm.android.ui.list.items.page

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.swipe.UiEntityOfSwipe
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling

class UiEntityOfPage(
    val swipeEntity: UiEntityOfSwipe,
    val recyclerEntity: UiEntityOfRecycler,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatefulRecycling(),
) : IdentifiableUiEntity<UiEntityOfPage>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(other: UiEntityOfPage): Boolean = false

    override fun resetChangingState() {
        changingState.resetChangingState()
    }

    override fun reset() {
        recyclerEntity.reset()
    }
}
