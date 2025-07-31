package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

import com.scotiabank.canvascore.inputs.FormInputView.EventListener
import com.scotiabank.canvascore.utils.KeyboardUtils.OnKeyboardCloseListener
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEvent
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.InputEventCarrier

class InputHandlingAdapter<D: Any>(
    private val entity: UiEntityOfPassword<D>,
): EventListener, OnKeyboardCloseListener {

    private val receiver: InstanceReceiver?
        get() = entity.receiver

    override fun onEvent() {
        notify(InputEvent.IME_ACTION_PRESSED)
    }

    private fun notify(inputEvent: InputEvent) {
        val carrier: InputEventCarrier<D, UiEntityOfPassword<D>> = InputEventCarrier(
            event = inputEvent,
            entity = entity,
        )
        receiver?.receive(carrier)
    }

    override fun onKeyboardClose() {
        notify(InputEvent.BACK_KEY_PRESSED_WHEN_EDIT_TEXT_FOCUSED)
    }
}
