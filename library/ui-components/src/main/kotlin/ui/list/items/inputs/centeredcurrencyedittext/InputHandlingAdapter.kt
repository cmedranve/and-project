package pe.com.scotiabank.blpm.android.ui.list.items.inputs.centeredcurrencyedittext

import androidx.core.util.Supplier
import com.scotiabank.canvascore.inputs.FormInputView.EventListener
import com.scotiabank.canvascore.utils.KeyboardUtils.OnKeyboardCloseListener
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.currencyedittext.CanvasCenteredCurrencyEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEvent
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEventCarrier

class InputHandlingAdapter<D: Any>(
    private val entity: UiEntityOfCenteredCurrencyEditText<D>,
    private val textSupplying: Supplier<CharSequence>,
): CanvasCenteredCurrencyEditText.TextChangedInputEventCallBack, EventListener, OnKeyboardCloseListener {

    private val receiver: InstanceReceiver?
        get() = entity.receiver

    override fun onTextChanged() {
        entity.text = textSupplying.get()
        receiver?.receive(entity)
    }

    override fun onEvent() {
        notify(InputEvent.IME_ACTION_PRESSED)
    }

    private fun notify(inputEvent: InputEvent) {
        val carrier: InputEventCarrier<D, UiEntityOfCenteredCurrencyEditText<D>> = InputEventCarrier(
            event = inputEvent,
            entity = entity,
        )
        receiver?.receive(carrier)
    }

    override fun onKeyboardClose() {
        notify(InputEvent.BACK_KEY_PRESSED_WHEN_EDIT_TEXT_FOCUSED)
    }
}
