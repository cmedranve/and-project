package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.toUiEntities
import pe.com.scotiabank.blpm.android.ui.list.decoration.DecorationCompound
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling

class UiEntityOfRecycler(
    val paddingEntity: UiEntityOfPadding,
    val compoundsById: LinkedHashMap<Long, UiCompound<*>>,
    val layoutManagerFactory: LayoutManagerFactory,
    val decorationCompounds: List<DecorationCompound> = emptyList(),
    val clipToPadding: Boolean = false,
    val isNestedScrollingEnabled: Boolean = false,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = ImmutableState,
    private val recycling: Recycling = StatefulRecycling(),
) : IdentifiableUiEntity<UiEntityOfRecycler>,
    ChangingState by changingState,
    Recycling by recycling
{

    val totalSizeOfEntities: Int
        get() = compoundsById.flatMap(::toUiEntities).size

    override fun isHoldingTheSameContentAs(other: UiEntityOfRecycler): Boolean = false

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }

    override fun reset() {
        compoundsById.values.forEach(::resetCompound)
    }

    private fun resetCompound(compound: UiCompound<*>) {
        compound.uiEntities.forEach(::resetItemEntity)
    }

    private fun resetItemEntity(entity: IdentifiableUiEntity<*>) {
        entity.reset()
    }

    fun copyWith(
        newCompoundsById: LinkedHashMap<Long, UiCompound<*>>,
    ): UiEntityOfRecycler = UiEntityOfRecycler(
        paddingEntity = paddingEntity,
        compoundsById = newCompoundsById,
        layoutManagerFactory = layoutManagerFactory,
        decorationCompounds = decorationCompounds,
        clipToPadding = clipToPadding,
        isNestedScrollingEnabled = isNestedScrollingEnabled,
        expectedFlexGrow = expectedFlexGrow,
        id = id,
        changingState = changingState,
        recycling = recycling,
    )
}
