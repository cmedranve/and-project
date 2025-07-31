package pe.com.scotiabank.blpm.android.client.base.verification.business

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.data.entity.business.TokenEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository

class BusinessOtpVerificationModel(
    dispatcherProvider: DispatcherProvider,
    private val repository: BusinessOtpRepository,
    private val transactionId: String,
    private val transactionType: String,
    private val eventOnOtpVerified: Any,
) : DispatcherProvider by dispatcherProvider, SuspendingFunction<CharArray, Any> {

    override suspend fun apply(input: CharArray) = withContext(ioDispatcher) {
        val tokenEntity = TokenEntity(smartToken = String(input))
        val httpResponse: HttpResponse<*> = repository.validateOtp(transactionId, transactionType, tokenEntity)

        when (val responseEntity: Any? = httpResponse.body) {
            is Unit -> eventOnOtpVerified
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }
}
