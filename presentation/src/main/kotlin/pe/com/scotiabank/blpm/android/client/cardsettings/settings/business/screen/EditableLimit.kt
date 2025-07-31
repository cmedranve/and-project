package pe.com.scotiabank.blpm.android.client.cardsettings.settings.business.screen

import android.content.res.Resources
import android.icu.text.NumberFormat
import android.text.SpannableStringBuilder
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.FormatterUtil
import pe.com.scotiabank.blpm.android.data.entity.businesscards.settings.LimitEntity
import java.lang.ref.WeakReference

class EditableLimit(
    val id: Long,
    val info: CardLimitInfo,
    private val amountFromNetworkCall: Double,
    private val maxAmount: Double,
    private val weakResources: WeakReference<Resources?>
) {

    var isEnabled: Boolean = false
        private set

    var amount: Double = amountFromNetworkCall
        private set

    val asDataEntity: LimitEntity?
        get() {
            if (isChanged().not()) return null
            val amountConfig: String = FormatterUtil.format(amount.toString(), false)
            return LimitEntity(position = info.positionFromNetworkCall, amountMax = null, amountConfig = amountConfig)
        }

    fun setIsEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }

    fun setAmount(amount: Double) {
        this.amount = amount
    }

    fun isAllowed(): Boolean {
        return amount <= maxAmount
    }

    fun isChanged(): Boolean {
        return if (isEnabled) amount != amountFromNetworkCall else false
    }

    fun createSupplementaryText(doubleNumberFormat: NumberFormat): CharSequence {
        val text: String = weakResources.get()
            ?.getString(R.string.max)
            .orEmpty()

        val formattedMax: String = formatAmount(doubleNumberFormat)
        return SpannableStringBuilder
            .valueOf(text)
            .append(Constant.SPACE_WHITE)
            .append(formattedMax)
    }

    fun createErrorText(doubleNumberFormat: NumberFormat): CharSequence {
        val text: String = weakResources.get()
        ?.getString(R.string.card_settings_edit_text_error)
        .orEmpty()

        val formattedMax: String = formatAmount(doubleNumberFormat)
        return SpannableStringBuilder
            .valueOf(text)
            .append(Constant.SPACE_WHITE)
            .append(Constant.OPEN_BREAK)
            .append(formattedMax)
            .append(Constant.CLOSE_BREAK)
    }

    private fun formatAmount(integerNumberFormat: NumberFormat): String {
        val amountWithSeparator: String = integerNumberFormat.format(maxAmount)
        return String.format(Constant.X_X_PATTERN, info.currency.symbol, amountWithSeparator)
    }

    fun updateLimitAfterLocking(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }
}
