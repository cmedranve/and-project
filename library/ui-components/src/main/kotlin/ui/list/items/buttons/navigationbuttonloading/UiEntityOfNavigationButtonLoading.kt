package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbuttonloading

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.buttons.NavigationButtonLoading
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfNavigationButtonLoading<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    isEnabled: Boolean,
    @StringRes textResId: Int = ResourcesCompat.ID_NULL,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    state: Int = NavigationButtonLoading.STATE_IDLE,
    @DrawableRes errorDrawableId: Int = ResourcesCompat.ID_NULL,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfNavigationButtonLoading<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    var textResId: Int by Delegates.observable(
        textResId,
        ::onChangeOfNonEntityProperty
    )

    var state: Int by Delegates.observable(
        state,
        ::onChangeOfNonEntityProperty
    )

    var errorDrawableId: Int by Delegates.observable(
        errorDrawableId,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other:UiEntityOfNavigationButtonLoading<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isEnabled == other.isEnabled
            && textResId == other.textResId
            && state == other.state
            && errorDrawableId == other.errorDrawableId
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
