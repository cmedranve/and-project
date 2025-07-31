package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv

import pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard.CredentialData

interface Model : StoreOfAtmCard {

    val atmCardInfo: AtmCardInfo
    val credentialDataReceived: CredentialData?
    var isFullError: Boolean
    val isMainHolder: Boolean
    val isCardLocked: Boolean
    val isPurchasesDisabled: Boolean
    val isCvvError: Boolean
    val state: AtmCardState
    val isCardAvailable: Boolean

    suspend fun getCredentialData(): CredentialData

    suspend fun getDecryptionKey(): String

    suspend fun decryptCredentialData(data: CredentialData, key: String): CredentialData
}
