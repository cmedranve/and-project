package pe.com.scotiabank.blpm.android.client.base.products.picking

import pe.com.scotiabank.blpm.android.client.model.ProductModel
import pe.com.scotiabank.blpm.android.client.util.AmountUtil
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.FormatterUtil
import java.math.BigDecimal

class FormatterOfProductName {

    fun format(product: ProductModel): CharSequence {
        val amount: BigDecimal = AmountUtil.getformatAmount(
            product.availableAmount,
            product.productType,
            product.subProductType
        )
        val name: String? = findName(product)
        return buildTextForItem(name, product.symbolCurrency, amount)
    }

    fun findName(product: ProductModel): String? = when {
        isGoingToShowAlias(product) -> product.productAlias
        product.subProductName == null -> product.productName
        else -> product.subProductName
    }

    private fun isGoingToShowAlias(
        product: ProductModel
    ): Boolean = !isAliasNullOrEmpty(product)
            && !isTypeNullOrEmpty(product)
            && isEitherSavingOrCheckingAccount(product)

    private fun isAliasNullOrEmpty(product: ProductModel): Boolean {
        return product.productAlias.isNullOrEmpty()
    }

    private fun isTypeNullOrEmpty(product: ProductModel): Boolean {
        return product.productType.isNullOrEmpty()
    }

    fun isEitherSavingOrCheckingAccount(product: ProductModel): Boolean {
        return isSavingAccount(product) || isCheckingAccount(product)
    }

    private fun isSavingAccount(product: ProductModel): Boolean {
        return Constant.AH.equals(product.productType, ignoreCase = true)
    }

    private fun isCheckingAccount(product: ProductModel): Boolean {
        return Constant.CC.equals(product.productType, ignoreCase = true)
    }

    fun isCreditCard(product: ProductModel): Boolean {
        return Constant.TC.equals(product.productType, ignoreCase = true)
    }

    private fun buildTextForItem(
        name: String?,
        currencySymbol: String,
        amount: BigDecimal
    ): CharSequence = when {
        name.isNullOrBlank() -> buildTextFromAmountOnly(currencySymbol, amount)
        else -> buildTextFromName(name, currencySymbol, amount)
    }

    private fun buildTextFromAmountOnly(currencySymbol: String, amount: BigDecimal): CharSequence {
        val formattedAmount: String = FormatterUtil.format(amount)
        return String.format(Constant.X_X_PATTERN, currencySymbol, formattedAmount)
    }

    private fun buildTextFromName(name: String, currencySymbol: String, amount: BigDecimal): CharSequence {
        val formattedAmount: String = AmountUtil.formatBigDecimalAmount(amount)
        val amountWithCurrency: String = String.format(Constant.X_X_PATTERN, currencySymbol, formattedAmount)
        return String.format(Constant.X_X_PATTERN_LINE, name, amountWithCurrency)
    }
}
