package pe.com.scotiabank.blpm.android.client.base.verification

import android.content.res.Resources
import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import java.lang.ref.WeakReference

class FactoryOfInspectedOtpModalDataHolder(
    dispatcherProvider: DispatcherProvider,
    private val weakResources: WeakReference<Resources?>,
    private val receiver: InstanceReceiver?,
) : DispatcherProvider by dispatcherProvider {

    suspend fun createBy(
        code: String,
        message: CharArray,
    ): ModalDataHolder = withContext(defaultDispatcher) {

        val attrs: AttrsCanvasDialogModal = createAttrsWith(message)

        ModalDataHolder(attrs, receiver, code)
    }

    private fun createAttrsWith(message: CharArray): AttrsCanvasDialogModal = AttrsCanvasDialogModal(
        title = weakResources.get()?.getString(R.string.credit_card_error_invalid_data).orEmpty(),
        textBody = String(message),
        primaryButtonLabel = weakResources.get()?.getString(R.string.understood).orEmpty(),
    )
}
