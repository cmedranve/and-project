package pe.com.scotiabank.blpm.android.client.base.products.newpicking.exchangerate

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.AdapterFactoryOfTwoColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.UiEntityOfTwoColumnText

class ComposerOfExchangeRate(
    private val converter: ConverterOfExchangeRate,
) : ExchangeRateService {

    private val entities: MutableList<UiEntityOfTwoColumnText> = mutableListOf()

    fun composeUiData(visibilitySupplier: Supplier<Boolean>): UiCompound<UiEntityOfTwoColumnText> {
        val adapterFactory = AdapterFactoryOfTwoColumnText()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun clearThenAddExchangeRate(originCurrency: Currency) {
        entities.clear()
        val newEntities: List<UiEntityOfTwoColumnText> = converter.toUiEntities(originCurrency)
        entities.addAll(newEntities)
    }
}
