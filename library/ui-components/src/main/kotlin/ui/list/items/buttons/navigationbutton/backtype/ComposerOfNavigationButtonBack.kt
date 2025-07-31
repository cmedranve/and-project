package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.backtype

import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton.UiEntityOfNavigationButton
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

class ComposerOfNavigationButtonBack(
    private val paddingEntity: UiEntityOfPadding,
    private val receiver: InstanceReceiver,
) : NavigationButtonBackController {

    private val itemEntities: MutableList<UiEntityOfNavigationButton<Any>> = mutableListOf()

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    ): UiCompound<UiEntityOfNavigationButton<Any>> {

        val adapterFactory: AdapterFactoryOfNavigationButtonBack<Any> = AdapterFactoryOfNavigationButtonBack()
        return UiCompound(itemEntities, adapterFactory, visibilitySupplier)
    }

    fun add(
        id: Long,
        isEnabled: Boolean,
        text: CharSequence,
        data: Any? = null,
    ) {
        val entity: UiEntityOfNavigationButton<Any> = UiEntityOfNavigationButton(
            paddingEntity = paddingEntity,
            isEnabled = isEnabled,
            text = text,
            receiver = receiver,
            data = data,
            id = id,
        )

        itemEntities.add(entity)
    }

    override fun editNavigationButtonBackEnabling(id: Long, isEnabled: Boolean) {
        val entity: UiEntityOfNavigationButton<Any> = itemEntities
            .firstOrNull { entity -> id == entity.id }
            ?: return

        entity.isEnabled = isEnabled
    }

    override fun editNavigationButtonBackText(id: Long, text: CharSequence) {
        val entity: UiEntityOfNavigationButton<Any> = itemEntities
            .firstOrNull { entity -> id == entity.id }
            ?: return

        entity.text = text
    }

    override fun removeNavigationButtonBack(id: Long) {
        itemEntities.removeIf { entity -> id == entity.id }
    }
}
