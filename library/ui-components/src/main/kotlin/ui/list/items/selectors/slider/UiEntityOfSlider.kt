package pe.com.scotiabank.blpm.android.ui.list.items.selectors.slider

import androidx.core.util.Function
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfSlider(
    val paddingEntity: UiEntityOfPadding,
    val entityOfTitle: UiEntityOfText,
    val formattingCallback: Function<Float, CharSequence>,
    val maxValueLabel: CharSequence,
    val maxValue: Float,
    val stepSize: Float = 0f,
    val minValue: Float = 0f,
    val initialValue: Float = minValue,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    currentValue: Float? = null,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfSlider>,
    ChangingState by changingState,
    Recycling by recycling
{

    var currentValue: Float? by Delegates.observable(
        currentValue,
        ::onChangeOfNonEntityProperty
    )

    var isValueFromUser: Boolean = false
        internal set

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfSlider
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && entityOfTitle.isHoldingTheSameContentAs(other.entityOfTitle)
            && maxValueLabel.contentEquals(other.maxValueLabel)
            && maxValue == other.maxValue
            && stepSize == other.stepSize
            && minValue == other.minValue
            && initialValue == other.initialValue
            && currentValue == other.currentValue
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        entityOfTitle.resetChangingState()
    }
}
