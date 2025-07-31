package pe.com.scotiabank.blpm.android.ui.list.items.buttons.pillbutton

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfPillButton(
    private val collector: CollectorOfPillButton,
    private val receiver: InstanceReceiver
) {

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfPillButton<Any>> {

        val entities: List<UiEntityOfPillButton<Any>> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
        )
        val adapterFactory: AdapterFactoryOfPillButton<Any> = AdapterFactoryOfPillButton()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
