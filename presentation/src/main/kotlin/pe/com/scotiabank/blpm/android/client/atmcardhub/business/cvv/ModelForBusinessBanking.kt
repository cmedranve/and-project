package pe.com.scotiabank.blpm.android.client.atmcardhub.business.cvv

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
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialData
import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialDataMapper
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import pe.com.scotiabank.blpm.android.data.entity.businesscards.credentials.BusinessCardCredentialsRequestEntity
import pe.com.scotiabank.blpm.android.data.entity.debitcard.CardCredentialsResponseEntity
import pe.com.scotiabank.blpm.android.data.entity.debitcard.KeyResponseEntity
import pe.com.scotiabank.blpm.android.data.exception.createExceptionOnIllegalResponseBody
import pe.com.scotiabank.blpm.android.data.net.client.HttpResponse
import pe.com.scotiabank.blpm.android.data.repository.businesscards.BusinessCardsRepository
import pe.com.scotiabank.blpm.android.data.util.Constant
import java.lang.ref.WeakReference

class ModelForBusinessBanking(
    dispatcherProvider: DispatcherProvider,
    private val weakResources: WeakReference<Resources?>,
    override val atmCardInfo: AtmCardInfo,
    private val storeOfAtmCard: MutableStoreOfAtmCard,
    private val credentialDataMapper: CredentialDataMapper,
    private val decryption: Decryption,
    private val cardRepository: BusinessCardsRepository,
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

    override val isMainHolder: Boolean
        get() = atmCardInfo.isMainHolder

    override val isCardLocked: Boolean
        get() = atmCardInfo.isCardLocked

    override val isPurchasesDisabled: Boolean
        get() = atmCardInfo.isPurchasesDisabled

    private val includeCvv: Boolean
        get() = isCardLocked.not() && isPurchasesDisabled.not()

    override var isCvvError: Boolean = false
        private set

    override val isCardAvailable: Boolean
        get() = atmCardInfo.isCardAvailable

    override val state: AtmCardState
        get() = when {
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

    override suspend fun getCredentialData(): CredentialData = withContext(ioDispatcher) {
        val requestEntity = BusinessCardCredentialsRequestEntity(
            cardId = atmCardInfo.cardId,
            cardType = atmCardInfo.atmCard.type.nameFromNetworkCall,
            includeCvv = includeCvv,
            operationId = atmCardInfo.operationId,
        )
        val httpResponse: HttpResponse<*> = cardRepository.getCredentials(
            acceptHeader = pickAcceptHeaderForCvv(),
            authId = atmCardInfo.authId,
            authTracking = atmCardInfo.authTracking,
            requestEntity = requestEntity,
        )

        val responseEntity: CardCredentialsResponseEntity = httpResponse.body as? CardCredentialsResponseEntity
            ?: throw createExceptionOnIllegalResponseBody(CardCredentialsResponseEntity::class)
        credentialDataMapper.toDataToDecrypt(responseEntity)
    }

    private fun pickAcceptHeaderForCvv(): String = when {
        pushOtpFlowChecker.isPushOtpEnabled -> Constant.ACCEPT_BUSINESS_CREDENTIALS_V2_HEADER
        else -> String.EMPTY
    }

    override suspend fun getDecryptionKey(): String = withContext(ioDispatcher) {
        val httpResponse: HttpResponse<*> = cardRepository.getDecryptionKey(
            operationId = atmCardInfo.operationId,
            acceptHeader = pickAcceptHeaderForCvv(),
        )

        val responseEntity: KeyResponseEntity = httpResponse.body as? KeyResponseEntity
            ?: throw createExceptionOnIllegalResponseBody(KeyResponseEntity::class)
        responseEntity.key.orEmpty()
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
