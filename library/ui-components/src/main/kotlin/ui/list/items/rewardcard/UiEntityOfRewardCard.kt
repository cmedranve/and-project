package pe.com.scotiabank.blpm.android.ui.list.items.rewardcard

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfRewardCard<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    isMainStyle: Boolean = true,
    labelPoints: CharSequence = Constant.EMPTY,
    points: CharSequence = Constant.EMPTY,
    pointsEquivalence: CharSequence = Constant.EMPTY,
    pointsRate: CharSequence = Constant.EMPTY,
    @DrawableRes val iconRes: Int = ResourcesCompat.ID_NULL,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity< UiEntityOfRewardCard<D>>,
    ChangingState by changingState,
    Recycling by recycling
{
    var isMainStyle: Boolean by Delegates.observable(
        isMainStyle,
        ::onChangeOfNonEntityProperty
    )

    private var observableLabelPoints: String by Delegates.observable(
        labelPoints.toString(),
        ::onChangeOfNonEntityProperty
    )

    var labelPoints: CharSequence = labelPoints
        set(value) {
            observableLabelPoints = value.toString()
            field = value
        }

    private var observablePoints: String by Delegates.observable(
        points.toString(),
        ::onChangeOfNonEntityProperty
    )

    var points: CharSequence = points
        set(value) {
            observablePoints = value.toString()
            field = value
        }

    private var observablePointsEquivalence: String by Delegates.observable(
        pointsEquivalence.toString(),
        ::onChangeOfNonEntityProperty
    )

    var pointsEquivalence: CharSequence = pointsEquivalence
        set(value) {
            observablePointsEquivalence = value.toString()
            field = value
        }

    private var observablePointsToValue: String by Delegates.observable(
        pointsRate.toString(),
        ::onChangeOfNonEntityProperty
    )

    var pointsRate: CharSequence = pointsRate
        set(value) {
            observablePointsToValue = value.toString()
            field = value
        }

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfRewardCard<D>,
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isMainStyle == other.isMainStyle
            && labelPoints.contentEquals(other.labelPoints)
            && points.contentEquals(other.points)
            && pointsEquivalence.contentEquals(other.pointsEquivalence)
            && pointsRate.contentEquals(other.pointsRate)
            && iconRes == other.iconRes
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
