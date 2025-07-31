package pe.com.scotiabank.blpm.android.client.cardsettings.hub

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.data.entity.cardsettings.CardsSettingResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.exception.createExceptionOnIllegalResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.cardsettings.CardSettingDataRepository

class NewCardSettingHubModel(
    dispatcherProvider: DispatcherProvider,
    private val repository: CardSettingDataRepository,
    private val hubMapper: NewCardSettingHubMapper,
) : DispatcherProvider by dispatcherProvider {

    @Suppress("UNUSED_PARAMETER")
    suspend fun getCardSettingHub(
        inputData: Map<Long, Any?>,
    ): CardSettingHub = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = repository.getCardSettingHub()

        when (val responseEntity: Any? = httpResponse.body) {
            is CardsSettingResponseEntity -> hubMapper.toCardSettingHub(responseEntity)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw createExceptionOnIllegalResponseBody(CardsSettingResponseEntity::class)
        }
    }
}
