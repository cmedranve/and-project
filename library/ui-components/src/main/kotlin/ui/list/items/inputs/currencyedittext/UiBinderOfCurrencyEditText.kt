package pe.com.scotiabank.blpm.android.ui.list.items.inputs.currencyedittext

import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.currencyedittext.CanvasCurrencyEditText
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCurrencyEditTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEvent
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.UiBinderOfInputText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.attemptBindToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent
import pe.com.scotiabank.blpm.android.ui.util.bindTextIfNotBlank

object UiBinderOfCurrencyEditText {

    @JvmStatic
    fun <D: Any> delegateBinding(carrier: UiEntityCarrier<UiEntityOfCurrencyEditText<D>, ViewCurrencyEditTextItemBinding>) {
        val entity: UiEntityOfCurrencyEditText<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfCurrencyEditText<D>, binding: ViewCurrencyEditTextItemBinding) {
        binding.root.gravity = entity.gravity
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindFormInputView(entity, binding.ccet)
        UiBinderOfInputText.bindErrorView(entity.errorText, binding.ccet, binding.iev, binding.ctvError)
        binding.ccet.tintUnderlineWithErrorColor(entity.errorText)
        bindIfDifferent(entity.isUnderlineVisible, binding.ccet::isUnderlineVisible, binding.ccet::setUnderlineVisible)
        entity.clearButtonEntity?.let { clearButtonEntity -> binding.ccet.enableClearButton(clearButtonEntity.isEnabled) }
        UiBinderOfInputText.showSupplementaryIfNotBlank(entity.supplementaryText, binding.ctvSupplementary)
        bindCallbacks(entity, binding.ccet)
        attemptBindToolTip(entity.toolTipEntity, binding, binding.ccet::setToolTip)
        bindRightDrawable(entity, binding.ccet)
        bindIfDifferent(entity.isEnabled, binding.ccet::isEnabled, binding.ccet::setEnabled)
    }

    @JvmStatic
    fun <D: Any> bindFormInputView(entity: UiEntityOfCurrencyEditText<D>, ccet: CanvasCurrencyEditText) {
        bindIfDifferent(entity.currencyText, ccet.getCurrencyField()::getText, ccet.getCurrencyField()::setText)
        UiBinderOfInputText.setTextWithoutTextWatcher(entity.text, ccet)
        bindTextIfNotBlank(entity::titleText, ccet::setTitle)
        setSubtitleIfDifferent(entity, ccet)
        bindTextIfNotBlank(entity::hintText, ccet::setHint)
        bindTextIfNotBlank(entity::contentDescription, ccet::setContentDescription)

        UiBinderOfInputText.setFiltersIfDifferent(entity.filters, ccet.getInputField())
        bindIfDifferent(entity.inputType, ccet.getInputField()::getInputType, ccet::setInputType)
    }

    @JvmStatic
    private fun <D: Any> setSubtitleIfDifferent(
        entity: UiEntityOfCurrencyEditText<D>,
        ccet: CanvasCurrencyEditText
    ) {
        val subtitleText: CharSequence = entity.subtitleText
        if (subtitleText.contentEquals(ccet.getSubTitleTextView().text)) return

        ccet.setSubtitle(subtitleText)
    }

    @JvmStatic
    fun <D: Any> bindCallbacks(entity: UiEntityOfCurrencyEditText<D>, ccet: CanvasCurrencyEditText) {
        val textSupplying: Supplier<CharSequence> = Supplier(ccet::getText)
        val inputHandlingAdapter = InputHandlingAdapter(entity, textSupplying)
        ccet.setTextClearedInputEventCallBack(inputHandlingAdapter)
        ccet.setTextEnteredInputEventCallBack(inputHandlingAdapter)
        ccet.setImeActionListener(inputHandlingAdapter)
        ccet.getInputField().setOnKeyboardCloseListener(inputHandlingAdapter)
    }

    @JvmStatic
    fun <D: Any> bindRightDrawable(entity: UiEntityOfCurrencyEditText<D>, ccet: CanvasCurrencyEditText) {
        if (ResourcesCompat.ID_NULL == entity.drawableRightId) return
        ccet.setRightDrawableSrc(entity.drawableRightId)

        val receiver: InstanceReceiver = entity.receiver ?: return
        ccet.setRightDrawableOnClickListener {
            val carrier: InputEventCarrier<D, UiEntityOfCurrencyEditText<D>> = InputEventCarrier(
                event = InputEvent.DRAWABLE_RIGHT_CLICKED,
                entity = entity,
            )
            receiver.receive(carrier)
        }
    }
}
