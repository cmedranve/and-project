package pe.com.scotiabank.blpm.android.client.base.errorstate

import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException

object ErrorHelper {

    fun getErrorCode(throwable: Throwable): String? {
        val exception: HttpResponseException = throwable as? HttpResponseException ?: return null
        val body: PeruErrorResponseBody? = exception.body
        return body?.code?.let(::String)
    }

    fun getErrorTitle(throwable: Throwable): String? {
        val exception: HttpResponseException = throwable as? HttpResponseException ?: return null
        val body: PeruErrorResponseBody? = exception.body
        return body?.title?.let(::String)
    }

    fun getErrorMessage(throwable: Throwable): String? {
        val exception: HttpResponseException = throwable as? HttpResponseException ?: return null
        val body: PeruErrorResponseBody? = exception.body
        return body?.message?.let(::String)
    }
}
