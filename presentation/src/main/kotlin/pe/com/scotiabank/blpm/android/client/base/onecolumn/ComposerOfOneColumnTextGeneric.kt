package pe.com.scotiabank.blpm.android.client.base.onecolumn

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.AdapterFactoryOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.CollectorOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.items.text.onecolumn.UiEntityOfOneColumnText
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfOneColumnTextGeneric(
    private val collector: CollectorOfOneColumnText,
    private val receiver: InstanceReceiver? = null,
) {

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfOneColumnText> {

        val entities: List<UiEntityOfOneColumnText> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
        )
        val adapterFactory = AdapterFactoryOfOneColumnText()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
