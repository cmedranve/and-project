package pe.com.scotiabank.blpm.android.client.base.products.newpicking.editableinstallment

import android.text.SpannableStringBuilder
import pe.com.scotiabank.blpm.android.client.base.number.IntegerParser
import pe.com.scotiabank.blpm.android.client.util.Constant

class EditableInstallment {

    val rangeText: CharSequence by lazy {
        val minAsText: String = MIN_NUMBER_ALLOWED.toString()
        val maxAsText: String = MAX_NUMBER_ALLOWED.toString()
        SpannableStringBuilder
            .valueOf(Constant.MIN_ABBREVIATION)
            .append(Constant.SPACE_WHITE)
            .append(minAsText)
            .append(Constant.SPACE_WHITE)
            .append(Constant.HYPHEN_STRING)
            .append(Constant.SPACE_WHITE)
            .append(Constant.MAX_ABBREVIATION)
            .append(Constant.SPACE_WHITE)
            .append(maxAsText)
    }

    fun getErrorTextOrEmpty(
        text: CharSequence,
        integerParser: IntegerParser,
    ): CharSequence = if (text.isEmpty()) Constant.EMPTY_STRING else findErrorText(text, integerParser)

    private fun findErrorText(text: CharSequence, integerParser: IntegerParser): CharSequence {
        val installments: Int = integerParser.parse(text)
        return when {
            isBetween(installments).not() -> rangeText
            else -> Constant.EMPTY_STRING
        }
    }

    fun isAllowed(text: CharSequence, integerParser: IntegerParser): Boolean {
        val installments: Int = integerParser.parse(text)
        return isBetween(installments)
    }

    private fun isBetween(
        installments: Int,
    ): Boolean = installments in MIN_NUMBER_ALLOWED..MAX_NUMBER_ALLOWED

    companion object {

        val MIN_NUMBER_ALLOWED: Int
            @JvmStatic
            get() = 2

        val MAX_NUMBER_ALLOWED: Int
            @JvmStatic
            get() = 36
    }
}
