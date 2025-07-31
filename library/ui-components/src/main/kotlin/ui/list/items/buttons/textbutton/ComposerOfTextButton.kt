package pe.com.scotiabank.blpm.android.ui.list.items.buttons.textbutton

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfTextButton(
    private val collector: CollectorOfTextButton,
    private val receiver: InstanceReceiver,
) {

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfTextButton<Any>> {

        val entities: List<UiEntityOfTextButton<Any>> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
        )
        val adapterFactory: AdapterFactoryOfTextButton<Any> = AdapterFactoryOfTextButton()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }
}
