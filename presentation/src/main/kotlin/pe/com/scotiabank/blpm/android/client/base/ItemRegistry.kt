package pe.com.scotiabank.blpm.android.client.base

import androidx.core.util.Supplier
import java.util.concurrent.ConcurrentHashMap

class ItemRegistry {

    var defaultItemId: Long = NO_ID
    var selectedItemId: Long = defaultItemId

    private val itemFactoriesById: MutableMap<Long, Supplier<Coordinator>> = ConcurrentHashMap()
    private val itemsById: MutableMap<Long, Coordinator> = ConcurrentHashMap()

    val selectedItem: Coordinator?
        get() = itemsById[selectedItemId]

    val nonSelectedItems: Collection<Coordinator>
        get() = itemsById.filterNot(::isSelectedItem).values

    private fun isSelectedItem(
        itemById: Map.Entry<Long, Coordinator>,
    ): Boolean = selectedItemId == itemById.key

    fun getSelectedItemIfFactoryExists(): Coordinator? {
        val factory: Supplier<Coordinator> = itemFactoriesById[selectedItemId] ?: return null
        return itemsById.getOrPut(key = selectedItemId, defaultValue = factory::get)
    }

    fun addItemFactory(id: Long, factory: Supplier<Coordinator>) {
        itemFactoriesById[id] = factory
    }

    fun removeAllItems() {
        itemFactoriesById.clear()
        itemsById.clear()
        defaultItemId = NO_ID
        selectedItemId = NO_ID
    }

    suspend fun hideItem(id: Long) {
        if (selectedItemId == id) {
            selectedItemId = if (defaultItemId == id) NO_ID else defaultItemId
        }
        val child: Coordinator = itemsById.remove(id) ?: return
        child.clearCoordinator()
    }

    companion object {

        private val NO_ID: Long
            get() = -1
    }
}
