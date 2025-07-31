package pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.stable

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.AdapterFactoryOfTextButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton.UiEntityOfTextButton

class ComposerOfTextButton(private val collector: CollectorOfTextButton) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfTextButton<Any>> {

        val entities: List<UiEntityOfTextButton<Any>> = collector.collect()
        val adapterFactory: AdapterFactoryOfTextButton<Any> = AdapterFactoryOfTextButton()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
