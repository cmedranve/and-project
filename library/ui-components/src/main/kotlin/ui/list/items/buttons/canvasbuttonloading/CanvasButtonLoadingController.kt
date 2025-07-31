package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading
import com.scotiabank.canvascore.buttons.CanvasButtonLoading

interface CanvasButtonLoadingController {

    fun addForCanvasButtonLoading(
        id: Long,
        isEnabled: Boolean,
        text: String,
        data: Any? = null,
        state: Int = CanvasButtonLoading.STATE_IDLE,
    )

    fun editForCanvasButtonLoading(
        id: Long,
        isEnabled: Boolean,
        text: String,
        state: Int,
    )

    fun removeForCanvasButtonLoading(id: Long)
}
