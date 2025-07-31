package pe.com.scotiabank.blpm.android.client.atmcardhub.personal.cvv

import android.content.res.Resources
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.PushOtpFlowChecker
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardInfo
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.AtmCardState
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.Model
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.MutableStoreOfAtmCard
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.StoreOfAtmCard
import pe.com.scotiabank.blpm.android.client.base.cipher.Decryption
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.ActionRequired
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCard
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.AtmCardType
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialData
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.data.entity.debitcard.CardCredentialsResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.debitcard.KeyResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.debitcard.NewCredentialsEntity
import pe.com.scotiabank.blpm.android.data.entity.nonsession.PeruErrorResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponseException
import pe.com.scotiabank.blpm.android.data.repository.creditcard.CreditCardRepository
import pe.com.scotiabank.blpm.android.data.repository.debitcard.DebitCardRepository
import pe.com.scotiabank.blpm.android.data.util.Constant.ACCEPT_CREDIT_CARD_CREDENTIALS_V2_HEADER
import pe.com.scotiabank.blpm.android.data.util.Constant.ACCEPT_CREDIT_CARD_CREDENTIALS_V3_HEADER
import pe.com.scotiabank.blpm.android.data.util.Constant.ACCEPT_DEBIT_CARD_CREDENTIALS_V2_HEADER
import pe.com.scotiabank.blpm.android.data.util.Constant.ACCEPT_DEBIT_CARD_CREDENTIALS_V3_HEADER
import java.lang.ref.WeakReference

class ModelForPersonalBanking(
    dispatcherProvider: DispatcherProvider,
    private val weakResources: WeakReference<Resources?>,
    override val atmCardInfo: AtmCardInfo,
    private val storeOfAtmCard: MutableStoreOfAtmCard,
    private val credentialDataMapper: CredentialDataMapper,
    private val decryption: Decryption,
    private val debitCardRepository: DebitCardRepository,
    private val creditCardRepository: CreditCardRepository,
    private val pushOtpFlowChecker: PushOtpFlowChecker,
) : Model, DispatcherProvider by dispatcherProvider, StoreOfAtmCard by storeOfAtmCard {

    private val isDecrypted: Boolean
        get() {
            val atmCardReceived: AtmCard = atmCardReceived ?: return false
            val decryptedData: CredentialData = atmCardReceived.credentialData
            return ActionRequired.NONE == decryptedData.actionRequired
        }

    override var credentialDataReceived: CredentialData? = null
        private set

    override var isFullError: Boolean = false

    private val cardType: AtmCardType
        get() = atmCardInfo.atmCard.type

    override val isMainHolder: Boolean
        get() = atmCardInfo.isMainHolder

    override val isCardLocked: Boolean
        get() = atmCardInfo.isCardLocked

    override val isPurchasesDisabled: Boolean
        get() = atmCardInfo.isPurchasesDisabled

    override var isCvvError: Boolean = false
        private set

    override val isCardAvailable: Boolean
        get() = atmCardInfo.isCardAvailable

    override val state: AtmCardState
        get() = when {
            isCardAvailable.not() -> AtmCardState.UNAVAILABLE_CARD
            isFullError -> AtmCardState.FULL_ERROR
            isCvvError -> AtmCardState.CVV_ERROR
            isCardLocked && isMainHolder -> AtmCardState.LOCKED_CARD
            isCardLocked && isMainHolder.not() -> AtmCardState.LOCKED_ADDITIONAL_CARD
            isPurchasesDisabled && isMainHolder -> AtmCardState.DISABLED_PURCHASES_FOR_CARD
            isPurchasesDisabled && isMainHolder.not() -> AtmCardState.DISABLED_PURCHASES_FOR_ADDITIONAL_CARD
            isDecrypted.not() -> AtmCardState.ENCRYPTED
            else -> AtmCardState.DECRYPTED
        }

    init {
        receiveAtmCard(atmCardInfo.atmCard)
    }

    private fun receiveAtmCard(atmCard: AtmCard) {
        storeOfAtmCard.atmCardReceived = atmCard
        credentialDataReceived = atmCard.credentialData
    }

    override suspend fun getCredentialData(): CredentialData = when (cardType) {
        AtmCardType.DEBIT -> getDebitCardCredentialData()
        AtmCardType.CREDIT -> getCreditCardCredentialData()
        else -> throw IllegalStateException("Unreachable code")
    }

    private suspend fun getDebitCardCredentialData() = withContext(ioDispatcher) {

        val requestEntity = NewCredentialsEntity(atmCardInfo.operationId)
        val acceptHeader = pickAcceptHeaderForDebitCardCredentials()
        val httpResponse: HttpResponse<*> = debitCardRepository.getCredentials(
            acceptHeader = acceptHeader,
            authId = atmCardInfo.authId,
            authTracking = atmCardInfo.authTracking,
            cardId = atmCardInfo.cardId,
            requestEntity = requestEntity,
        )

        when (val responseEntity: Any? = httpResponse.body) {
            is CardCredentialsResponseEntity -> credentialDataMapper.toDataToDecrypt(responseEntity)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }

    private fun pickAcceptHeaderForDebitCardCredentials(): String = when {
        pushOtpFlowChecker.isPushOtpEnabled -> ACCEPT_DEBIT_CARD_CREDENTIALS_V3_HEADER
        else -> ACCEPT_DEBIT_CARD_CREDENTIALS_V2_HEADER
    }

    private suspend fun getCreditCardCredentialData(): CredentialData = withContext(ioDispatcher) {
        val requestEntity = NewCredentialsEntity(atmCardInfo.operationId)
        val acceptHeader = pickAcceptHeaderForCreditCardCredentials()
        val httpResponse: HttpResponse<*> = creditCardRepository.getCredentials(
            acceptHeader = acceptHeader,
            authId = atmCardInfo.authId,
            authTracking = atmCardInfo.authTracking,
            cardId = atmCardInfo.cardId,
            requestEntity = requestEntity
        )
        when (val responseEntity: Any? = httpResponse.body) {
            is CardCredentialsResponseEntity -> credentialDataMapper.toDataToDecrypt(responseEntity)
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }

    private fun pickAcceptHeaderForCreditCardCredentials(): String = when {
        pushOtpFlowChecker.isPushOtpEnabled -> ACCEPT_CREDIT_CARD_CREDENTIALS_V3_HEADER
        else -> ACCEPT_CREDIT_CARD_CREDENTIALS_V2_HEADER
    }

    override suspend fun getDecryptionKey(): String = when (cardType) {
        AtmCardType.DEBIT -> getDebitCardDecryptionKey()
        AtmCardType.CREDIT -> getCreditCardDecryptionKey()
        else -> throw IllegalStateException("Unreachable code")
    }

    private suspend fun getDebitCardDecryptionKey(): String = withContext(ioDispatcher) {

        val httpResponse: HttpResponse<*> = debitCardRepository.getDecryptionKey(
            operationId = atmCardInfo.operationId,
        )

        when (val responseEntity: Any? = httpResponse.body) {
            is KeyResponseEntity -> responseEntity.key.orEmpty()
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }

    private suspend fun getCreditCardDecryptionKey(): String = withContext(ioDispatcher) {
        val httpResponse: HttpResponse<*> = creditCardRepository.getDecryptionKey(atmCardInfo.operationId)
        when (val responseEntity: Any? = httpResponse.body) {
            is KeyResponseEntity -> responseEntity.key.orEmpty()
            is PeruErrorResponseBody -> throw HttpResponseException(httpResponse, responseEntity)
            else -> throw IllegalStateException("Unreachable code")
        }
    }

    override suspend fun decryptCredentialData(
        data: CredentialData,
        key: String,
    ): CredentialData = withContext(defaultDispatcher) {

        val cardNumber: String = decryption.decrypt(data.cardNumber.toString(), key)
        val expiryDate: String = decryption.decrypt(data.expiryDate.toString(), key)
        val code: String = decryptCode(code = data.code.toString(), key = key)
        credentialDataMapper.toDecryptedData(
            cardNumber = cardNumber,
            expiryDate = expiryDate,
            code = code,
        )
    }

    private fun decryptCode(code: String, key: String): String {
        isCvvError = code.isBlank()

        if (isCvvError) return weakResources.get()?.getString(R.string.cvv_default).orEmpty()

        return decryption.decrypt(code, key)
    }
}
