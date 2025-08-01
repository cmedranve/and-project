package pe.com.scotiabank.blpm.android.client.newdashboard.gates

import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.gates.GateMapper
import pe.com.scotiabank.blpm.android.client.model.GateWrapperModel
import pe.com.scotiabank.blpm.android.client.util.observable.onReturnableError
import pe.com.scotiabank.blpm.android.client.util.observable.transformToAny
import pe.com.scotiabank.blpm.android.client.util.observable.transformToEntity
import pe.com.scotiabank.blpm.android.data.entity.GateWrapperEntity
import pe.com.scotiabank.blpm.android.data.entity.gatebff.GateFeedbackEntity
import pe.com.scotiabank.blpm.android.data.repository.gates.GateBffRepository
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class GatesBffModel @Inject constructor(
    dispatcherProvider: DispatcherProvider,
    private val gatesBffRepository: GateBffRepository,
) : DispatcherProvider by dispatcherProvider {

    suspend fun getCampaignsBff() = withContext(ioDispatcher) {
        val entityOfError: Any = gatesBffRepository.getCampaignsBff()
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Response<ResponseBody> = transformToEntity(entityOfError)
        if (responseEntity.code() != HttpURLConnection.HTTP_OK) throw HttpException(responseEntity)
    }

    suspend fun getCampaigns() = withContext(ioDispatcher) {
        val entityOfError: Any = gatesBffRepository.getCampaigns()
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Response<ResponseBody> = transformToEntity(entityOfError)
        if (responseEntity.code() != HttpURLConnection.HTTP_OK) throw HttpException(responseEntity)
    }

    suspend fun getGatesBff(context: String): GateWrapperModel = withContext(ioDispatcher) {
        val entityOfError: Any = gatesBffRepository.getGateBff(context)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Response<GateWrapperEntity> = transformToEntity(entityOfError)
        if (responseEntity.code() != HttpURLConnection.HTTP_OK) throw HttpException(responseEntity)
        val responseBody: GateWrapperEntity = responseEntity.body()
            ?: throw HttpException(responseEntity)
        GateMapper.transformGateWrapper(responseBody)
    }

    suspend fun getGates(vararg context: String): GateWrapperModel = withContext(ioDispatcher) {
        val entityOfError: Any = gatesBffRepository.getGate(*context)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Response<GateWrapperEntity> = transformToEntity(entityOfError)
        if (responseEntity.code() != HttpURLConnection.HTTP_OK) throw HttpException(responseEntity)
        val responseBody: GateWrapperEntity = responseEntity.body()
            ?: throw HttpException(responseEntity)
        GateMapper.transformGateWrapper(responseBody)
    }

    suspend fun sendGateBffFeedback(gateFeedback: GateFeedbackEntity) = withContext(ioDispatcher) {
        val entityOfError: Any = gatesBffRepository.sendGateBffFeedback(gateFeedback)
            .map(::transformToAny)
            .onErrorReturn(::onReturnableError)
            .blockingSingle()

        val responseEntity: Response<ResponseBody> = transformToEntity(entityOfError)
        if (responseEntity.code() != HttpURLConnection.HTTP_OK) throw HttpException(responseEntity)
    }

}