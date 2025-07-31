package pe.com.scotiabank.blpm.android.ui.list.items.skeleton

import android.view.Gravity
import androidx.annotation.DimenRes
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfSkeleton(
    val paddingEntity: UiEntityOfPadding,
    val isStretchedWidth: Boolean = true,
    @DimenRes val width: Int = R.dimen.canvascore_width_24,
    @DimenRes val height: Int = R.dimen.canvascore_height_2,
    val gravity: Int = Gravity.START,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val shape: SkeletonShape = SkeletonShape.QUADRILATERAL,
    override val id: Long = randomLong(),
    changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfSkeleton>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfSkeleton
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isStretchedWidth == other.isStretchedWidth
            && width == other.width
            && height == other.height
            && gravity == other.gravity
            && expectedFlexGrow == other.expectedFlexGrow
            && shape == other.shape
}
