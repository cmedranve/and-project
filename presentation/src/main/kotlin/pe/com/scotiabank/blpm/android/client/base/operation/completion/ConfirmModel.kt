package pe.com.scotiabank.blpm.android.client.base.operation.completion

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.entity.transfer.ReferenceRequestEntity
import pe.com.scotiabank.blpm.android.data.exception.createExceptionOnIllegalResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException

class ConfirmModel<R: Any>(
    dispatcherProvider: DispatcherProvider,
    private val factoryOfReferenceRequest: FactoryOfReferenceRequest,
    private val confirmRepository: ConfirmRepository,
) : DispatcherProvider by dispatcherProvider {

    @Suppress("UNCHECKED_CAST")
    suspend fun confirm(
        authTracking: String,
        authToken: String,
        transactionId: String,
        description: String,
    ): R = withContext(ioDispatcher) {

        val requestEntity: ReferenceRequestEntity = factoryOfReferenceRequest.createEntity(description)
        val httpResponse: HttpResponse<*> = confirmRepository.confirmOperation(
            authTracking = authTracking,
            authToken = authToken,
            transactionId = transactionId,
            requestEntity = requestEntity,
        )

        when(val responseEntity: Any? = httpResponse.body) {
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> httpResponse.body as? R
                ?: throw createExceptionOnIllegalResponseBody(this::class)
        }
    }
}
