package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbuttonloading

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfCanvasButtonLoading(
    private val paddingEntity: UiEntityOfPadding,
    private val receiver: InstanceReceiver,
) : CanvasButtonLoadingController {

    private val itemEntities: MutableList<UiEntityOfCanvasButtonLoading<Any>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfCanvasButtonLoading<Any>> {

        val adapterFactory: AdapterFactoryOfCanvasButtonLoading<Any> = AdapterFactoryOfCanvasButtonLoading()

        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    override fun addForCanvasButtonLoading(
        id: Long,
        isEnabled: Boolean,
        text: String,
        data: Any?,
        state: Int,
    ) {
        val entity: UiEntityOfCanvasButtonLoading<Any> = UiEntityOfCanvasButtonLoading(
            paddingEntity = paddingEntity,
            isEnabled = isEnabled,
            text = text,
            receiver = receiver,
            data = data,
            state = state,
            id = id,
        )

        itemEntities.add(entity)
    }

    override fun editForCanvasButtonLoading(
        id: Long,
        isEnabled: Boolean,
        text: String,
        state: Int
    ) {
        val entity: UiEntityOfCanvasButtonLoading<Any> = itemEntities
            .firstOrNull { entity -> id == entity.id }
            ?: return

        entity.isEnabled = isEnabled
        entity.text = text
        entity.state = state
    }

    override fun removeForCanvasButtonLoading(id: Long) {
        itemEntities.removeIf { entity -> id == entity.id }
    }
}
