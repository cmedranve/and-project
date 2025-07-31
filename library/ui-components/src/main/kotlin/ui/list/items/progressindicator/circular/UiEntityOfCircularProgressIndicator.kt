package pe.com.scotiabank.blpm.android.ui.list.items.progressindicator.circular

import androidx.annotation.DimenRes
import com.scotiabank.canvascore.R
import com.scotiabank.canvascore.utils.ProgressColor
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfCircularProgressIndicator<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    progressValue: Int = 0,
    maxValue: Int = 100,
    color: ProgressColor = ProgressColor.BRAND_BLUE,
    @DimenRes val sizeRes: Int = R.dimen.canvascore_margin_32,
    val data: D? = null,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfCircularProgressIndicator<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var progressValue: Int by Delegates.observable(
        progressValue,
        ::onChangeOfNonEntityProperty,
    )

    var maxValue: Int by Delegates.observable(
        maxValue,
        ::onChangeOfNonEntityProperty,
    )

    var color: ProgressColor by Delegates.observable(
        color,
        ::onChangeOfNonEntityProperty,
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfCircularProgressIndicator<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && progressValue == other.progressValue
            && maxValue == other.maxValue
            && color.color == other.color.color
            && sizeRes == other.sizeRes
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
