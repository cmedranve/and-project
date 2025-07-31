package pe.com.scotiabank.blpm.android.client.base.operation.completion

import pe.com.scotiabank.blpm.android.data.entity.transfer.ReferenceRequestEntity
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse

fun interface ConfirmRepository {

    suspend fun confirmOperation(
        authTracking: String,
        authToken: String,
        transactionId: String,
        requestEntity: ReferenceRequestEntity,
    ): HttpResponse<*>
}
