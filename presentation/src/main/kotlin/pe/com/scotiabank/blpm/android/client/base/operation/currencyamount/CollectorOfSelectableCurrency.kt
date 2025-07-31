package pe.com.scotiabank.blpm.android.client.base.operation.currencyamount

import android.content.res.Resources
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfStaticChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import java.lang.ref.WeakReference

class CollectorOfSelectableCurrency(
    private val currencies: Collection<Currency>,
    private val defaultCurrency: Currency,
    private val weakResources: WeakReference<Resources?>,
) {

    fun collect(
        paddingEntity: UiEntityOfPadding,
        controllerOfSelectableCurrency: SelectionControllerOfChipsComponent<Currency>,
        toolTipEntity: UiEntityOfToolTip? = null,
    ): List<UiEntityOfStaticChipsComponent<Currency>> {

        val chipEntitiesByText: LinkedHashMap<String, UiEntityOfChip<Currency>> = LinkedHashMap()
        currencies.associateByTo(chipEntitiesByText, ::bySymbolAsKey, ::toChipEntity)

        val chipEntityToBeDefault: UiEntityOfChip<Currency> = chipEntitiesByText
            .firstNotNullOfOrNull(::findDefaultCurrency)
            ?: return emptyList()

        val collectionEntity = UiEntityOfStaticChipsComponent(
            paddingEntity = paddingEntity,
            controller = controllerOfSelectableCurrency,
            _chipEntitiesByChipText = chipEntitiesByText,
            isSelectionRequired = true,
            title = weakResources.get()?.getString(R.string.currency),
            toolTipEntity = toolTipEntity,
        )
        controllerOfSelectableCurrency.setComponentEntity(collectionEntity)
        controllerOfSelectableCurrency.setDefaultChip(chipEntityToBeDefault)

        return listOf(collectionEntity)
    }

    private fun bySymbolAsKey(currency: Currency): String = currency.symbol

    private fun toChipEntity(
        currency: Currency,
    ): UiEntityOfChip<Currency> = UiEntityOfChip(currency.symbol, currency)

    private fun findDefaultCurrency(
        entry: Map.Entry<String, UiEntityOfChip<Currency>>,
    ): UiEntityOfChip<Currency>? {
        val entity: UiEntityOfChip<Currency> = entry.value
        if (defaultCurrency == entity.data) return entity
        return null
    }
}
