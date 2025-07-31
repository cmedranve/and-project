package pe.com.scotiabank.blpm.android.ui.list.items.alertbanner

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfAlertBanner(
    private val collector: CollectorOfAlertBanner,
    private val receiver: InstanceReceiver,
) {

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        callback: Runnable?,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfAlertBanner<Any>> {

        val entities: List<UiEntityOfAlertBanner<Any>> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
            callback = callback,
        )
        val adapterFactory: AdapterFactoryOfAlertBanner<Any> = AdapterFactoryOfAlertBanner()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
