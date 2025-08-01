package pe.com.scotiabank.blpm.android.client.newdashboard.mylist.summary

import androidx.core.util.Predicate
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.client.base.operation.frequent.FrequentOperationType
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.AdapterFactoryOfStaticChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.SelectionControllerOfChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfChip
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.chips.UiEntityOfStaticChipsComponent
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound

class ComposerOfFrequentOperationType(
    private val collector: CollectorOfFrequentOperationType,
    receiver: InstanceReceiver,
) {

    val controller: SelectionControllerOfChipsComponent<FrequentOperationType> = SelectionControllerOfChipsComponent(
        instanceReceiver = receiver,
    )
    val selectedChip: UiEntityOfChip<FrequentOperationType>?
        get() = controller.selectedChip

    val selectedType: FrequentOperationType?
        get() = selectedChip?.data

    val visibilityPredicate: Predicate<FrequentOperationType> = Predicate(::isTypeSelected)

    val chipEntitiesByText: Map<String, UiEntityOfChip<FrequentOperationType>>
        get() = collector.chipEntitiesByText

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompound<UiEntityOfStaticChipsComponent<FrequentOperationType>> {

        val entities: List<UiEntityOfStaticChipsComponent<FrequentOperationType>> = collector.collect(
            controller = controller,
        )
        val adapterFactory: AdapterFactoryOfStaticChipsComponent<FrequentOperationType> = AdapterFactoryOfStaticChipsComponent()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    private fun isTypeSelected(type: FrequentOperationType): Boolean = type == selectedType
}