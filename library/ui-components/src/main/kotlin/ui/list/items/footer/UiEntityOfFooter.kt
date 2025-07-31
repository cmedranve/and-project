package pe.com.scotiabank.blpm.android.ui.list.items.footer

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.toUiEntities
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling

class UiEntityOfFooter(
    val compoundsById: LinkedHashMap<Long, UiCompound<*>>,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = ImmutableState,
    private val recycling: Recycling = StatefulRecycling(),
) : IdentifiableUiEntity<UiEntityOfFooter>,
    ChangingState by changingState,
    Recycling by recycling
{

    val totalSizeOfEntities: Int
        get() = compoundsById.flatMap(::toUiEntities).size

    val isAnyItemVisible: Boolean
        get() = compoundsById.any(::isAnyVisible)

    private fun isAnyVisible(compoundById: Map.Entry<Long, UiCompound<*>>): Boolean {
        val compound: UiCompound<*> = compoundById.value
        if (compound.visibilitySupplier.get()) {
            return compound.uiEntities.isNotEmpty()
        }
        return false
    }

    override fun isHoldingTheSameContentAs(other: UiEntityOfFooter): Boolean = false

    override fun resetChangingState() {
        changingState.resetChangingState()
    }
}
