package pe.com.scotiabank.blpm.android.ui.list.items.inputs

import android.icu.text.DecimalFormatSymbols
import java.util.*

class DecimalGrouping(private val digitsAfterDecimalSeparator: Int, locale: Locale) {

    val separator: Char = DecimalFormatSymbols.getInstance(locale).decimalSeparator

    fun createPatternStartingWithZero(): String = "(0{0,1})${createPattern()}"

    fun createPatternStartingWithAny(): String = "([0-9]{0,1})${createPattern()}"

    fun createPattern(): String = "((\\$separator[0-9]{0,$digitsAfterDecimalSeparator})?)"
}
