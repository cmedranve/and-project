package pe.com.scotiabank.blpm.android.client.base.operation.frequent

import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.model.BaseSummaryModel
import pe.com.scotiabank.blpm.android.data.entity.FrequentPaymentEntity
import pe.com.scotiabank.blpm.android.data.repository.FrequentOperationDataRepository
import retrofit2.Response
import java.net.HttpURLConnection

class FeasibleFrequentOperationModel<R: Any, S: BaseSummaryModel>(
    dispatcherProvider: DispatcherProvider,
    private val frequentFactory: FrequentFactory<S>,
    private val repository: FrequentOperationDataRepository,
) : FrequentOperationModel<R, S>, DispatcherProvider by dispatcherProvider {

    override suspend fun verify(
        responseEntity: R,
        summary: S,
    ): Boolean = withContext(ioDispatcher) {
        val requestEntity: FrequentPaymentEntity = frequentFactory.create(summary)
        tryVerify(requestEntity)
    }

    private fun tryVerify(requestEntity: FrequentPaymentEntity): Boolean = try {
        repository.frequentPaymentQuery(requestEntity)
            .map(::findResponseBody)
            .blockingSingle()
    } catch (throwable: Throwable) {
        true
    }

    private fun findResponseBody(response: Response<ResponseBody>): Boolean = when (response.code()) {
        HttpURLConnection.HTTP_OK -> true
        HttpURLConnection.HTTP_NO_CONTENT -> false
        else -> throw RuntimeException()
    }
}
