package pe.com.scotiabank.blpm.android.ui.list.items.selectors.dropdown

import androidx.core.util.Consumer
import com.scotiabank.canvascore.selectors.Dropdown
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.selectors.EventOfSelectionController
import java.util.concurrent.ConcurrentHashMap

class SelectionControllerOfDropdownSelector<D : Any>(
    private val instanceReceiver: InstanceReceiver
) {

    var selectorEntity: UiEntityOfDropdownSelector<D>? = null
        private set
    private val itemEntities:List<UiEntityOfDropdownSelectorItem<D>>?
        get() = selectorEntity?.itemEntities

    private var positionOfDefaultItem = INVALID_POSITION
    private val defaultItem: UiEntityOfDropdownSelectorItem<D>?
        get() = itemEntities?.getOrNull(positionOfDefaultItem)

    private val selectedPositionBySingleKey: MutableMap<Int, Int?> = ConcurrentHashMap()
    val positionOfSelectedItem: Int
        get() = selectedPositionBySingleKey[SINGLE_KEY] ?: INVALID_POSITION

    val selectedItem: UiEntityOfDropdownSelectorItem<D>?
        get() = itemEntities?.getOrNull(positionOfSelectedItem)

    private val isInitializing: Boolean
        get() = selectedItem == null && defaultItem == null

    internal val dropdownCallbacks: Dropdown.Callbacks by lazy {
        val onItemClicked: Consumer<Int> = Consumer(::handleItemClicked)
        ProxyOfDropdownCallbacks(onItemClicked)
    }

    fun setSelectorEntity(selectorEntity: UiEntityOfDropdownSelector<D>) {
        this.selectorEntity = selectorEntity
    }

    fun setDefaultItem(itemEntity: UiEntityOfDropdownSelectorItem<D>) {
        val isTheSame: Boolean = isTheSameAsTheSelectedItem(itemEntity)
        if (isTheSame) return

        val position: Int = itemEntities
            ?.indexOfFirst { itemEntityUnderEvaluation -> itemEntity.id == itemEntityUnderEvaluation.id }
            ?: return
        val itemEntityFound: UiEntityOfDropdownSelectorItem<D> = itemEntities
            ?.getOrNull(position)
            ?: return

        val isInitializing: Boolean = isInitializing
        selectedItem?.let(::dropFromSelection)
        addToSelection(itemEntityFound, position)
        positionOfDefaultItem = position
        if (isInitializing) return

        instanceReceiver.receive(EventOfSelectionController.NEW_DEFAULT)
    }

    private fun isTheSameAsTheSelectedItem(itemEntity: UiEntityOfDropdownSelectorItem<D>): Boolean {
        val idOfSelectedItem: Long? = selectedItem?.id
        return idOfSelectedItem != null && itemEntity.id == idOfSelectedItem
    }

    private fun dropFromSelection(previousSelectedItem: UiEntityOfDropdownSelectorItem<D>) {
        previousSelectedItem.mutableIsSelected = false
    }

    private fun addToSelection(itemEntity: UiEntityOfDropdownSelectorItem<D>, position: Int) {
        itemEntity.mutableIsSelected = true
        selectedPositionBySingleKey[SINGLE_KEY] = position
    }

    private fun handleItemClicked(position: Int) {
        if (position == positionOfSelectedItem) return
        val itemEntity: UiEntityOfDropdownSelectorItem<D> = itemEntities?.getOrNull(position) ?: return

        selectedItem?.let(::dropFromSelection)
        addToSelection(itemEntity, position)
        instanceReceiver.receive(itemEntity)
    }

    fun reset() {
        if (isInitializing) return
        val isTheSame: Boolean = defaultItem?.let(::isTheSameAsTheSelectedItem) ?: false
        if (isTheSame) return

        selectedItem?.let(::dropFromSelection)
        defaultItem?.let { item-> addToSelection(item, positionOfDefaultItem) }
        instanceReceiver.receive(EventOfSelectionController.RESET)
    }

    companion object {

        internal const val INVALID_POSITION = -1
        private const val SINGLE_KEY = 0
    }
}
