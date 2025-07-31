package pe.com.scotiabank.blpm.android.client.base.operation.currencyamount

import com.google.android.flexbox.FlexWrap
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.operation.IdentifiableEditText
import pe.com.scotiabank.blpm.android.client.base.session.entities.Currency
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.byId
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.AdapterFactoryOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.CollectorOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext.UiEntityOfEditText
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.FactoryOfFlexboxLayoutManager
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.LayoutManagerFactory
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiEntityOfRecycler
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.AdapterFactoryOfStaticChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfStaticChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip

class CollectorOfHorizontalCurrencyAmount(
    private val collectorOfSelectableCurrency: CollectorOfSelectableCurrency,
    private val collectorOfEditableAmount: CollectorOfEditText<IdentifiableEditText>,
) {

    var amountEntities: List<UiEntityOfEditText<IdentifiableEditText>> = emptyList()
        private set

    fun collect(
        paddingEntityOfHorizontal: UiEntityOfPadding,
        paddingEntityOfCurrency: UiEntityOfPadding,
        controllerOfSelectableCurrency: SelectionControllerOfChipsComponent<Currency>,
        receiver: InstanceReceiver,
        toolTipEntityOfCurrency: UiEntityOfToolTip?,
    ): List<UiEntityOfRecycler> {

        val currencyEntities: List<UiEntityOfStaticChipsComponent<Currency>> = collectorOfSelectableCurrency.collect(
            paddingEntity = paddingEntityOfCurrency,
            controllerOfSelectableCurrency = controllerOfSelectableCurrency,
            toolTipEntity = toolTipEntityOfCurrency,
        )
        val staticChipsComponentCompound = UiCompound(
            uiEntities = currencyEntities,
            factoryOfPortableAdapter = AdapterFactoryOfStaticChipsComponent(),
        )

        amountEntities = collectorOfEditableAmount.collect(
            paddingEntity = UiEntityOfPadding(),
            receiver = receiver,
        )
        val editTextCompound = UiCompound(
            uiEntities = amountEntities,
            factoryOfPortableAdapter = AdapterFactoryOfEditText(),
        )

        val compounds: List<UiCompound<*>> = listOf(staticChipsComponentCompound, editTextCompound)

        val layoutManagerFactory: LayoutManagerFactory = FactoryOfFlexboxLayoutManager(
            flexWrapToSet = FlexWrap.WRAP,
        )

        val recyclerEntity = UiEntityOfRecycler(
            paddingEntity = paddingEntityOfHorizontal,
            compoundsById = LinkedHashMap(),
            layoutManagerFactory = layoutManagerFactory,
        )
        compounds.associateByTo(destination = recyclerEntity.compoundsById, keySelector = ::byId)

        return listOf(recyclerEntity)
    }
}
