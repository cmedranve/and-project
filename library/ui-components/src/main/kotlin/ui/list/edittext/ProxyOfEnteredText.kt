package pe.com.scotiabank.blpm.android.ui.list.edittext

import com.scotiabank.canvascore.inputs.CanvasEditText

class ProxyOfEnteredText(
    private val callback: Runnable
): CanvasEditText.TextChangedInputEventCallBack {

    override fun onTextChanged() {
        callback.run()
    }
}
