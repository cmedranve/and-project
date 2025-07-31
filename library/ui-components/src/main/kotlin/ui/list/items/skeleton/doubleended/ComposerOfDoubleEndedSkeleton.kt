package pe.com.scotiabank.blpm.android.ui.list.items.skeleton.doubleended

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfDoubleEndedSkeleton(private val collector: CollectorOfDoubleEndedSkeleton) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfDoubleEndedSkeleton> {

        val entities: List<UiEntityOfDoubleEndedSkeleton> = collector.collect()
        val adapterFactory = AdapterFactoryOfDoubleEndedSkeleton()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
