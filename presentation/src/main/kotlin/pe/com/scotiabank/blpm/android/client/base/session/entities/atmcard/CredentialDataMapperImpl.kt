package pe.com.scotiabank.blpm.android.client.base.session.entities.atmcard

import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.data.entity.debitcard.CardCredentialsResponseEntity

class CredentialDataMapperImpl : CredentialDataMapper {

    override fun toDataToMask(cardNumber: String): CredentialData = CredentialData(
        actionRequired = ActionRequired.MASK,
        cardNumber = cardNumber,
        expiryDate = Constant.EMPTY_STRING,
        code = Constant.EMPTY_STRING,
    )

    override fun toMaskedData(data: CredentialData): CredentialData {

        val unmaskedNumber: CharSequence = takeLastDigits(data.cardNumber)
        val maskedCardNumber: CharSequence = MASKED_CARD_NUMBER + Constant.SPACE_WHITE + unmaskedNumber

        return CredentialData(
            actionRequired = ActionRequired.NONE,
            cardNumber = maskedCardNumber,
            expiryDate = MASKED_EXPIRY_DATE,
            code = MASKED_CODE,
        )
    }

    private fun takeLastDigits(cardNumber: CharSequence): CharSequence {
        if (cardNumber.length < QUANTITY_OF_UNMASKED_DIGITS) return cardNumber
        return cardNumber.takeLast(QUANTITY_OF_UNMASKED_DIGITS)
    }

    override fun toDataToDecrypt(
        entity: CardCredentialsResponseEntity,
    ): CredentialData = CredentialData(
        actionRequired = ActionRequired.DECRYPT,
        cardNumber = entity.cardNumber.orEmpty(),
        expiryDate = entity.expirationDate.orEmpty(),
        code = entity.cvv.orEmpty(),
    )

    override fun toDecryptedData(
        cardNumber: String,
        expiryDate: String,
        code: String,
    ): CredentialData = CredentialData(
        actionRequired = ActionRequired.NONE,
        cardNumber = formatCardNumber(cardNumber),
        expiryDate = expiryDate,
        code = code,
    )

    private fun formatCardNumber(cardNumber: String): String {
        val cardNumberWithoutSpaces: String = cardNumber.replace(
            oldValue = Constant.SPACE_WHITE,
            newValue = Constant.EMPTY_STRING,
        )
        val groupsOfFourDigits: MutableList<String> = mutableListOf()

        for (startIndex in cardNumberWithoutSpaces.indices step QUANTITY_OF_UNMASKED_DIGITS) {

            val endIndex: Int = startIndex + QUANTITY_OF_UNMASKED_DIGITS
            val fourDigits: String = cardNumberWithoutSpaces.substring(startIndex, endIndex)

            groupsOfFourDigits.add(fourDigits)
        }

        return groupsOfFourDigits.joinToString(separator = Constant.SPACE_WHITE)
    }

    companion object {

        private val QUANTITY_OF_UNMASKED_DIGITS: Int
            @JvmStatic
            get() = 4

        private val MASKED_CARD_NUMBER: String
            @JvmStatic
            get() = "•••• •••• ••••"

        private val MASKED_EXPIRY_DATE: String
            @JvmStatic
            get() = "••/••"

        private val MASKED_CODE: String
            @JvmStatic
            get() = "•••"
    }
}
