package pe.com.scotiabank.blpm.android.ui.list.items.otp

import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfOtp(
    val paddingEntity: UiEntityOfPadding,
    isEnabled: Boolean = true,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfOtp>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    private var observableText1: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var text1: CharSequence = Constant.EMPTY
        set(value) {
            observableText1 = value.toString()
            field = value
        }

    private var observableText2: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var text2: CharSequence = Constant.EMPTY
        set(value) {
            observableText2 = value.toString()
            field = value
        }

    private var observableText3: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var text3: CharSequence = Constant.EMPTY
        set(value) {
            observableText3 = value.toString()
            field = value
        }

    private var observableText4: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var text4: CharSequence = Constant.EMPTY
        set(value) {
            observableText4 = value.toString()
            field = value
        }

    private var observableText5: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var text5: CharSequence = Constant.EMPTY
        set(value) {
            observableText5 = value.toString()
            field = value
        }

    private var observableText6: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var text6: CharSequence = Constant.EMPTY
        set(value) {
            observableText6 = value.toString()
            field = value
        }

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfOtp,
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isEnabled == other.isEnabled
            && text1.contentEquals(other.text1)
            && text2.contentEquals(other.text2)
            && text3.contentEquals(other.text3)
            && text4.contentEquals(other.text4)
            && text5.contentEquals(other.text5)
            && text6.contentEquals(other.text6)

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }

    override fun reset() {
        text1 = Constant.EMPTY
        text2 = Constant.EMPTY
        text3 = Constant.EMPTY
        text4 = Constant.EMPTY
        text5 = Constant.EMPTY
        text6 = Constant.EMPTY
    }
}
