package pe.com.scotiabank.blpm.android.ui.list.items.avatar.leading

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfLeadingAvatar(private val collector: CollectorOfLeadingAvatar) {

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfLeadingAvatar<Any>> {

        val entities: List<UiEntityOfLeadingAvatar<Any>> = collector.collect()
        val adapterFactory: AdapterFactoryOfLeadingAvatar<Any> = AdapterFactoryOfLeadingAvatar()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
