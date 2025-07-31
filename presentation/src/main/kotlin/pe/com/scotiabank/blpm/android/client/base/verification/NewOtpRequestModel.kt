package pe.com.scotiabank.blpm.android.client.base.verification

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.entity.otp.NewOtpRequestRequestEntity
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.otp.OtpRepository

class NewOtpRequestModel(
    dispatcherProvider: DispatcherProvider,
    private val repository: OtpRepository,
    private val operation: CharArray,
) : DispatcherProvider by dispatcherProvider, SuspendingFunction<CharArray, Unit> {

    override suspend fun apply(input: CharArray) = withContext(ioDispatcher) {
        val requestEntity = NewOtpRequestRequestEntity(input, operation)
        val httpResponse: HttpResponse<*> = repository.requestNewOtp(requestEntity)

        when (val responseEntity: Any? = httpResponse.body) {
            is Unit -> Unit
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }
}
