package pe.com.scotiabank.blpm.android.ui.list.items.buttons.quickactionbutton

import android.view.Gravity
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
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

class UiEntityOfQuickActionButton<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    isEnabled: Boolean,
    @DrawableRes iconId: Int,
    val gravity: Int = Gravity.START,
    title: String = Constant.EMPTY,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    @DimenRes titleSizeId: Int = R.dimen.canvascore_font_14,
    @ColorRes titleColorId: Int = R.color.canvascore_qab_text,
    val isWhiteBorderNeeded: Boolean = false,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfQuickActionButton<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    var iconId: Int by Delegates.observable(
        iconId,
        ::onChangeOfNonEntityProperty
    )

    var title: String by Delegates.observable(
        title,
        ::onChangeOfNonEntityProperty
    )

    var titleSizeId: Int by Delegates.observable(
        titleSizeId,
        ::onChangeOfNonEntityProperty
    )

    var titleColorId: Int by Delegates.observable(
        titleColorId,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other:UiEntityOfQuickActionButton<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isEnabled == other.isEnabled
            && iconId == other.iconId
            && gravity == other.gravity
            && title.contentEquals(other.title)
            && titleSizeId == other.titleSizeId
            && titleColorId == other.titleColorId
            && isWhiteBorderNeeded == other.isWhiteBorderNeeded
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
