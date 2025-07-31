package pe.com.scotiabank.blpm.android.client.base.operation.token.validation

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.SuspendingFunction
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.SmartKeyJoyRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.otp.SmartKeyRepository

class SmartKeyModel(
    dispatcherProvider: DispatcherProvider,
    private val repository: SmartKeyRepository,
): DispatcherProvider by dispatcherProvider, SuspendingFunction<CharArray, Unit> {

    override suspend fun apply(input: CharArray): Unit = withContext(ioDispatcher) {

        val optionForService: String = String(input)
            .takeIf(Constant.AUTH_TRACKING_EMAIL::contentEquals)
            ?: Constant.PHONE_OTP

        val requestEntity = SmartKeyJoyRequestEntity(optionForService)
        val httpResponse: HttpResponse<*> = repository.requestNewOtp(requestEntity)

        when (val responseEntity: Any? = httpResponse.body) {
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            is Any -> Unit
            else -> throw IllegalStateException("Unreachable code")
        }
    }
}
