package pe.com.scotiabank.blpm.android.ui.list.items.card

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

class UiEntityOfCard<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    val recyclerEntity: UiEntityOfRecycler,
    val useCompatPadding: Boolean = true,
    @DimenRes val cornerRadiusRes: Int = R.dimen.canvascore_card_view_corner_radius,
    @DimenRes val elevationRes: Int = R.dimen.canvascore_card_flat_elevation,
    @ColorRes val backgroundColorRes: Int = R.color.canvascore_background,
    @DimenRes val strokeWidthRes: Int = R.dimen.canvascore_border_width_1,
    @ColorRes val strokeColorRes: Int = R.color.canvascore_card_float_border,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfCard<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfCard<D>,
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && cornerRadiusRes == other.cornerRadiusRes
            && elevationRes == other.elevationRes
            && backgroundColorRes == other.backgroundColorRes
            && strokeWidthRes == other.strokeWidthRes
            && strokeColorRes == other.strokeColorRes
            && expectedFlexGrow == other.expectedFlexGrow
            && recyclerEntity.isHoldingTheSameContentAs(other.recyclerEntity)

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
