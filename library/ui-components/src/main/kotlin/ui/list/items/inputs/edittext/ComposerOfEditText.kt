package pe.com.scotiabank.blpm.android.ui.list.items.inputs.edittext

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.tooltip.UiEntityOfToolTip
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfEditText<D: Any>(
    private val collector: CollectorOfEditText<D>,
    private val receiver: InstanceReceiver,
) : EditTextInputService {

    var entity: UiEntityOfEditText<D>? = null
        private set

    fun composeUiData(
        paddingEntity: UiEntityOfPadding,
        toolTipEntity: UiEntityOfToolTip? = null,
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfEditText<D>> {

        val entities: List<UiEntityOfEditText<D>> = collector.collect(
            paddingEntity = paddingEntity,
            receiver = receiver,
            toolTipEntity = toolTipEntity,
        )
        entity = entities.firstOrNull()
        val adapterFactory: AdapterFactoryOfEditText<D> = AdapterFactoryOfEditText()

        return UiCompound(entities, adapterFactory, visibilitySupplier)
    }

    override fun clearInput() {
        entity?.reset()
    }
}
