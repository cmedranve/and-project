package pe.com.scotiabank.blpm.android.client.base.operation.currencyamount

import androidx.core.util.Function
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.util.Constant
import java.math.BigDecimal

class CurrencyFormatter(private val formatting: Function<String, String>) {

    fun format(currencyCode: String?, amount: BigDecimal): CharSequence = format(
        currencyCode = currencyCode,
        amount = amount.toString(),
    )

    fun format(currency: Currency, amount: BigDecimal): CharSequence = format(
        currencyCode = currency.id,
        amount = amount.toString(),
    )

    fun format(currency: Currency, amount: Double): CharSequence = format(
        currencyCode = currency.id,
        amount = amount.toString(),
    )

    fun format(currencyCode: String?, amount: Double): CharSequence = format(
        currencyCode = currencyCode,
        amount = amount.toString(),
    )

    fun format(currencyCode: String?, amount: String): CharSequence {
        val amountWithDecimals: String = formatting.apply(amount)
        val currency: Currency = Currency.identifyBy(currencyCode)
        return String.format(Constant.X_X_PATTERN, currency.symbol, amountWithDecimals)
    }
}
