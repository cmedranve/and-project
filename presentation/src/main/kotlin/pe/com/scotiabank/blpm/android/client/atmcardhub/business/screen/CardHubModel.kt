package pe.com.scotiabank.blpm.android.client.atmcardhub.business.screen

import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCard
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.base.verification.TransactionType
import pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen.CardSettingsMapper
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.entity.businesscards.BusinessCardsResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.businesscards.credentials.OperationResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.BusinessCardSettingsResponseEntity
import pe.com.scotiabank.blpm.android.data.exception.createExceptionOnIllegalResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.repository.otp.BusinessOtpRepository
import pe.com.scotiabank.blpm.android.data.util.Constant

class CardHubModel(
    dispatcherProvider: DispatcherProvider,
    val pushOtpFlowChecker: PushOtpFlowChecker,
    private val repository: BusinessCardsRepository,
    private val mapper: CardHubMapper,
    private val cardSettingsMapper: CardSettingsMapper,
    private val credentialDataMapper: CredentialDataMapper,
    private val businessOtpRepository: BusinessOtpRepository,
): DispatcherProvider by dispatcherProvider {

    var currentCard: AtmCardInfo? = null

    suspend fun getCards(): List<AtmCardInfo> = withContext(ioDispatcher) {
        val httpResponse: HttpResponse<*> = repository.getCards()
        val responseEntity: BusinessCardsResponseEntity = httpResponse.body as? BusinessCardsResponseEntity
            ?: throw createExceptionOnIllegalResponseBody(BusinessCardsResponseEntity::class)

        mapper.toCards(responseEntity)
    }

    suspend fun fetchOperationId() = withContext(ioDispatcher) {
        val httpResponse: HttpResponse<*> = repository.getOperationId(
            acceptHeader = pickAcceptHeader(),
        )
        val responseEntity: OperationResponseEntity? = httpResponse.body as? OperationResponseEntity
        val operationId: String = responseEntity
            ?.operationId
            ?.takeUnless(String::isNullOrEmpty)
            ?: throw createExceptionOnIllegalResponseBody(OperationResponseEntity::class)

        currentCard?.operationId = operationId
    }

    private fun pickAcceptHeader(): String = when {
        pushOtpFlowChecker.isPushOtpEnabled -> Constant.ACCEPT_BUSINESS_CREDENTIALS_V2_HEADER
        else -> String.EMPTY
    }

    suspend fun requestOtp(card: AtmCardInfo): HttpResponse<*> = withContext(ioDispatcher) {
        businessOtpRepository.requestOtp(
            transactionId = card.operationId,
            transactionType = TransactionType.CVV.typeForNetworkCall,
        )
    }

    suspend fun getCardSettingsDetail(cardId: String): CardSettings = withContext(ioDispatcher) {
        val httpResponse: HttpResponse<*> = repository.getSettings(
            id = cardId,
            acceptHeader = pickSettingAcceptHeader(),
        )
        val responseEntity: BusinessCardSettingsResponseEntity = httpResponse.body as? BusinessCardSettingsResponseEntity
            ?: throw createExceptionOnIllegalResponseBody(BusinessCardSettingsResponseEntity::class)

        val cardSettings: CardSettings = cardSettingsMapper.toSettings(responseEntity)
        editCurrentCard(cardSettings)
        cardSettings
    }

    private fun editCurrentCard(cardSettings: CardSettings) {
        val card: AtmCardInfo = currentCard ?: return

        val atmCard: AtmCard = card.atmCard
        card.authId = String.EMPTY
        card.authTracking = String.EMPTY
        card.isCardLocked = cardSettings.isTempLock
        card.isPurchasesDisabled = cardSettings.isOnlinePurchase.not()
        atmCard.credentialData = credentialDataMapper.toDataToMask(atmCard.number)
    }

    private fun pickSettingAcceptHeader(): String = when {
        pushOtpFlowChecker.isPushOtpEnabled -> Constant.ACCEPT_BUSINESS_SETTINGS_V2_HEADER
        else -> String.EMPTY
    }
}
