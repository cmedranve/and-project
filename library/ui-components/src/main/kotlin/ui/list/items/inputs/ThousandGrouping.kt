package pe.com.scotiabank.blpm.android.ui.list.items.inputs

import android.icu.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.ceil

class ThousandGrouping(
    private val decimalGrouping: DecimalGrouping,
    private val digitsBeforeDecimalSeparator: Int,
    locale: Locale,
) {

    private val separator: Char = DecimalFormatSymbols.getInstance(locale).groupingSeparator

    fun createForMultipleDigits(): String {
        return "${createPattern()}|${decimalGrouping.createPatternStartingWithAny()}"
    }

    private fun createPattern(): String {
        val groupsAfter: Int = countGroupsAfterLeftMostSeparator(digitsBeforeDecimalSeparator.toDouble())
        val digitsBetween: Int = countDigitsBetweenNonZeroAndLeftMostSeparator(digitsBeforeDecimalSeparator, groupsAfter)
        return "([1-9]{1})([0-9]{0,$digitsBetween})(((\\$separator)?([0-9]{0,3})){0,$groupsAfter})${decimalGrouping.createPattern()}"
    }

    private fun countGroupsAfterLeftMostSeparator(digitsBeforeDecimalSeparator: Double): Int {
        val mixedFractionalNumberOfGroups: Double = digitsBeforeDecimalSeparator.div(
            NUMBER_OF_DIGITS_PER_THOUSAND_GROUP
        )
        val totalOfGroups: Int = ceil(mixedFractionalNumberOfGroups).toInt()
        return totalOfGroups - NUMBER_OF_LEFT_MOST_GROUPS
    }

    private fun countDigitsBetweenNonZeroAndLeftMostSeparator(digitsBeforeDecimalSeparator: Int, groupsAfter: Int): Int {
        val totalOfDigits: Int = groupsAfter * NUMBER_OF_DIGITS_PER_THOUSAND_GROUP
        val digitsBeforeLeftMostSeparator: Int = digitsBeforeDecimalSeparator - totalOfDigits
        return digitsBeforeLeftMostSeparator - NUMBER_OF_LEFT_MOST_DIGITS
    }

    companion object {

        private val NUMBER_OF_DIGITS_PER_THOUSAND_GROUP
            get() = 3
        private val NUMBER_OF_LEFT_MOST_GROUPS
            get() = 1
        private val NUMBER_OF_LEFT_MOST_DIGITS
            get() = 1
    }
}
