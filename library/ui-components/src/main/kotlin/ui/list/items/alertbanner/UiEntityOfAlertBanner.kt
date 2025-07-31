package pe.com.scotiabank.blpm.android.ui.list.items.alertbanner

import com.scotiabank.canvascore.views.AlertBannerType
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ImmutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant

class UiEntityOfAlertBanner<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    val textContent: CharSequence,
    val iconContentDescription: String,
    val type: AlertBannerType,
    val newTypeText: String = Constant.EMPTY,
    val supportLink: Boolean = false,
    val emphasisContent: CharSequence = Constant.EMPTY,
    val emphasisLink: Boolean = false,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = ImmutableState,
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfAlertBanner<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfAlertBanner<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && textContent.contentEquals(other.textContent)
            && iconContentDescription.contentEquals(other.iconContentDescription)
            && type == other.type
            && newTypeText.contentEquals(other.newTypeText)
            && supportLink == other.supportLink
            && emphasisContent.contentEquals(other.emphasisContent)
            && emphasisLink == other.emphasisLink
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
