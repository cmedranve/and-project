package pe.com.scotiabank.blpm.android.ui.list.items.image.doubleended

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfDoubleEndedImage(private val collector: CollectorOfDoubleEndedImage) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfDoubleEndedImage<Any>> {

        val entities: List<UiEntityOfDoubleEndedImage<Any>> = collector.collect()
        val adapterFactory: AdapterFactoryOfDoubleEndedImage<Any> = AdapterFactoryOfDoubleEndedImage()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
