package pe.com.scotiabank.blpm.android.ui.list.items.image.onecolumn

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfOneColumnImage(
    private val collector: CollectorOfOneColumnImage,
    private val receiver: InstanceReceiver? = null,
) {

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfOneColumnImage> {

        val entities: List<UiEntityOfOneColumnImage> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
        )
        val adapterFactory = AdapterFactoryOfOneColumnImage()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
