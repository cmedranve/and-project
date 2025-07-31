package pe.com.scotiabank.blpm.android.client.base.products.newpicking

import kotlinx.coroutines.CoroutineScope
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.client.model.ProductModel

class CarrierOfProductPicked(
    val productPickingScope: CoroutineScope,
    val currencyAmounts: List<Pair<Currency, Double>>,
    val data: Any,
    val productPicked: ProductModel,
    val numberOfInstallments: Int,
)
