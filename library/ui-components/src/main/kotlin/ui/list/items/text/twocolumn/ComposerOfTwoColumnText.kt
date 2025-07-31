package pe.com.scotiabank.blpm.android.ui.list.items.text.twocolumn

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfTwoColumnText(
    private val collector: CollectorOfTwoColumnText,
    private val receiver: InstanceReceiver? = null,
) {

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfTwoColumnText> {

        val entities: List<UiEntityOfTwoColumnText> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
        )
        val adapterFactory = AdapterFactoryOfTwoColumnText()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
