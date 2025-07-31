package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.products.picking.radiobutton.ProductGroup
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency

class CarrierFromPickingConsumer(
    val titleText: String,
    val currencyAmountLabel: CharSequence,
    val currencyAmounts: List<Pair<Currency, Double>>,
    val productGroupLabel: CharSequence,
    val productGroup: ProductGroup,
    val data: Any,
) {

    class Factory(
        val pickingConsumerScope: CoroutineScope,
        val isPayableWithCreditCard: Boolean,
        private val titleText: String,
        private val currencyAmountLabel: CharSequence,
        private val currencyAmounts: List<Pair<Currency, Double>>,
        private val productGroupLabel: CharSequence,
        private val data: Any,
    ) {

        fun create(productGroup: ProductGroup): CarrierFromPickingConsumer = CarrierFromPickingConsumer(
            titleText = titleText,
            currencyAmountLabel = currencyAmountLabel,
            currencyAmounts = currencyAmounts,
            productGroupLabel = productGroupLabel,
            productGroup = productGroup,
            data = data,
        )
    }
}
