package pe.com.scotiabank.blpm.android.ui.list.items.inputs.centeredcurrencyedittext

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.currencyedittext.CanvasCenteredCurrencyEditText
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCenteredCurrencyEditTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.UiBinderOfInputText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent
import pe.com.scotiabank.blpm.android.ui.util.bindTextIfNotBlank

object UiBinderOfCenteredCurrencyEditText {

    fun <D: Any> delegateBinding(carrier: UiEntityCarrier<UiEntityOfCenteredCurrencyEditText<D>, ViewCenteredCurrencyEditTextItemBinding>) {
        val entity: UiEntityOfCenteredCurrencyEditText<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfCenteredCurrencyEditText<D>, binding: ViewCenteredCurrencyEditTextItemBinding) {
        binding.root.gravity = entity.gravity
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindFormInputView(entity, binding.cccet)
        bindIfDifferent(entity.isUnderlineVisible, binding.cccet::isUnderlineVisible, binding.cccet::setUnderlineVisible)
        bindCallbacks(entity, binding.cccet)
        bindIfDifferent(entity.isEnabled, binding.cccet::isEnabled, binding.cccet::setEnabled)
    }

    @JvmStatic
    fun <D: Any> bindFormInputView(entity: UiEntityOfCenteredCurrencyEditText<D>, ccet: CanvasCenteredCurrencyEditText) {
        bindIfDifferent(entity.currencyText, ccet.getCurrencyField()::getText, ccet.getCurrencyField()::setText)
        UiBinderOfInputText.setTextWithoutTextWatcher(entity.text, ccet)
        bindTextIfNotBlank(entity::hintText, ccet::setHint)

        ccet.getCurrencyField().setTextAppearance(entity.appearanceForText)
        ccet.getInputField().setTextAppearance(entity.appearanceForText)
        bindTextSize(entity, ccet)
        bindTextHintColor(entity, ccet)
        UiBinderOfInputText.setFiltersIfDifferent(entity.filters, ccet.getInputField())
        bindIfDifferent(entity.inputType, ccet.getInputField()::getInputType, ccet::setInputType)
    }

    @JvmStatic
    fun <D: Any> bindCallbacks(entity: UiEntityOfCenteredCurrencyEditText<D>, ccet: CanvasCenteredCurrencyEditText) {
        val textSupplying: Supplier<CharSequence> = Supplier(ccet::getText)
        val inputHandlingAdapter = InputHandlingAdapter(entity, textSupplying)
        ccet.setTextClearedInputEventCallBack(inputHandlingAdapter)
        ccet.setTextEnteredInputEventCallBack(inputHandlingAdapter)
        ccet.setImeActionListener(inputHandlingAdapter)
        ccet.getInputField().setOnKeyboardCloseListener(inputHandlingAdapter)
    }

    @JvmStatic
    private fun <D: Any> bindTextSize(
        entity: UiEntityOfCenteredCurrencyEditText<D>,
        cet: CanvasCenteredCurrencyEditText,
    ) {
        val resources: Resources = cet.resources
        val textSize: Float = resources.getDimension(entity.textSizeId)

        bindInputText(cet, textSize)
        bindCurrencyText(cet, textSize)
    }

    @JvmStatic
    private fun bindInputText(
        cet: CanvasCenteredCurrencyEditText,
        textSize: Float,
    ) {
        if (cet.getInputField().textSize == textSize) return
        cet.getInputField().setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    }

    @JvmStatic
    private fun bindCurrencyText(
        cet: CanvasCenteredCurrencyEditText,
        textSize: Float,
    ) {
        if (cet.getCurrencyField().textSize == textSize) return
        cet.getCurrencyField().setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    }

    @JvmStatic
    private fun <D: Any> bindTextHintColor(
        entity: UiEntityOfCenteredCurrencyEditText<D>,
        ccet: CanvasCenteredCurrencyEditText,
    ) {
        @ColorInt val textColorHint: Int = ContextCompat.getColor(ccet.context, entity.textColorHintId)
        if (ccet.getInputField().currentHintTextColor == textColorHint) return

        ccet.getInputField().setHintTextColor(textColorHint)
    }
}
