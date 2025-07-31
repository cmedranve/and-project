package pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.stable

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.AdapterFactoryOfAlertBanner
import pe.com.scotiabank.blpm.android.ui.list.items.alertbanner.UiEntityOfAlertBanner

class ComposerOfAlertBanner(private val collector: CollectorOfAlertBanner) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfAlertBanner<Any>> {

        val entities: List<UiEntityOfAlertBanner<Any>> = collector.collect()
        val adapterFactory: AdapterFactoryOfAlertBanner<Any> = AdapterFactoryOfAlertBanner()
        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
