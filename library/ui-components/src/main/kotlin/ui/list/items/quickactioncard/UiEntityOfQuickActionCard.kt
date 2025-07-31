package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.isNullableEntityTheSameAs
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.statusbadge.UiEntityOfStatusBadge
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfQuickActionCard<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    @DrawableRes iconRes: Int = ResourcesCompat.ID_NULL,
    description: CharSequence = Constant.EMPTY,
    @DimenRes val elevationRes: Int = com.scotiabank.canvascore.R.dimen.canvascore_card_flat_elevation,
    val borderStyleOfCard: BorderStyleOfCard? = null,
    val receiver: InstanceReceiver? = null,
    val data: D? = null,
    val chevronEntity: UiEntityOfChevron? = null,
    val statusBadgeEntity: UiEntityOfStatusBadge? = null,
    descriptionSecondary: CharSequence = Constant.EMPTY,
    placeholderSecondaryRes: Int = R.string.empty,
    showLoadingShimmer: Boolean = false,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfQuickActionCard<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var iconRes: Int by Delegates.observable(
        iconRes,
        ::onChangeOfNonEntityProperty
    )

    private var observableDescription: String by Delegates.observable(
        description.toString(),
        ::onChangeOfNonEntityProperty
    )

    var description: CharSequence = description
        set(value) {
            observableDescription = value.toString()
            field = value
        }

    private var observableDescriptionSecondary: String by Delegates.observable(
        descriptionSecondary.toString(),
        ::onChangeOfNonEntityProperty
    )

    var descriptionSecondary: CharSequence = descriptionSecondary
        set(value) {
            observableDescriptionSecondary = value.toString()
            field = value
        }

    private var observableInitialShimmerRes: String by Delegates.observable(
        placeholderSecondaryRes.toString(),
        ::onChangeOfNonEntityProperty
    )

    var placeholderSecondaryRes: Int = placeholderSecondaryRes
        set(value) {
            observableInitialShimmerRes = value.toString()
            field = value
        }

    var showLoadingShimmer: Boolean by Delegates.observable(
        showLoadingShimmer,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfQuickActionCard<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && iconRes == other.iconRes
            && description.contentEquals(other.description)
            && elevationRes == other.elevationRes
            && chevronEntity.isNullableEntityTheSameAs(other.chevronEntity)
            && statusBadgeEntity.isNullableEntityTheSameAs(other.statusBadgeEntity)
            && descriptionSecondary.contentEquals(other.descriptionSecondary)
            && placeholderSecondaryRes == other.placeholderSecondaryRes
            && showLoadingShimmer == other.showLoadingShimmer
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        chevronEntity?.resetChangingState()
        statusBadgeEntity?.resetChangingState()
    }
}
