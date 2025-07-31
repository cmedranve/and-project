package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.single

import com.google.android.gms.common.util.BiConsumer
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.EventOfSelectionController
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.ControllerOfSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import java.util.concurrent.ConcurrentHashMap

class ControllerOfSingleSelection<D: Any>(
    private val instanceReceiver: InstanceReceiver
) : ControllerOfSelection<D>() {

    private var defaultItemEntity: UiEntityOfCheckable<D>? = null

    private val selectedItemBySingleKey: MutableMap<Int, UiEntityOfCheckable<D>?> = ConcurrentHashMap()
    val selectedItem: UiEntityOfCheckable<D>?
        get() = selectedItemBySingleKey[SINGLE_KEY]

    private val isInitializing: Boolean
        get() = selectedItem == null && defaultItemEntity == null

    override val onCheckedChange: BiConsumer<UiEntityOfCheckable<D>, Boolean> by lazy {
        BiConsumer(::handleCheckingChange)
    }

    fun setDefaultItem(itemEntity: UiEntityOfCheckable<D>) {
        val isTheSame: Boolean = isTheSameAsTheSelectedItem(itemEntity)
        if (isTheSame) return

        val isInitializing: Boolean = isInitializing
        selectedItem?.let(::dropFromSelection)
        addToSelection(itemEntity)
        defaultItemEntity = itemEntity
        if (isInitializing) return

        instanceReceiver.receive(EventOfSelectionController.NEW_DEFAULT)
    }

    private fun isTheSameAsTheSelectedItem(itemEntity: UiEntityOfCheckable<D>): Boolean {
        val idOfSelectedItem: Long? = selectedItem?.id
        return idOfSelectedItem != null && itemEntity.id == idOfSelectedItem
    }

    fun dropFromSelection(previousSelectedItem: UiEntityOfCheckable<D>) {
        previousSelectedItem.mutableIsChecked = false
        selectedItemBySingleKey.remove(SINGLE_KEY)
        previousSelectedItem.reset()
    }

    fun addToSelection(itemEntity: UiEntityOfCheckable<D>) {
        itemEntity.mutableIsChecked = true
        selectedItemBySingleKey[SINGLE_KEY] = itemEntity
    }

    private fun handleCheckingChange(itemEntity: UiEntityOfCheckable<D>, isChecked: Boolean) {
        if (!isChecked) return

        val isTheSame: Boolean = isTheSameAsTheSelectedItem(itemEntity)
        if (isTheSame) return

        selectedItem?.let(::dropFromSelection)
        addToSelection(itemEntity)
        instanceReceiver.receive(itemEntity)
    }

    fun reset() {
        if (isInitializing) return
        val isTheSame: Boolean = defaultItemEntity?.let(::isTheSameAsTheSelectedItem) ?: false
        if (isTheSame) return

        selectedItem?.let(::dropFromSelection)
        defaultItemEntity?.let(::addToSelection)
        instanceReceiver.receive(EventOfSelectionController.RESET)
    }

    companion object {

        private const val SINGLE_KEY = 0
    }
}
