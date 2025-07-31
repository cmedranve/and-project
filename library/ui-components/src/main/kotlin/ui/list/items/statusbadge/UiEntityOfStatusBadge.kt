package pe.com.scotiabank.blpm.android.ui.list.items.statusbadge

import android.view.Gravity
import com.scotiabank.canvascore.views.StatusBadge.Companion.StatusBadgeType
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfStatusBadge(
    val paddingEntity: UiEntityOfPadding,
    type: StatusBadgeType,
    text: String,
    gravity: Int = Gravity.START,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfStatusBadge>,
    ChangingState by changingState,
    Recycling by recycling
{

    var type: StatusBadgeType by Delegates.observable(
        type,
        ::onChangeOfNonEntityProperty
    )

    var text: String by Delegates.observable(
        text,
        ::onChangeOfNonEntityProperty
    )

    var gravity: Int by Delegates.observable(
        gravity,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfStatusBadge
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && type == other.type
            && text.contentEquals(other.text)
            && gravity == other.gravity
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
