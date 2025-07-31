package pe.com.scotiabank.blpm.android.client.base.calltoaction

import android.content.res.Resources
import androidx.core.util.Supplier
import com.scotiabank.canvascore.buttons.CanvasButton
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.AdapterFactoryOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.canvasbutton.UiEntityOfCanvasButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference

class ComposerOfCallToAction(
    private val weakResources: WeakReference<Resources?>,
    private val paddingEntity: UiEntityOfPadding,
    private val receiver: InstanceReceiver,
): CallToActionService {

    private val itemEntities: MutableList<UiEntityOfCanvasButton<Any>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
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

    override fun add(callToAction: CallToAction) {

        val entity: UiEntityOfCanvasButton<Any> = UiEntityOfCanvasButton(
            paddingEntity = paddingEntity,
            isEnabled = true,
            text = weakResources.get()?.getString(callToAction.buttonLabel).orEmpty(),
            receiver = receiver,
            data = callToAction,
            type = callToAction.type,
            id = callToAction.id,
        )

        itemEntities.add(entity)
    }
}
