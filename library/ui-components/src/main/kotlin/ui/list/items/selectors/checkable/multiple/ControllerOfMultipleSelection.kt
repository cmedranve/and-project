package pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.multiple

import com.google.android.gms.common.util.BiConsumer
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.EventOfSelectionController
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.ControllerOfSelection
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.checkable.UiEntityOfCheckable
import java.util.concurrent.ConcurrentHashMap

class ControllerOfMultipleSelection<D: Any>(
    private val instanceReceiver: InstanceReceiver
) : ControllerOfSelection<D>() {

    private var defaultItemEntities: Collection<UiEntityOfCheckable<D>> = emptyList()
    private val mutableItemSelection: MutableSet<UiEntityOfCheckable<D>> = ConcurrentHashMap.newKeySet()
    val itemSelection: Collection<UiEntityOfCheckable<D>>
        get() = mutableItemSelection

    private val isInitializing: Boolean
        get() = mutableItemSelection.isEmpty() && defaultItemEntities.isEmpty()

    override val onCheckedChange: BiConsumer<UiEntityOfCheckable<D>, Boolean> by lazy {
        BiConsumer(::handleCheckingChange)
    }

    fun setDefaultItems(itemEntities: Collection<UiEntityOfCheckable<D>>) {
        if (itemEntities.isEmpty()) return

        val isInitializing: Boolean = isInitializing
        mutableItemSelection.forEach(::dropFromSelection)
        itemEntities.forEach(::addToSelection)

        defaultItemEntities = itemEntities
        mutableItemSelection.clear()
        mutableItemSelection.addAll(itemEntities)
        if (isInitializing) return

        instanceReceiver.receive(EventOfSelectionController.NEW_DEFAULT)
    }

    private fun handleCheckingChange(itemEntity: UiEntityOfCheckable<D>, isChecked: Boolean) {
        itemEntity.mutableIsChecked = isChecked
        val isFound: Boolean = mutableItemSelection.any { mutableItem -> mutableItem.id == itemEntity.id }
        if (isFound) {
            dropFromSelection(itemEntity)
        } else {
            addToSelection(itemEntity)
        }
        instanceReceiver.receive(itemEntity)
    }

    fun dropFromSelection(previousSelectedItem: UiEntityOfCheckable<D>) {
        previousSelectedItem.mutableIsChecked = false
        mutableItemSelection.remove(previousSelectedItem)
        previousSelectedItem.reset()
    }

    fun addToSelection(itemEntity: UiEntityOfCheckable<D>) {
        itemEntity.mutableIsChecked = true
        mutableItemSelection.add(itemEntity)
    }

    fun reset() {
        if (isInitializing) return

        mutableItemSelection.forEach(::dropFromSelection)
        defaultItemEntities.forEach(::addToSelection)

        instanceReceiver.receive(EventOfSelectionController.RESET)
    }
}
