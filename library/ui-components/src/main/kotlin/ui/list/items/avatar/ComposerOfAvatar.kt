package pe.com.scotiabank.blpm.android.ui.list.items.avatar

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfAvatar(
    private val collector: CollectorOfAvatar,
    private val receiver: InstanceReceiver? = null,
) {

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfAvatar> {

        val entities: List<UiEntityOfAvatar> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
        )
        val adapterFactory = AdapterFactoryOfAvatar()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
