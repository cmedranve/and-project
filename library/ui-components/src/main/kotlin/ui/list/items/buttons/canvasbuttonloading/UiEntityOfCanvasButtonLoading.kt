package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.buttons.CanvasButtonLoading
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

class UiEntityOfCanvasButtonLoading<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    isEnabled: Boolean,
    text: String = Constant.EMPTY,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    state: Int = CanvasButtonLoading.STATE_IDLE,
    @DrawableRes drawableEndId: Int = ResourcesCompat.ID_NULL,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfCanvasButtonLoading<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    var text: String by Delegates.observable(
        text,
        ::onChangeOfNonEntityProperty
    )

    var state: Int by Delegates.observable(
        state,
        ::onChangeOfNonEntityProperty
    )

    var drawableEndId: Int by Delegates.observable(
        drawableEndId,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other:UiEntityOfCanvasButtonLoading<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isEnabled == other.isEnabled
            && text.contentEquals(other.text)
            && state == other.state
            && drawableEndId == other.drawableEndId
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
