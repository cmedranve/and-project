package pe.com.scotiabank.blpm.android.ui.list.items.skeleton.doubleended

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.skeleton.UiEntityOfSkeleton
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.BIAS_AT_ZERO

class UiEntityOfDoubleEndedSkeleton(
    val leftSkeletonEntity: UiEntityOfSkeleton,
    val verticalBiasOfLeftSkeleton: Float = BIAS_AT_ZERO,
    val rightSkeletonEntity: UiEntityOfSkeleton,
    val verticalBiasOfRightSkeleton: Float = BIAS_AT_ZERO,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val centerRecyclerEntity: UiEntityOfRecycler,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfDoubleEndedSkeleton>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfDoubleEndedSkeleton,
    ): Boolean = isUnmodified
            && leftSkeletonEntity == other.leftSkeletonEntity
            && verticalBiasOfLeftSkeleton == other.verticalBiasOfLeftSkeleton
            && rightSkeletonEntity == other.rightSkeletonEntity
            && verticalBiasOfRightSkeleton == other.verticalBiasOfRightSkeleton
            && expectedFlexGrow == other.expectedFlexGrow
            && centerRecyclerEntity.isHoldingTheSameContentAs(other.centerRecyclerEntity)

    override fun resetChangingState() {
        changingState.resetChangingState()
        leftSkeletonEntity.resetChangingState()
        rightSkeletonEntity.resetChangingState()
    }
}
