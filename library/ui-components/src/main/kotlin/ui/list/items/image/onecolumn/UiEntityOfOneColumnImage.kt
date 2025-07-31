package pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn

import androidx.annotation.DrawableRes
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.image.UiEntityOfImage
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfOneColumnImage(
    val paddingEntity: UiEntityOfPadding,
    val entityOfColumn: UiEntityOfImage,
    @DrawableRes drawableRes: Int,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfOneColumnImage>,
    ChangingState by changingState,
    Recycling by recycling
{

    var drawableRes: Int by Delegates.observable(
        drawableRes,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfOneColumnImage
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && entityOfColumn.isHoldingTheSameContentAs(other.entityOfColumn)
            && drawableRes == other.drawableRes
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        entityOfColumn.resetChangingState()
    }
}
