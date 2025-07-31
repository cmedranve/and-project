package pe.com.scotiabank.blpm.android.client.base.dialog

import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong

class ModalDataHolder(
    val attrs: AttrsCanvasDialogModal,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    val id: Long = randomLong(),
    val buttonDirectionColumn: Boolean = false,
    val isDialogDismissible: Boolean = true,
)
