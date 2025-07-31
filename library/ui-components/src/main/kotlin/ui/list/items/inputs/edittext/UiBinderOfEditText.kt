package pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext

import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Supplier
import com.scotiabank.canvascore.inputs.CanvasEditText
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.databinding.ViewEditTextItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEvent
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEventCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.UiBinderOfInputText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.attemptBindToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent
import pe.com.scotiabank.blpm.android.ui.util.bindTextIfNotBlank

object UiBinderOfEditText {

    @JvmStatic
    fun <D: Any> delegateBinding(carrier: UiEntityCarrier<UiEntityOfEditText<D>, ViewEditTextItemBinding>) {
        val entity: UiEntityOfEditText<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfEditText<D>, binding: ViewEditTextItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(
            child = binding.root,
            expectedFlexGrow = UiBinderOfWidthParam.FLEX_GROW_AT_ONE,
        )
        bindFormInputView(entity, binding.cet)
        UiBinderOfInputText.bindErrorView(entity.errorText, binding.cet, binding.iev, binding.ctvError)
        entity.clearButtonEntity?.let { clearButtonEntity -> binding.cet.enableClearButton(clearButtonEntity.isEnabled) }
        UiBinderOfInputText.showSupplementaryIfNotBlank(entity.supplementaryText, binding.ctvSupplementary)
        bindCallbacks(entity, binding.cet)
        attemptBindToolTip(entity.toolTipEntity, binding, binding.cet::setToolTip)
        bindRightDrawable(entity, binding.cet)
        bindIfDifferent(entity.isEnabled, binding.cet::isEnabled, binding.cet::setEnabled)
    }

    @JvmStatic
    fun <D: Any> bindFormInputView(entity: UiEntityOfEditText<D>, cet: CanvasEditText) {
        UiBinderOfInputText.setTextWithoutTextWatcher(entity.text, cet)
        bindTextIfNotBlank(entity::titleText, cet::setTitle)
        setSubtitleIfDifferent(entity, cet)
        bindTextIfNotBlank(entity::hintText, cet::setHint)
        bindTextIfNotBlank(entity::contentDescription, cet::setContentDescription)

        UiBinderOfInputText.setFiltersIfDifferent(entity.filters, cet.getInputField())
        bindIfDifferent(entity.inputType, cet.getInputField()::getInputType, cet::setInputType)
    }

    @JvmStatic
    private fun <D: Any> setSubtitleIfDifferent(
        entity: UiEntityOfEditText<D>,
        cet: CanvasEditText
    ) {
        val subtitleText: CharSequence = entity.subtitleText
        if (subtitleText.contentEquals(cet.getSubTitleTextView().text)) return

        cet.setSubtitle(subtitleText)
    }

    @JvmStatic
    fun <D: Any> bindCallbacks(entity: UiEntityOfEditText<D>, cet: CanvasEditText) {
        val textSupplying: Supplier<CharSequence> = Supplier(cet::getText)
        val inputHandlingAdapter = InputHandlingAdapter(entity, textSupplying)
        cet.setTextClearedInputEventCallBack(inputHandlingAdapter)
        cet.setTextEnteredInputEventCallBack(inputHandlingAdapter)
        cet.setImeActionListener(inputHandlingAdapter)
        cet.getInputField().setOnKeyboardCloseListener(inputHandlingAdapter)
    }

    @JvmStatic
    fun <D: Any> bindRightDrawable(entity: UiEntityOfEditText<D>, cet: CanvasEditText) {
        if (ResourcesCompat.ID_NULL == entity.drawableRightId) return
        cet.setRightDrawableSrc(entity.drawableRightId)

        val receiver: InstanceReceiver = entity.receiver ?: return
        cet.setRightDrawableOnClickListener {
            val carrier: InputEventCarrier<D, UiEntityOfEditText<D>> = InputEventCarrier(
                event = InputEvent.DRAWABLE_RIGHT_CLICKED,
                entity = entity,
            )
            receiver.receive(carrier)
        }
    }
}
