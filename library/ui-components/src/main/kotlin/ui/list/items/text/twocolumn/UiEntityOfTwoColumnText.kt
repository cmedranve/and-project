package pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfTwoColumnText(
    val paddingEntity: UiEntityOfPadding,
    val entityOfColumn1: UiEntityOfText,
    val entityOfColumn2: UiEntityOfText,
    val guidelinePercent: Float = GUIDELINE_AT_MIDDLE,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfTwoColumnText>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfTwoColumnText
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && entityOfColumn1.isHoldingTheSameContentAs(other.entityOfColumn1)
            && entityOfColumn2.isHoldingTheSameContentAs(other.entityOfColumn2)
            && guidelinePercent == other.guidelinePercent

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        entityOfColumn1.resetChangingState()
        entityOfColumn2.resetChangingState()
    }

    companion object {

        private val GUIDELINE_AT_MIDDLE: Float
            get() = 0.5f
    }
}
