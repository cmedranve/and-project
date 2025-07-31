package pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton

import android.view.Gravity
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.R
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

class UiEntityOfTextButton<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    isEnabled: Boolean,
    text: CharSequence = Constant.EMPTY,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    @StyleRes val appearanceForEnabledState: Int = R.style.canvascore_style_text_button,
    val gravity: Int = Gravity.START,
    @DrawableRes drawableStartId: Int = ResourcesCompat.ID_NULL,
    @DrawableRes drawableEndId: Int = ResourcesCompat.ID_NULL,
    @DimenRes val drawablePadding: Int = R.dimen.canvascore_margin_6,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfTextButton<D>>,
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

    private var drawableStartIdDelegate: Int by Delegates.observable(
        drawableStartId,
        ::onChangeOfNonEntityProperty
    )

    private var drawableEndIdDelegate: Int by Delegates.observable(
        drawableEndId,
        ::onChangeOfNonEntityProperty
    )

    var drawableStartId: Int
        @DrawableRes
        get() = drawableStartIdDelegate
        set(value) {
            drawableStartIdDelegate = value
            drawableEndIdDelegate = ResourcesCompat.ID_NULL
        }

    var drawableEndId: Int
        @DrawableRes
        get() = drawableEndIdDelegate
        set(value) {
            drawableStartIdDelegate = ResourcesCompat.ID_NULL
            drawableEndIdDelegate = value
        }

    override fun isHoldingTheSameContentAs(
        other:UiEntityOfTextButton<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isEnabled == other.isEnabled
            && text.contentEquals(other.text)
            && appearanceForEnabledState == other.appearanceForEnabledState
            && gravity == other.gravity
            && drawableStartId == other.drawableStartId
            && drawableEndId == other.drawableEndId
            && drawablePadding == other.drawablePadding
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
