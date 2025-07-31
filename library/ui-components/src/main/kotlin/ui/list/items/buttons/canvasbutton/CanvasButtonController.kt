package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton

import com.scotiabank.canvascore.buttons.CanvasButton

interface CanvasButtonController {

    fun editCanvasButtonEnabling(
        id: Long,
        isEnabled: Boolean,
    )

    fun editCanvasButtonText(
        id: Long,
        text: CharSequence,
    )

    fun editCanvasButtonType(
        id: Long,
        type: Int = CanvasButton.PRIMARY,
    )

    fun removeCanvasButton(id: Long)
}
