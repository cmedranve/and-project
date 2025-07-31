package pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips

import androidx.core.util.Consumer
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.isNullableEntityTheSameAs
import pe.com.scotiabank.blpm.android.ui.list.items.horizontalscrollview.HostUiEntityOfScrollOffset
import pe.com.scotiabank.blpm.android.ui.list.items.UiEntityOfError
import pe.com.scotiabank.blpm.android.ui.list.items.isHoldingTheSameContentAs
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling

sealed class UiEntityOfChipsComponent<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    protected val controller: SelectionControllerOfChipsComponent<D>,
    protected val _chipEntitiesByChipText: LinkedHashMap<String, UiEntityOfChip<D>>,
    val isSelectionRequired: Boolean,
    val title: String?,
    val subtitle: CharSequence?,
    val errorEntity: UiEntityOfError?,
    val toolTipEntity: UiEntityOfToolTip?
) {

    internal val chipEntitiesByChipText: Map<String, UiEntityOfChip<D>>
        get() = _chipEntitiesByChipText

    internal val onChipClicked: Consumer<String?>
        get() = controller.onChipClicked

    protected fun isTheSameSubtitleAs(other: CharSequence?): Boolean = when {
        subtitle == null && other == null -> true
        else -> subtitle.contentEquals(other)
    }
}

class UiEntityOfDynamicChipsComponent<D: Any>(
    paddingEntity: UiEntityOfPadding,
    controller: SelectionControllerOfChipsComponent<D>,
    _chipEntitiesByChipText: LinkedHashMap<String, UiEntityOfChip<D>>,
    isSelectionRequired: Boolean,
    title: String? = null,
    subtitle: CharSequence? = null,
    errorEntity: UiEntityOfError? = null,
    toolTipEntity: UiEntityOfToolTip? = null,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): UiEntityOfChipsComponent<D>(
    paddingEntity,
    controller,
    _chipEntitiesByChipText,
    isSelectionRequired,
    title,
    subtitle,
    errorEntity,
    toolTipEntity
), IdentifiableUiEntity<UiEntityOfDynamicChipsComponent<D>>,
    HostUiEntityOfScrollOffset,
    ChangingState by changingState,
    Recycling by recycling
{

    private var _isReset: Boolean = false
        get() {
            val copy: Boolean = field
            field = false
            return copy
        }
    override val isReset: Boolean
        get() = _isReset

    override var scrollOffsetFromStart: Int? = null
        get() {
            val copy: Int? = field
            field = null
            return copy
        }

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfDynamicChipsComponent<D>
    ): Boolean = isUnmodified
            && _chipEntitiesByChipText.isHoldingTheSameContentAs(other._chipEntitiesByChipText)
            && isSelectionRequired == other.isSelectionRequired
            && title.equals(other.title)
            && isTheSameSubtitleAs(other.subtitle)
            && errorEntity.isNullableEntityTheSameAs(other.errorEntity)
            && toolTipEntity.isNullableEntityTheSameAs(other.toolTipEntity)
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        errorEntity?.resetChangingState()
        toolTipEntity?.resetChangingState()
    }

    override fun reset() {
        _isReset = true
        scrollOffsetFromStart = DEFAULT_SCROLL_OFFSET_ON_RESET
        controller.reset()
    }

    companion object {

        private const val DEFAULT_SCROLL_OFFSET_ON_RESET = 0
    }
}

class UiEntityOfStaticChipsComponent<D: Any>(
    paddingEntity: UiEntityOfPadding,
    controller: SelectionControllerOfChipsComponent<D>,
    _chipEntitiesByChipText: LinkedHashMap<String, UiEntityOfChip<D>>,
    isSelectionRequired: Boolean,
    title: String? = null,
    subtitle: CharSequence? = null,
    errorEntity: UiEntityOfError? = null,
    toolTipEntity: UiEntityOfToolTip? = null,
    val isGoingToHideChipMargins: Boolean = false,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): UiEntityOfChipsComponent<D>(
    paddingEntity,
    controller,
    _chipEntitiesByChipText,
    isSelectionRequired,
    title,
    subtitle,
    errorEntity,
    toolTipEntity
), IdentifiableUiEntity<UiEntityOfStaticChipsComponent<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfStaticChipsComponent<D>
    ): Boolean = isUnmodified
            && _chipEntitiesByChipText.isHoldingTheSameContentAs(other._chipEntitiesByChipText)
            && isSelectionRequired == other.isSelectionRequired
            && title.equals(other.title)
            && isTheSameSubtitleAs(other.subtitle)
            && errorEntity.isNullableEntityTheSameAs(other.errorEntity)
            && toolTipEntity.isNullableEntityTheSameAs(other.toolTipEntity)
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        errorEntity?.resetChangingState()
        toolTipEntity?.resetChangingState()
    }

    override fun reset() {
        controller.reset()
    }
}
