package pe.com.scotiabank.blpm.android.client.base.operation.currencyamount

import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.operation.IdentifiableEditText
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.AdapterFactoryOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler

class ComposerOfHorizontalCurrencyAmount(
    private val collector: CollectorOfHorizontalCurrencyAmount,
    private val receiver: InstanceReceiver,
) {

    private val controllerOfCurrency: SelectionControllerOfChipsComponent<Currency> = SelectionControllerOfChipsComponent(
        instanceReceiver = receiver,
    )
    val selectedCurrencyEntity: UiEntityOfChip<Currency>?
        get() = controllerOfCurrency.selectedChip

    val amountEntity: UiEntityOfEditText<IdentifiableEditText>?
        get() = collector.amountEntities.firstOrNull()

    fun composeUiData(
        paddingEntityOfHorizontal: UiEntityOfPadding,
        paddingEntityOfCurrency: UiEntityOfPadding,
        toolTipEntityOfCurrency: UiEntityOfToolTip? = null,
    ): UiCompound<UiEntityOfRecycler> {

        val entities: List<UiEntityOfRecycler> = collector.collect(
            paddingEntityOfHorizontal = paddingEntityOfHorizontal,
            paddingEntityOfCurrency = paddingEntityOfCurrency,
            controllerOfSelectableCurrency = controllerOfCurrency,
            receiver = receiver,
            toolTipEntityOfCurrency = toolTipEntityOfCurrency,
        )
        val adapterFactory = AdapterFactoryOfRecycler()
        return UiCompound(entities, adapterFactory)
    }
}
