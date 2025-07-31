package pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown

import com.scotiabank.canvascore.selectors.Dropdown
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.isHoldingTheSameContentAs
import pe.com.scotiabank.blpm.android.ui.list.items.isNullableEntityTheSameAs
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfDropdownSelector<D : Any>(
    val paddingEntity: UiEntityOfPadding,
    val controller: SelectionControllerOfDropdownSelector<D>,
    internal val itemEntities: List<UiEntityOfDropdownSelectorItem<D>>,
    val isFirstItemHint: Boolean,
    val titleText: CharSequence = Constant.EMPTY,
    val toolTipEntity: UiEntityOfToolTip? = null,
    val accessibilityLabelOfError: CharSequence = Constant.EMPTY,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfDropdownSelector<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    private var observableErrorText: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var errorText: CharSequence = Constant.EMPTY
        set(value) {
            observableErrorText = value.toString()
            field = value
        }

    internal val dropdownCallbacks: Dropdown.Callbacks
        get() = controller.dropdownCallbacks

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfDropdownSelector<D>
    ): Boolean = isUnmodified
            && itemEntities.isHoldingTheSameContentAs(other.itemEntities)
            && isFirstItemHint == other.isFirstItemHint
            && titleText.contentEquals(other.titleText)
            && toolTipEntity.isNullableEntityTheSameAs(other.toolTipEntity)
            && accessibilityLabelOfError.contentEquals(other.accessibilityLabelOfError)
            && errorText.contentEquals(other.errorText)
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        toolTipEntity?.resetChangingState()
    }

    override fun reset() {
        controller.reset()
        errorText = Constant.EMPTY
    }
}
