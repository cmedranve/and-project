package pe.com.scotiabank.blpm.android.ui.list.items.buddytip.stable

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.AdapterFactoryOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip

class ComposerOfBuddyTip(
    private val collector: CollectorOfBuddyTip,
) {

    var entity: UiEntityOfBuddyTip? = null
        private set

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfBuddyTip> {

        val entities: List<UiEntityOfBuddyTip> = collector.collect()
        entity = entities.firstOrNull()
        val adapterFactory = AdapterFactoryOfBuddyTip()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
