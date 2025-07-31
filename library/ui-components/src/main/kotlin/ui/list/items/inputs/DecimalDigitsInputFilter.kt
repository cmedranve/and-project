package pe.com.scotiabank.blpm.android.ui.list.items.inputs

import android.text.InputFilter
import android.text.Spanned
import pe.com.scotiabank.blpm.android.ui.util.Constant
import java.util.Locale
import kotlin.math.absoluteValue

class DecimalDigitsInputFilter(
    digitsBeforeDecimalSeparator: Int = DIGITS_BEFORE_DECIMAL_SEPARATOR,
    digitsAfterDecimalSeparator: Int = DIGITS_AFTER_DECIMAL_SEPARATOR,
    locale: Locale = Locale.getDefault(),
): InputFilter {

    private val regex: Regex = createRegex(
        digitsBeforeDecimalSeparator = digitsBeforeDecimalSeparator,
        digitsAfterDecimalSeparator = digitsAfterDecimalSeparator,
        locale = locale,
    )

    private fun createRegex(
        digitsBeforeDecimalSeparator: Int,
        digitsAfterDecimalSeparator: Int,
        locale: Locale,
    ): Regex {
        val decimalGrouping = DecimalGrouping(digitsAfterDecimalSeparator, locale)
        val thousandGrouping = ThousandGrouping(decimalGrouping, digitsBeforeDecimalSeparator, locale)
        val pattern: String = createPattern(
            decimalGrouping = decimalGrouping,
            thousandGrouping = thousandGrouping,
            digitsBeforeDecimalSeparator = digitsBeforeDecimalSeparator.absoluteValue,
        )
        return pattern.toRegex()
    }

    private fun createPattern(
        decimalGrouping: DecimalGrouping,
        thousandGrouping: ThousandGrouping,
        digitsBeforeDecimalSeparator: Int,
    ): String = when (digitsBeforeDecimalSeparator) {

        0 -> decimalGrouping.createPatternStartingWithZero()
        1 -> decimalGrouping.createPatternStartingWithAny()
        else -> thousandGrouping.createForMultipleDigits()
    }

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        val replacement: String = source?.substring(start, end).orEmpty()
        val newVal: String = dest?.substring(0, dstart) + replacement + dest?.substring(dend, dest.length)
        if (newVal.matches(regex)) return null // keep original

        return if (source.isNullOrEmpty()) dest?.substring(dstart, dend) else Constant.EMPTY
    }

    companion object {

        private val DIGITS_BEFORE_DECIMAL_SEPARATOR
            get() = 8
        private val DIGITS_AFTER_DECIMAL_SEPARATOR
            get() = 2
    }
}
