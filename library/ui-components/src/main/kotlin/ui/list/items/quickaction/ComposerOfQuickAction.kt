package pe.com.scotiabank.blpm.android.ui.list.items.quickaction

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfQuickAction(
    private val collector: CollectorOfQuickAction,
    private val receiver: InstanceReceiver,
) {

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfQuickAction<Any>> {

        val entities: List<UiEntityOfQuickAction<Any>> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
        )
        val adapterFactory: AdapterFactoryOfQuickAction<Any> = AdapterFactoryOfQuickAction()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
