package pe.com.scotiabank.blpm.android.ui.list.items.inputs.currencyedittext

import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.isNullableEntityTheSameAs
import pe.com.scotiabank.blpm.android.ui.list.items.UiEntityOfClearButton
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.UiEntityOfInputText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfCurrencyEditText<D: Any>(
    override val paddingEntity: UiEntityOfPadding,
    titleText: String = Constant.EMPTY,
    subtitleText: CharSequence = Constant.EMPTY,
    currencyText: CharSequence = Constant.EMPTY,
    hintText: CharSequence = Constant.EMPTY,
    override val receiver: InstanceReceiver? = null,
    filters: Array<InputFilter> = emptyArray(),
    override val inputType: Int = InputType.TYPE_CLASS_TEXT,
    override val contentDescription: CharSequence = Constant.EMPTY,
    val clearButtonEntity: UiEntityOfClearButton? = null,
    override val toolTipEntity: UiEntityOfToolTip? = null,
    override val data: D? = null,
    @DrawableRes drawableRightId: Int = ResourcesCompat.ID_NULL,
    isEnabled: Boolean = true,
    val gravity: Int = Gravity.START,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val isUnderlineVisible: Boolean = true,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfCurrencyEditText<D>>,
    UiEntityOfInputText<D>,
    ChangingState by changingState,
    Recycling by recycling
{

    override var titleText: String by Delegates.observable(
        titleText,
        ::onChangeOfNonEntityProperty
    )

    private var observableSubtitleText: String by Delegates.observable(
        subtitleText.toString(),
        ::onChangeOfNonEntityProperty
    )

    var subtitleText: CharSequence = subtitleText
        set(value) {
            observableSubtitleText = value.toString()
            field = value
        }

    private var observableCurrencyText: String by Delegates.observable(
        currencyText.toString(),
        ::onChangeOfNonEntityProperty
    )

    var currencyText: CharSequence = currencyText
        set(value) {
            observableCurrencyText = value.toString()
            field = value
        }

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

    var drawableRightId: Int by Delegates.observable(
        drawableRightId,
        ::onChangeOfNonEntityProperty
    )

    override var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    override var filters: Array<InputFilter> by Delegates.observable(
        filters,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfCurrencyEditText<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && currencyText.contentEquals(other.currencyText)
            && titleText.contentEquals(other.titleText)
            && subtitleText.contentEquals(other.subtitleText)
            && hintText.contentEquals(other.hintText)
            && inputType == other.inputType
            && contentDescription.contentEquals(other.contentDescription)
            && clearButtonEntity.isNullableEntityTheSameAs(other.clearButtonEntity)
            && toolTipEntity.isNullableEntityTheSameAs(other.toolTipEntity)
            && text.contentEquals(other.text)
            && supplementaryText.contentEquals(other.supplementaryText)
            && errorText.contentEquals(other.errorText)
            && drawableRightId == other.drawableRightId
            && isEnabled == other.isEnabled
            && filters.contentEquals(other.filters)
            && gravity == other.gravity
            && expectedFlexGrow == other.expectedFlexGrow
            && isUnderlineVisible ==other.isUnderlineVisible

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
        clearButtonEntity?.resetChangingState()
        toolTipEntity?.resetChangingState()
    }

    override fun reset() {
        text = Constant.EMPTY
        currencyText = Constant.EMPTY
        errorText = Constant.EMPTY
        hintText = Constant.EMPTY
    }
}
