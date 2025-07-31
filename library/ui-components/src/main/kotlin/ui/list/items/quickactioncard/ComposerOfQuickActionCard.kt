package pe.com.scotiabank.blpm.android.ui.list.items.quickactioncard

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfQuickActionCard<D: Any>(
    private val collector: CollectorOfQuickActionCard<D>,
    private val receiver: InstanceReceiver,
) {

    private var entities: List<UiEntityOfQuickActionCard<D>> = emptyList()
    val firstEntity: UiEntityOfQuickActionCard<D>?
        get() = entities.firstOrNull()

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfQuickActionCard<D>> {

        entities = collector.collect(paddingEntity, receiver)
        val adapterFactory: AdapterFactoryOfQuickActionCard<D> = AdapterFactoryOfQuickActionCard()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

}
