package pe.com.scotiabank.blpm.android.client.base.verification

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.data.entity.nonsession.OtpVerifyingRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.OtpVerifyingResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.otp.OtpRepository

class OtpVerificationModel(
    dispatcherProvider: DispatcherProvider,
    private val repository: OtpRepository,
    private val eventOnOtpVerified: Any,
): DispatcherProvider by dispatcherProvider, SuspendingFunction<CharArray, Any> {

    override suspend fun apply(input: CharArray) = withContext(ioDispatcher) {

        val requestEntity = OtpVerifyingRequestEntity(input)
        val httpResponse: HttpResponse<*> = repository.verifyOtp(requestEntity)

        when (val responseEntity: Any? = httpResponse.body) {
            is OtpVerifyingResponseEntity -> eventOnOtpVerified
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }
}
