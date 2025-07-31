package pe.com.scotiabank.blpm.android.ui.list.items.inputs.password

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfPassword<D: Any>(
    private val collector: CollectorOfPassword<D>,
) {

    var firstEntity: UiEntityOfPassword<D>? = null
        private set

    var secondEntity: UiEntityOfPassword<D>? = null
        private set

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfPassword<D>> {

        val entities: List<UiEntityOfPassword<D>> = collector.collect()
        firstEntity = entities.getOrNull(0)
        secondEntity = entities.getOrNull(1)
        val adapterFactory: AdapterFactoryOfPassword<D> = AdapterFactoryOfPassword()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
