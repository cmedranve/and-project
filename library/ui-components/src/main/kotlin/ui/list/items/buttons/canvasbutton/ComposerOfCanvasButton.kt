package pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton

import androidx.core.util.Supplier
import com.scotiabank.canvascore.buttons.CanvasButton
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible

class ComposerOfCanvasButton(
    private val paddingEntity: UiEntityOfPadding,
    private val receiver: InstanceReceiver,
) : CanvasButtonController {

    private val itemEntities: MutableList<UiEntityOfCanvasButton<Any>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfCanvasButton<Any>> {

        val adapterFactory: AdapterFactoryOfCanvasButton<Any> = AdapterFactoryOfCanvasButton()

        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    fun add(
        id: Long,
        isEnabled: Boolean,
        text: CharSequence,
        data: Any? = null,
        type: Int = CanvasButton.PRIMARY,
    ) {
        val entity: UiEntityOfCanvasButton<Any> = UiEntityOfCanvasButton(
            paddingEntity = paddingEntity,
            isEnabled = isEnabled,
            text = text,
            receiver = receiver,
            data = data,
            type = type,
            id = id,
        )

        itemEntities.add(entity)
    }

    fun add(
        id: Long,
        isEnabled: Boolean,
        text: CharSequence,
        padding: UiEntityOfPadding,
        data: Any? = null,
        type: Int = CanvasButton.PRIMARY,
    ) {
        val entity: UiEntityOfCanvasButton<Any> = UiEntityOfCanvasButton(
            paddingEntity = padding,
            isEnabled = isEnabled,
            text = text,
            receiver = receiver,
            data = data,
            type = type,
            id = id,
        )

        itemEntities.add(entity)
    }

    override fun editCanvasButtonEnabling(id: Long, isEnabled: Boolean) {
        val entity: UiEntityOfCanvasButton<Any> = itemEntities
            .firstOrNull { entity -> id == entity.id }
            ?: return

        entity.isEnabled = isEnabled
    }

    override fun editCanvasButtonText(id: Long, text: CharSequence) {
        val entity: UiEntityOfCanvasButton<Any> = itemEntities
            .firstOrNull { entity -> id == entity.id }
            ?: return

        entity.text = text
    }

    override fun editCanvasButtonType(id: Long, type: Int) {
        val entity: UiEntityOfCanvasButton<Any> = itemEntities
            .firstOrNull { entity -> id == entity.id }
            ?: return

        entity.type = type
    }

    override fun removeCanvasButton(id: Long) {
        itemEntities.removeIf { entity -> id == entity.id }
    }
}
