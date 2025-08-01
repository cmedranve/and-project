package pe.com.scotiabank.blpm.android.client.newdashboard.edit.transfer

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.number.NumberParser
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import java.lang.ref.WeakReference

class AmountHelper(
    private val weakResources: WeakReference<Resources?>,
    doubleParser: NumberParser<Double>,
) : NumberParser<Double> by doubleParser {

    fun isAmountAllowed(anyAmountText: CharSequence?): Boolean {
        val amountText: CharSequence = anyAmountText ?: return false
        val amount: Double = parse(amountText)
        return amount >= MIN_AMOUNT_ALLOWED_TO_ENTER
    }

    fun pickErrorMessageFor(currency: Currency): CharSequence = weakResources.get()
        ?.getString(
            R.string.you_can_trans_from,
            currency.symbol,
            MIN_AMOUNT_ALLOWED_TO_ENTER.toString(),
        )
        .orEmpty()

    companion object {

        private val MIN_AMOUNT_ALLOWED_TO_ENTER
            get() = 0.01
    }
}