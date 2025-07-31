package pe.com.scotiabank.blpm.android.ui.list.items.skeleton

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfSkeleton(private val collector: CollectorOfSkeleton) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfSkeleton> {

        val entities: List<UiEntityOfSkeleton> = collector.collect()
        val adapterFactory = AdapterFactoryOfSkeleton()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
