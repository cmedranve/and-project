package pe.com.scotiabank.blpm.android.client.host.session.subflow

import android.content.res.Resources
import androidx.annotation.StringRes
import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import java.lang.ref.WeakReference

class FactoryOfModalDataHolder(
    dispatcherProvider: DispatcherProvider,
    private val weakResources: WeakReference<Resources?>,
    private val receiver: InstanceReceiver,
) : DispatcherProvider by dispatcherProvider {

    suspend fun create(
        @StringRes textResId: Int,
        launcher: SubFlowLauncher,
    ): ModalDataHolder = withContext(defaultDispatcher) {

        val textBody: String = weakResources.get()?.getString(textResId, launcher.shortcutName).orEmpty()
        val attrs = createAttrsFrom(textBody)
        ModalDataHolder(attrs = attrs, receiver = receiver, data = launcher, buttonDirectionColumn = true)
    }

    private fun createAttrsFrom(textBody: String): AttrsCanvasDialogModal = AttrsCanvasDialogModal(
        title = weakResources.get()?.getString(R.string.warning).orEmpty(),
        textBody = textBody,
        primaryButtonLabel = weakResources.get()?.getString(R.string.yes_continue).orEmpty(),
        secondaryButtonLabel = weakResources.get()?.getString(R.string.no).orEmpty(),
    )
}
