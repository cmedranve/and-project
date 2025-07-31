package pe.com.scotiabank.blpm.android.client.base.products.newpicking.exchangerate

import android.content.res.Resources
import android.view.Gravity
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.session.HolderOfProfile
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.base.session.exchangerate.ExchangeRate
import pe.com.scotiabank.blpm.android.client.newpayment.enabled.shared.GuidelineRatio
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.UiEntityOfText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.UiEntityOfTwoColumnText
import java.lang.ref.WeakReference

class ConverterOfExchangeRate(
    appModel: AppModel,
    private val weakResources: WeakReference<Resources?>,
    private val currencyAmounts: List<Pair<Currency, Double>>,
    private val formatter: CurrencyFormatter,
    private val twoColumnIdOfExchangeRate: Long,
    private val horizontalPaddingEntity: UiEntityOfPadding,
) : HolderOfProfile by appModel {

    private val exchangeRates: List<ExchangeRate>
        get() = profile.exchangeRates

    private val saleRate: ExchangeRate?
        get() = exchangeRates.firstOrNull(::isSale)

    private val buyRate: ExchangeRate?
        get() = exchangeRates.firstOrNull(::isBuy)

    private val paddingEntity: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_4,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_4,
        )
    }

    private fun isSale(
        rate: ExchangeRate,
    ): Boolean = Constant.TC_SALE_TYPE.equals(rate.type, ignoreCase = true)

    private fun isBuy(
        rate: ExchangeRate,
    ): Boolean = Constant.TC_BUY_TYPE.equals(rate.type, ignoreCase = true)

    fun toUiEntities(originCurrency: Currency): List<UiEntityOfTwoColumnText> {
        val currency: Currency = currencyAmounts.firstOrNull()?.first ?: return emptyList()
        return when {
            Currency.PEN == originCurrency && Currency.USD == currency -> saleRate?.let(::createEntities).orEmpty()
            Currency.USD == originCurrency && Currency.PEN == currency -> buyRate?.let(::createEntities).orEmpty()
            else -> emptyList()
        }
    }

    private fun createEntities(exchangeRate: ExchangeRate): List<UiEntityOfTwoColumnText> {

        val labelEntity = UiEntityOfText(
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_caption,
            gravity = Gravity.START,
            text = weakResources.get()?.getString(R.string.exchange_rate_type_field).orEmpty(),
        )

        val formattedRate: CharSequence = formatter.format(Currency.PEN, exchangeRate.value)
        val valueEntity = UiEntityOfText(
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_subtitle3,
            gravity = Gravity.END,
            text = formattedRate,
        )

        val entity = UiEntityOfTwoColumnText(
            paddingEntity = paddingEntity,
            entityOfColumn1 = labelEntity,
            entityOfColumn2 = valueEntity,
            guidelinePercent = GuidelineRatio.FOR_TITLE,
            id = twoColumnIdOfExchangeRate,
        )
        return listOf(entity)
    }
}
