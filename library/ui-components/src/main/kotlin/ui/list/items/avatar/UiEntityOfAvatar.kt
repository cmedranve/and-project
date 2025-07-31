package pe.com.scotiabank.blpm.android.ui.list.items.avatar

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.views.Avatar.Companion.AvatarColor
import com.scotiabank.canvascore.views.Avatar.Companion.AvatarSize
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

class UiEntityOfAvatar(
    val paddingEntity: UiEntityOfPadding,
    name: String = Constant.EMPTY,
    val color: AvatarColor? = null,
    @DrawableRes drawableRes: Int = ResourcesCompat.ID_NULL,
    val size: AvatarSize,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfAvatar>,
    ChangingState by changingState,
    Recycling by recycling
{

    var name: String by Delegates.observable(
        name,
        ::onChangeOfNonEntityProperty
    )

    var drawableRes: Int by Delegates.observable(
        drawableRes,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfAvatar
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && name == other.name
            && color == other.color
            && drawableRes == other.drawableRes
            && size == other.size
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
