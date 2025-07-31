package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import pe.com.scotiabank.blpm.android.data.entity.debitcard.CardCredentialsResponseEntity

interface CredentialDataMapper {

    fun toDataToMask(cardNumber: String): CredentialData

    fun toMaskedData(data: CredentialData): CredentialData

    fun toDataToDecrypt(entity: CardCredentialsResponseEntity): CredentialData

    fun toDecryptedData(cardNumber: String, expiryDate: String, code: String): CredentialData
}
