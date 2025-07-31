package pe.com.scotiabank.blpm.android.client.base.number

import android.icu.text.NumberFormat
import pe.com.scotiabank.blpm.android.client.util.parseOrNull

class IntegerParser(
    override val numberFormat: NumberFormat,
    override val defaultAmount: Int = DEFAULT_ZERO_AMOUNT,
) : NumberParser<Int> {

    override fun parse(amountText: CharSequence): Int {
        val number: Number? = numberFormat.parseOrNull(amountText.toString())
        return number?.toInt() ?: defaultAmount
    }

    companion object {

        private val DEFAULT_ZERO_AMOUNT
            get() = 0
    }
}
