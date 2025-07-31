package pe.com.scotiabank.blpm.android.ui.list.items.inputs.centeredcurrencyedittext

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfCenteredCurrencyEditText<D: Any>(
    private val collector: CollectorOfCenteredCurrencyEditText<D>,
) {

    var entity: UiEntityOfCenteredCurrencyEditText<D>? = null
        private set

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfCenteredCurrencyEditText<D>> {

        val entities: List<UiEntityOfCenteredCurrencyEditText<D>> = collector.collect()
        entity = entities.firstOrNull()
        val adapterFactory: AdapterFactoryOfCenteredCurrencyEditText<D> = AdapterFactoryOfCenteredCurrencyEditText()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
