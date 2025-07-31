package pe.com.scotiabank.blpm.android.client.base.operation.completion

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.canvascore.dialog.model.AttrsCanvasDialogModal
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.dialog.ModalDataHolder
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class FactoryOfModalDataHolder(
    dispatcherProvider: DispatcherProvider,
    private val receiver: InstanceReceiver,
    private val weakResources: WeakReference<Resources?>,
) : DispatcherProvider by dispatcherProvider  {

    private val holdersById: MutableMap<Long, ModalDataHolder> = ConcurrentHashMap()

    suspend fun createBy(modalData: ModalData): ModalDataHolder = withContext(defaultDispatcher) {
        val dataHolder: ModalDataHolder = holdersById.getOrPut(modalData.id) {
            createModalDataHolder(modalData)
        }

        dataHolder
    }

    suspend fun createBy(title: String, message: String): ModalDataHolder = withContext(defaultDispatcher) {
        createModalDataHolder(title, message)
    }

    suspend fun createBy(throwable: HttpResponseException): ModalDataHolder = withContext(defaultDispatcher) {
        val title = throwable.body?.title?.let(::String).orEmpty()
        val message: String = throwable.message.orEmpty()
        createModalDataHolder(title, message)
    }

    private fun createModalDataHolder(title: String, message: String): ModalDataHolder {
        val attrs: AttrsCanvasDialogModal = createAttrsFrom(title, message)

        return ModalDataHolder(
            attrs = attrs,
            id = randomLong(),
            receiver = receiver
        )
    }

    private fun createAttrsFrom(title: String, message: String): AttrsCanvasDialogModal = AttrsCanvasDialogModal(
        title = title,
        textBody = message,
        primaryButtonLabel = weakResources.get()?.getString(R.string.understood).orEmpty(),
    )

    private fun createModalDataHolder(modalData: ModalData): ModalDataHolder {
        val attrs: AttrsCanvasDialogModal = createAttrsWith(modalData)

        return ModalDataHolder(
            attrs = attrs,
            receiver = receiver,
            data = modalData,
            id = modalData.id,
            buttonDirectionColumn = modalData.buttonDirectionColumn,
        )
    }

    private fun createAttrsWith(data: ModalData): AttrsCanvasDialogModal = AttrsCanvasDialogModal(
        title = toTextOrEmpty(data.titleRes),
        textBody = toTextOrEmpty(data.textBodyRes),
        primaryButtonLabel = toTextOrEmpty(data.primaryButtonLabel),
        secondaryButtonLabel =  toTextOrEmpty(data.secondaryButtonLabel),
    )

    private fun toTextOrEmpty(@StringRes textResId: Int): String {
        if (ResourcesCompat.ID_NULL == textResId) return String.EMPTY
        return weakResources.get()?.getString(textResId).orEmpty()
    }
}
