package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

import androidx.core.util.Supplier
import com.scotiabank.canvascore.inputs.PasswordInputView
import pe.com.scotiabank.blpm.android.ui.databinding.ViewEditPasswordItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.UiBinderOfInputText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.attemptBindToolTip
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent
import pe.com.scotiabank.blpm.android.ui.util.bindTextIfNotBlank

object UiBinderOfPassword {

    @JvmStatic
    fun <D: Any> delegateBinding(carrier: UiEntityCarrier<UiEntityOfPassword<D>, ViewEditPasswordItemBinding>) {
        val entity: UiEntityOfPassword<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(entity: UiEntityOfPassword<D>, binding: ViewEditPasswordItemBinding) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)

        bindFormInputView(entity, binding.piv)
        UiBinderOfInputText.bindErrorView(entity.errorText, binding.piv, binding.iev, binding.ctvError)
        UiBinderOfRequirement.bind(entity, binding.pRequirements)

        bindCallbacks(entity, binding.piv)
        attemptBindToolTip(entity.toolTipEntity, binding, binding.piv::setToolTip)
    }

    @JvmStatic
    fun <D: Any> bindFormInputView(entity: UiEntityOfPassword<D>, piv: PasswordInputView) {
        UiBinderOfInputText.setTextWithoutTextWatcher(entity.text, piv)
        piv.setTitle(entity.titleText, entity.isIconNeeded)
        bindTextIfNotBlank(entity::hintText, piv::setHint)
        bindTextIfNotBlank(entity::contentDescription, piv::setContentDescription)

        UiBinderOfInputText.setFiltersIfDifferent(entity.filters, piv.getInputField())
        bindIfDifferent(entity.inputType, piv.getInputField()::getInputType, piv::setInputType)
    }

    @JvmStatic
    fun <D: Any> bindCallbacks(entity: UiEntityOfPassword<D>, piv: PasswordInputView) {

        val textSupplying: Supplier<CharSequence> = Supplier(piv::getText)
        val textHandler: TextHandler<D> = TextHandler(entity, textSupplying)
        piv.setOnTextClearedCallback(textHandler)
        piv.setOnTextEnteredCallback(textHandler)

        val inputHandlingAdapter = InputHandlingAdapter(entity)
        piv.setImeActionListener(inputHandlingAdapter)
        piv.getInputField().setOnKeyboardCloseListener(inputHandlingAdapter)
    }
}
