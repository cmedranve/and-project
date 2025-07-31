package pe.com.scotiabank.blpm.android.ui.list.items.card

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfCard(private val collector: CollectorOfCard) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfCard<Any>> {

        val entities: List<UiEntityOfCard<Any>> = collector.collect()
        val adapterFactory: AdapterFactoryOfCard<Any> = AdapterFactoryOfCard()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
