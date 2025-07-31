package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import pe.com.scotiabank.blpm.android.client.base.onecolumn.FactoryOfOneColumnTextEntity
import pe.com.scotiabank.blpm.android.client.base.operation.currencyamount.CurrencyFormatter
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable.CollectorOfOneColumnText

class CollectorOfHeader(
    private val currencyAmountLabel: CharSequence,
    private val currencyAmounts: List<Pair<Currency, Double>>,
    private val formatter: CurrencyFormatter,
    private val productGroupLabel: CharSequence,
    private val factory: FactoryOfOneColumnTextEntity,
    private val horizontalPaddingEntity: UiEntityOfPadding,
) : CollectorOfOneColumnText {

    private val paddingEntityForCurrencyAmountLabel: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_4,
        )
    }

    private val paddingEntityForCurrencyAmount: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_4,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_24,
        )
    }

    private val paddingEntityForProductGroupLabel: UiEntityOfPadding by lazy {
        UiEntityOfPadding(
            left = horizontalPaddingEntity.left,
            right = horizontalPaddingEntity.right,
            top = com.scotiabank.canvascore.R.dimen.canvascore_margin_4,
            bottom = com.scotiabank.canvascore.R.dimen.canvascore_margin_4,
        )
    }

    override fun collect(): List<UiEntityOfOneColumnText> {

        val entityForCurrencyAmountLabel: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForCurrencyAmountLabel,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_caption,
            text = currencyAmountLabel,
        )

        val currencyAmountToBeBold: CharSequence = currencyAmounts.joinToString(
            separator = Constant.SPACE_WHITE + Constant.AND + Constant.SPACE_WHITE,
            transform = ::toCurrencyAmount,
        )
        val entityForCurrencyAmount: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForCurrencyAmount,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_headline_small_black,
            text = currencyAmountToBeBold,
        )

        val entityForProductGroupLabel: UiEntityOfOneColumnText = factory.create(
            paddingEntity = paddingEntityForProductGroupLabel,
            appearance = com.scotiabank.canvascore.R.style.canvascore_style_subtitle3,
            text = productGroupLabel,
        )

        return listOf(entityForCurrencyAmountLabel, entityForCurrencyAmount, entityForProductGroupLabel)
    }

    private fun toCurrencyAmount(money: Pair<Currency, Double>): CharSequence = formatter.format(
        currency = money.first,
        amount = money.second,
    )
}
