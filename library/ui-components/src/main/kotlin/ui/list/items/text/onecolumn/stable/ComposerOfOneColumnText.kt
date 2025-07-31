package pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.stable

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText

class ComposerOfOneColumnText(private val collector: CollectorOfOneColumnText) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfOneColumnText> {

        val entities: List<UiEntityOfOneColumnText> = collector.collect()
        val adapterFactory = AdapterFactoryOfOneColumnText()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
