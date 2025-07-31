package pe.com.scotiabank.blpm.android.client.cardlesswithdrawal.form

import pe.com.scotiabank.blpm.android.client.util.Constant

object PhoneNumberUtil {

    @JvmStatic
    fun String.removeWhitespaces(): String = replace(Constant.SPACE_WHITE, Constant.EMPTY_STRING)

    @JvmStatic
    fun removeCountryCode(value: String): String {
        var valueWithoutCode: String = value
        if (valueWithoutCode.contains(Constant.PREFIX_PERU_PHONE)) {
            valueWithoutCode = valueWithoutCode.replace(Constant.PREFIX_PERU_PHONE, Constant.EMPTY_STRING)
        }
        if (valueWithoutCode.contains(Constant.PLUS_SYMBOL)) {
            valueWithoutCode = valueWithoutCode.replace(Constant.PLUS_SYMBOL, Constant.EMPTY_STRING)
        }
        return valueWithoutCode
    }

    @JvmStatic
    fun phoneSubString(value: String): String {
        val maxLength = Constant.PHONE_MAX_LENGTH
        return if (value.length > maxLength) value.substring(0, maxLength) else value
    }

    @JvmStatic
    fun isValid(
        numberWithoutSpaces: String
    ): Boolean = isHavingNineDigits(numberWithoutSpaces)
            && isStartingWithNine(numberWithoutSpaces)

    @JvmStatic
    private fun isStartingWithNine(value: String): Boolean {
        return value.startsWith(Constant.NUMBER_NINE)
    }

    @JvmStatic
    private fun isHavingNineDigits(value: String): Boolean {
        return value.isNotEmpty()
            && isStartingWithNine(value)
            && Constant.PHONE_MAX_LENGTH == value.trim().removeWhitespaces().length
    }

    @JvmStatic
    fun maskPhoneNumber(value: String): String {
        if (value.length != Constant.NINE) return Constant.EMPTY_STRING

        return Constant.PREFIX_PERU_PHONE + Constant.MASKED_PHONE_NUMBER + value.takeLast(3)
    }
}
