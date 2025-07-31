package pe.com.scotiabank.blpm.android.ui.list.items.buttons.pillbutton

import com.scotiabank.canvascore.buttons.PillButton.Companion.PillButtonType
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

class UiEntityOfPillButton<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    isEnabled: Boolean,
    text: CharSequence = Constant.EMPTY,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    type: PillButtonType = PillButtonType.TYPE_DEFAULT,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfPillButton<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    private var observableText: String by Delegates.observable(
        text.toString(),
        ::onChangeOfNonEntityProperty
    )

    var text: CharSequence = text
        set(value) {
            observableText = value.toString()
            field = value
        }

    var type: PillButtonType by Delegates.observable(
        type,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other:UiEntityOfPillButton<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isEnabled == other.isEnabled
            && text.contentEquals(other.text)
            && type == other.type
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
