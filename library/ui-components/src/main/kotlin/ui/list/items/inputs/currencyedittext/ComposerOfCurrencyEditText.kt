package pe.com.scotiabank.blpm.android.ui.list.items.inputs.currencyedittext

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfCurrencyEditText<D: Any>(
    private val collector: CollectorOfCurrencyEditText<D>,
) {

    var entity: UiEntityOfCurrencyEditText<D>? = null
        private set

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfCurrencyEditText<D>> {

        val entities: List<UiEntityOfCurrencyEditText<D>> = collector.collect()
        entity = entities.firstOrNull()
        val adapterFactory: AdapterFactoryOfCurrencyEditText<D> = AdapterFactoryOfCurrencyEditText()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
