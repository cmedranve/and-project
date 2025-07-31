package pe.com.scotiabank.blpm.android.ui.list.items.inputs.pintext

import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfPinText<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    titleText: String = Constant.EMPTY,
    pinCount: Int = 0,
    pin: Boolean = true,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfPinText<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var titleText: String by Delegates.observable(
        titleText,
        ::onChangeOfNonEntityProperty
    )

    var pinCount: Int by Delegates.observable(
        pinCount,
        ::onChangeOfNonEntityProperty
    )

    var pin: Boolean by Delegates.observable(
        pin,
        ::onChangeOfNonEntityProperty
    )

    private var observableText: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var text: CharSequence = Constant.EMPTY
        set(value) {
            observableText = value.toString()
            field = value
        }

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfPinText<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && titleText.contentEquals(other.titleText)
            && text.contentEquals(other.text)

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }

    override fun reset() {
        text = Constant.EMPTY
    }
}
