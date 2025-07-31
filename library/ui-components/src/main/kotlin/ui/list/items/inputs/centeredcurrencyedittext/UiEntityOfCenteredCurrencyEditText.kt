package pe.com.scotiabank.blpm.android.ui.list.items.inputs.centeredcurrencyedittext

import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.UiEntityOfInputText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfCenteredCurrencyEditText<D: Any>(
    override val paddingEntity: UiEntityOfPadding,
    currencyText: CharSequence = Constant.EMPTY,
    hintText: CharSequence = Constant.EMPTY,
    override val receiver: InstanceReceiver? = null,
    filters: Array<InputFilter> = emptyArray(),
    override val inputType: Int = InputType.TYPE_CLASS_TEXT,
    override val data: D? = null,
    @StyleRes val appearanceForText: Int = R.style.canvascore_style_headline_medium_black,
    @DimenRes val textSizeId: Int = R.dimen.canvascore_font_size_1000,
    @ColorRes val textColorHintId: Int = R.color.canvascore_gray_500,
    isEnabled: Boolean = true,
    val gravity: Int = Gravity.CENTER_HORIZONTAL,
    val expectedFlexGrow: Float = UiBinderOfWidthParam.NON_EXISTENT_FLEX_GROW,
    val isUnderlineVisible: Boolean = true,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfCenteredCurrencyEditText<D>>,
    UiEntityOfInputText<D>,
    ChangingState by changingState,
    Recycling by recycling
{

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

    override var isEnabled: Boolean by Delegates.observable(
        isEnabled,
        ::onChangeOfNonEntityProperty
    )

    override var filters: Array<InputFilter> by Delegates.observable(
        filters,
        ::onChangeOfNonEntityProperty
    )

    override var titleText: String = Constant.EMPTY
    override var supplementaryText: CharSequence = Constant.EMPTY
    override var errorText: CharSequence = Constant.EMPTY
    override val contentDescription: CharSequence = Constant.EMPTY
    override val toolTipEntity: UiEntityOfToolTip? = null

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfCenteredCurrencyEditText<D>
    ): Boolean = isUnmodified
            && paddingEntity.isHoldingTheSameContentAs(other.paddingEntity)
            && currencyText.contentEquals(other.currencyText)
            && inputType == other.inputType
            && text.contentEquals(other.text)
            && appearanceForText == other.appearanceForText
            && textSizeId == other.textSizeId
            && textColorHintId == other.textColorHintId
            && isEnabled == other.isEnabled
            && filters.contentEquals(other.filters)
            && gravity == other.gravity
            && expectedFlexGrow == other.expectedFlexGrow
            && isUnderlineVisible ==other.isUnderlineVisible

    override fun resetChangingState() {
        changingState.resetChangingState()
        paddingEntity.resetChangingState()
    }

    override fun reset() {
        text = Constant.EMPTY
        currencyText = Constant.EMPTY
        hintText = Constant.EMPTY
    }
}
