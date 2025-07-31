package pe.com.scotiabank.blpm.android.client.base.crasherrorreporting

import android.content.Context
import com.scotiabank.sdk.crasherrorreporting.network.NetworkMetadata
import com.scotiabank.sdk.crasherrorreporting.network.buffer
import okhttp3.Response
import pe.com.scotiabank.blpm.android.analytics.factories.AnalyticsBaseConstant
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.exception.RetrofitException
import pe.com.scotiabank.blpm.android.data.exception.FinishedSessionException
import pe.com.scotiabank.blpm.android.data.exception.ForceUpdateException
import pe.com.scotiabank.blpm.android.data.exception.NetworkConnectionException
import pe.com.scotiabank.blpm.android.data.exception.UnknownException

class ErrorInterceptorMapper(private val appContext: Context) {

    fun map(throwable: Throwable): NetworkMetadata = when (throwable) {
        is FinishedSessionException -> NetworkMetadata(
            code = throwable.httpCode,
            buffer = throwable.response.buffer(),
            values = map(throwable.response)
        )
        is ForceUpdateException -> NetworkMetadata(
            code = throwable.httpCode,
            buffer = throwable.response.buffer(),
            values = map(throwable.response)
        )
        is UnknownException -> NetworkMetadata(
            code = throwable.httpCode,
            buffer = throwable.response.buffer(),
            values = emptyMap()
        )
        is NetworkConnectionException -> createNetworkMetadataOnConnectionError(throwable)
        else -> createNetworkMetadataOnUnexpectedError(throwable)
    }

    fun map(response: Response): Map<String, String> {
        try {
            if (response.code >= 500) {
                return emptyMap()
            }
            val body = response.peekBody(Long.MAX_VALUE)
            val exception = RetrofitException.httpError(appContext, response.code, body)
            val code = exception.responseCode
            val message = exception.message ?: Constant.HYPHEN_STRING
            return buildValuesWith(code, message)
        } catch (ex: Throwable) {
            return emptyMap()
        }
    }

    private fun buildValuesWith(
        code: String,
        message: String,
    ): Map<String, String> = mapOf(
        AnalyticsBaseConstant.ERROR_CODE to code,
        AnalyticsBaseConstant.ERROR_MESSAGE to message,
    )

    private fun createNetworkMetadataOnConnectionError(
        throwable: NetworkConnectionException,
    ): NetworkMetadata {
        val exception: RetrofitException = RetrofitException.networkError(appContext, throwable)
        val message = exception.message ?: Constant.HYPHEN_STRING
        val code = CODE_FOR_NETWORK_CONNECTION
        return NetworkMetadata(
            code = code,
            buffer = null,
            values = buildValuesWith(exception.responseCode, message)
        )
    }

    private fun createNetworkMetadataOnUnexpectedError(throwable: Throwable): NetworkMetadata {
        val exception: RetrofitException = RetrofitException.unexpectedError(appContext, throwable)
        val message = exception.message ?: Constant.HYPHEN_STRING
        val code = CODE_DEFAULT
        return NetworkMetadata(
            code = code,
            buffer = null,
            values = buildValuesWith(exception.responseCode, message)
        )
    }

    companion object {
        const val CODE_DEFAULT = 500
        const val CODE_FOR_NETWORK_CONNECTION = 600
    }
}
