package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfRecycler(private val collector: CollectorOfRecycler) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfRecycler> {

        val entities: List<UiEntityOfRecycler> = collector.collect()
        val adapterFactory = AdapterFactoryOfRecycler()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
