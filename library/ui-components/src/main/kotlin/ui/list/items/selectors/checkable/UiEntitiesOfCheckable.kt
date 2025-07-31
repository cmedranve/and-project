package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable

import com.google.android.gms.common.util.BiConsumer
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.Identifiable
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.Resettable
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.BIAS_AT_ZERO
import kotlin.properties.Delegates

sealed class UiEntityOfCheckable<D: Any>(
    val paddingEntity: UiEntityOfPadding,
    private val controller: ControllerOfSelection<D>,
    val data: D?,
    override val id: Long,
) : Identifiable, Resettable {

    internal abstract var mutableIsChecked: Boolean
    val isChecked: Boolean
        get() = mutableIsChecked

    internal val onCheckedChange: BiConsumer<UiEntityOfCheckable<D>, Boolean>
        get() = controller.onCheckedChange
}

class UiEntityOfCheckableButton<D: Any>(
    paddingEntity: UiEntityOfPadding,
    val verticalBiasOfCheckableIcon: Float = BIAS_AT_ZERO,
    val paddingEntityOfCheckableIcon: UiEntityOfPadding,
    val sideRecyclerEntity: UiEntityOfRecycler,
    val bottomRecyclerEntity: UiEntityOfRecycler,
    controller: ControllerOfSelection<D>,
    data: D? = null,
    isEnabled: Boolean = true,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : UiEntityOfCheckable<D>(
    paddingEntity,
    controller,
    data,
    id,
), IdentifiableUiEntity<UiEntityOfCheckableButton<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    override var mutableIsChecked: Boolean by Delegates.observable(
        false,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfCheckableButton<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && verticalBiasOfCheckableIcon == other.verticalBiasOfCheckableIcon
            && paddingEntityOfCheckableIcon.isHoldingTheSameContentAs(other.paddingEntityOfCheckableIcon)
            && isEnabled == other.isEnabled
            && isChecked == other.isChecked
            && expectedFlexGrow == other.expectedFlexGrow
            && sideRecyclerEntity.isHoldingTheSameContentAs(other.sideRecyclerEntity)
            && bottomRecyclerEntity.isHoldingTheSameContentAs(other.bottomRecyclerEntity)

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        paddingEntityOfCheckableIcon.resetChangingState()
    }

    override fun reset() {
        sideRecyclerEntity.reset()
        bottomRecyclerEntity.reset()
    }
}

class UiEntityOfButtonLessCard<D: Any>(
    paddingEntity: UiEntityOfPadding,
    val recyclerEntity: UiEntityOfRecycler,
    controller: ControllerOfSelection<D>,
    data: D? = null,
    isEnabled: Boolean = true,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : UiEntityOfCheckable<D>(
    paddingEntity,
    controller,
    data,
    id,
), IdentifiableUiEntity<UiEntityOfButtonLessCard<D>>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    override var mutableIsChecked: Boolean by Delegates.observable(
        false,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfButtonLessCard<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && isEnabled == other.isEnabled
            && isChecked == other.isChecked
            && expectedFlexGrow == other.expectedFlexGrow
            && recyclerEntity.isHoldingTheSameContentAs(other.recyclerEntity)

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }

    override fun reset() {
        recyclerEntity.reset()
    }
}
