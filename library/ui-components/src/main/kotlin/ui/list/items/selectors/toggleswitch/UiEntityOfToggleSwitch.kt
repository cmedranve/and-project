package pe.com.scotiabank.blpm.android.ui.list.items.selectors.toggleswitch

import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfToggleSwitch<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    text: CharSequence = Constant.EMPTY,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    isChecked: Boolean = false,
    isEnabled: Boolean = true,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfToggleSwitch<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    private var observableText: String by Delegates.observable(
        text.toString(),
        ::onChangeOfNonEntityProperty
    )

    var text: CharSequence = text
        set(value) {
            observableText = value.toString()
            field = value
        }

    var isChecked: Boolean by Delegates.observable(
        isChecked,
        ::onChangeOfNonEntityProperty
    )

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfToggleSwitch<D>
    ): Boolean = isUnmodified
            && text.contentEquals(other.text)
            && isChecked == other.isChecked
            && isEnabled == other.isEnabled
            && expectedFlexGrow == other.expectedFlexGrow
}
