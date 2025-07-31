package pe.com.scotiabank.blpm.android.client.base.operation.token.request.businessbanking

import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.repository.PaymentDataRepository
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection

class TokenRequestModelForBusinessBanking(
    dispatcherProvider: DispatcherProvider,
    private val repository: PaymentDataRepository,
    private val transactionType: String,
): DispatcherProvider by dispatcherProvider {

    suspend fun requestToken(transactionId: String): Unit = withContext(ioDispatcher) {

        val entityOrError: Any = repository.requestOtpForBusiness(transactionId, transactionType)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Response<ResponseBody> = transformToEntity(entityOrError)

        if (HttpURLConnection.HTTP_NO_CONTENT == responseEntity.code()) return@withContext
        throw HttpException(responseEntity)
    }
}
