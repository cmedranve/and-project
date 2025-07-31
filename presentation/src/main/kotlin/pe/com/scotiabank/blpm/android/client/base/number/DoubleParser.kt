package pe.com.scotiabank.blpm.android.client.base.number

import android.icu.text.NumberFormat
import pe.com.scotiabank.blpm.android.client.util.parseOrNull

class DoubleParser(
    override val numberFormat: NumberFormat,
    override val defaultAmount: Double = DEFAULT_ZERO_AMOUNT,
) : NumberParser<Double> {

    override fun parse(amountText: CharSequence): Double {
        val number: Number? = numberFormat.parseOrNull(amountText.toString())
        return number?.toDouble() ?: defaultAmount
    }

    companion object {

        val DEFAULT_ZERO_AMOUNT
            @JvmStatic
            get() = 0.0
    }
}
