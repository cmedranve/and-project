package pe.com.scotiabank.blpm.android.client.base.dialog

import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal

/**
 * @deprecated This class is deprecated because there are callbacks, replaced by {@link ModalDataHolder}
 */
class StaticDataCanvasDialogModal(
    val attrsCanvasDialogModal: AttrsCanvasDialogModal,
    val callbackOfPrimaryButton: Runnable? = null,
    val callbackOfSecondaryButton: Runnable? = null,
    val id: Any? = null,
)
