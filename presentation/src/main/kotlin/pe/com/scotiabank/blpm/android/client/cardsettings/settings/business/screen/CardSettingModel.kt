package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen.CardSettings
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.verification.TransactionType
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.BusinessCardSettingsRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.BusinessCardSettingsResponseEntity
import pe.com.scotiabank.blpm.android.data.exception.createExceptionOnIllegalResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import pe.com.scotiabank.blpm.android.data.util.Constant

class CardSettingModel(
    dispatcherProvider: DispatcherProvider,
    val pushOtpFlowChecker: PushOtpFlowChecker,
    private val card: AtmCardInfo,
    private val cardRepository: BusinessCardsRepository,
    private val businessOtpRepository: BusinessOtpRepository,
    private val holderOfCardSettings: MutableHolderOfCardSettings,
    private val mapper: CardSettingsMapper,
): DispatcherProvider by dispatcherProvider, HolderOfCardSettings by holderOfCardSettings {

    val operationId: String
        get() = holderOfCardSettings.settingsReceived?.operationId ?: String.EMPTY

    suspend fun fetchSettings(): Unit = withContext(ioDispatcher) {
        val httpResponse: HttpResponse<*> = cardRepository.getSettings(
            id = card.cardId,
            acceptHeader = pickAcceptHeader(),
        )
        val responseEntity: BusinessCardSettingsResponseEntity = httpResponse.body as? BusinessCardSettingsResponseEntity
            ?: throw createExceptionOnIllegalResponseBody(BusinessCardSettingsResponseEntity::class)

        val settings = mapper.toSettings(responseEntity)
        receiveCardSettings(settings)
    }

    private fun receiveCardSettings(settings: CardSettings) {
        holderOfCardSettings.settingsReceived = settings
    }

    suspend fun requestOtp() = withContext(ioDispatcher) {
        businessOtpRepository.requestOtp(
            transactionId = operationId,
            transactionType = TransactionType.SETTINGS.typeForNetworkCall,
        )
    }

    suspend fun updateSettings(
        authId: String,
        authTracking: String,
        requestEntity: BusinessCardSettingsRequestEntity
    ) = withContext(ioDispatcher) {
        cardRepository.updateSettings(
            cardId = card.cardId,
            acceptHeader = pickAcceptHeader(),
            authId = authId,
            authTracking = authTracking,
            requestEntity = requestEntity
        )
    }

    private fun pickAcceptHeader(): String = when {
        pushOtpFlowChecker.isPushOtpEnabled -> Constant.ACCEPT_BUSINESS_SETTINGS_V2_HEADER
        else -> String.EMPTY
    }
}