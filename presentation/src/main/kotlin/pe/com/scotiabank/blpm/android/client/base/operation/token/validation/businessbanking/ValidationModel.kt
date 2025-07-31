package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.businessbanking

import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.business.TokenEntity
import pe.com.scotiabank.blpm.android.data.repository.PaymentDataRepository
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection

class ValidationModel(
    dispatcherProvider: DispatcherProvider,
    private val transactionType: String,
    private val repository: PaymentDataRepository,
): DispatcherProvider by dispatcherProvider {

    suspend fun validate(
        transactionId: String,
        smartToken: String,
    ): Unit = withContext(ioDispatcher) {

        val requestEntity = TokenEntity(smartToken)

        val entityOrError: Any = repository
            .otpValidationForBusiness(transactionId, transactionType, requestEntity)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Response<ResponseBody> = transformToEntity(entityOrError)

        if (HttpURLConnection.HTTP_NO_CONTENT == responseEntity.code()) return@withContext
        throw HttpException(responseEntity)
    }
}
