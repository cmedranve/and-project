package pe.com.scotiabank.blpm.android.ui.list.items.buddytip

import android.text.SpannableStringBuilder
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfBuddyTip(
    val paddingEntity: UiEntityOfPadding,
    @DrawableRes iconRes: Int = ResourcesCompat.ID_NULL,
    descriptionBuilder: SpannableStringBuilder = SpannableStringBuilder.valueOf(Constant.EMPTY),
    expandedDescriptionBuilder: SpannableStringBuilder = SpannableStringBuilder.valueOf(Constant.EMPTY),
    accessibilityActionLabel: String = Constant.EMPTY,
    accessibilityDescription: String = Constant.EMPTY,
    expandedAccessibilityDescription: String = Constant.EMPTY,
    closeButtonLabel: String = Constant.EMPTY,
    showLoadingShimmer: Boolean = false,
    type: BuddyTipType = BuddyTipType.NON_EXPANDABLE,
    isBackgroundEmpty: Boolean = false,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfBuddyTip>,
    ChangingState by changingState,
    Recycling by recycling
{

    var iconRes: Int by Delegates.observable(
        iconRes,
        ::onChangeOfNonEntityProperty
    )

    var descriptionBuilder: SpannableStringBuilder by Delegates.observable(
        descriptionBuilder,
        ::onChangeOfNonEntityProperty
    )

    var expandedDescriptionBuilder: SpannableStringBuilder by Delegates.observable(
        expandedDescriptionBuilder,
        ::onChangeOfNonEntityProperty
    )

    var accessibilityActionLabel: String by Delegates.observable(
        accessibilityActionLabel,
        ::onChangeOfNonEntityProperty
    )

    var accessibilityDescription: String by Delegates.observable(
        accessibilityDescription,
        ::onChangeOfNonEntityProperty
    )

    var expandedAccessibilityDescription: String by Delegates.observable(
        expandedAccessibilityDescription,
        ::onChangeOfNonEntityProperty
    )

    var closeButtonLabel: String by Delegates.observable(
        closeButtonLabel,
        ::onChangeOfNonEntityProperty
    )

    var showLoadingShimmer: Boolean by Delegates.observable(
        showLoadingShimmer,
        ::onChangeOfNonEntityProperty
    )

    var type: BuddyTipType by Delegates.observable(
        type,
        ::onChangeOfNonEntityProperty
    )

    var isBackgroundEmpty: Boolean by Delegates.observable(
        isBackgroundEmpty,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfBuddyTip
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && iconRes == other.iconRes
            && descriptionBuilder.contentEquals(other.descriptionBuilder)
            && expandedDescriptionBuilder.contentEquals(other.expandedDescriptionBuilder)
            && accessibilityActionLabel.contentEquals(other.accessibilityActionLabel)
            && accessibilityDescription.contentEquals(other.accessibilityDescription)
            && expandedAccessibilityDescription.contentEquals(other.expandedAccessibilityDescription)
            && closeButtonLabel.contentEquals(other.closeButtonLabel)
            && showLoadingShimmer == other.showLoadingShimmer
            && type == other.type
            && isBackgroundEmpty == other.isBackgroundEmpty
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }
}
