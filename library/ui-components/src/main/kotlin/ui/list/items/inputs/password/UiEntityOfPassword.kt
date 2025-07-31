package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

import android.text.InputFilter
import android.text.InputType
import androidx.annotation.DimenRes
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.UiEntityOfInputText
import pe.com.scotiabank.blpm.android.ui.list.items.isHoldingTheSameContentAs
import pe.com.scotiabank.blpm.android.ui.list.items.isNullableEntityTheSameAs
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfPassword<D: Any>(
    override val paddingEntity: UiEntityOfPadding,
    titleText: String = Constant.EMPTY,
    val isIconNeeded: Boolean = false,
    hintText: CharSequence = Constant.EMPTY,
    override val receiver: InstanceReceiver? = null,
    filters: Array<InputFilter> = emptyArray(),
    override val inputType: Int = InputType.TYPE_CLASS_TEXT,
    override val contentDescription: CharSequence = Constant.EMPTY,
    override val toolTipEntity: UiEntityOfToolTip? = null,
    @DimenRes val paddingTopForRequirement: Int = R.dimen.canvascore_margin_0,
    val requirementTitle: String = Constant.EMPTY,
    val requirementEntities: List<UiEntityOfRequirement> = emptyList(),
    override val data: D? = null,
    isEnabled: Boolean = true,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfPassword<D>>,
    UiEntityOfInputText<D>,
    ChangingState by changingState,
    Recycling by recycling
{

    override var titleText: String by Delegates.observable(
        titleText,
        ::onChangeOfNonEntityProperty
    )

    private var observableText: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    override var text: CharSequence = Constant.EMPTY
        set(value) {
            observableText = value.toString()
            field = value
        }

    private var observableHintText: String by Delegates.observable(
        hintText.toString(),
        ::onChangeOfNonEntityProperty
    )

    override var hintText: CharSequence = hintText
        set(value) {
            observableHintText = value.toString()
            field = value
        }

    private var observableSupplementaryText: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    override var supplementaryText: CharSequence = Constant.EMPTY
        set(value) {
            observableSupplementaryText = value.toString()
            field = value
        }

    private var observableErrorText: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    override var errorText: CharSequence = Constant.EMPTY
        set(value) {
            observableErrorText = value.toString()
            field = value
        }

    override var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    override var filters: Array<InputFilter> by Delegates.observable(
        filters,
        ::onChangeOfNonEntityProperty
    )

    val isAllDefault: Boolean
        get() = requirementEntities.all(::isDefault)

    val isAllSatisfied: Boolean
        get() = requirementEntities.all(::isSatisfied)

    val isAnyFailure: Boolean
        get() = requirementEntities.any(::isFailure)

    private fun isDefault(
        requirementEntity: UiEntityOfRequirement,
    ): Boolean = requirementEntity.isDefault

    private fun isSatisfied(
        requirementEntity: UiEntityOfRequirement,
    ): Boolean = requirementEntity.isSatisfied

    private fun isFailure(
        requirementEntity: UiEntityOfRequirement,
    ): Boolean = requirementEntity.isFailure

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfPassword<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && titleText.contentEquals(other.titleText)
            && isIconNeeded == other.isIconNeeded
            && hintText.contentEquals(other.hintText)
            && inputType == other.inputType
            && contentDescription.contentEquals(other.contentDescription)
            && toolTipEntity.isNullableEntityTheSameAs(other.toolTipEntity)
            && text.contentEquals(other.text)
            && supplementaryText.contentEquals(other.supplementaryText)
            && errorText.contentEquals(other.errorText)
            && paddingTopForRequirement == other.paddingTopForRequirement
            && requirementTitle.contentEquals(other.requirementTitle)
            && requirementEntities.isHoldingTheSameContentAs(other.requirementEntities)
            && isEnabled == other.isEnabled
            && filters.contentEquals(other.filters)
            && expectedFlexGrow == other.expectedFlexGrow

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }

    override fun reset() {
        text = Constant.EMPTY
        requirementEntities.forEach(::resetRequirementStatus)
    }

    private fun resetRequirementStatus(entity: UiEntityOfRequirement) {
        entity.reset()
    }
}
