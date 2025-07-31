package pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.stable

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.AdapterFactoryOfTwoColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn.UiEntityOfTwoColumnText

class ComposerOfTwoColumnText(
    private val collector: CollectorOfTwoColumnText,
) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfTwoColumnText> {

        val entities: List<UiEntityOfTwoColumnText> = collector.collect()
        val adapterFactory = AdapterFactoryOfTwoColumnText()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

}
