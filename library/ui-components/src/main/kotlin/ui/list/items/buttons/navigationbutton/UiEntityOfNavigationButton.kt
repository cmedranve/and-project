package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton

import android.widget.TextView.BufferType
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

class UiEntityOfNavigationButton<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    isEnabled: Boolean,
    text: CharSequence = Constant.EMPTY,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    @DrawableRes drawableId: Int = ResourcesCompat.ID_NULL,
    bufferTypeForText: BufferType? = null,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfNavigationButton<D>>,
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

    var drawableId: Int by Delegates.observable(
        drawableId,
        ::onChangeOfNonEntityProperty
    )

    var bufferTypeForText: BufferType? by Delegates.observable(
        bufferTypeForText,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other:UiEntityOfNavigationButton<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isEnabled == other.isEnabled
            && text.contentEquals(other.text)
            && drawableId == other.drawableId
            && bufferTypeForText == other.bufferTypeForText
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
