package pe.com.scotiabank.blpm.android.client.base

import androidx.core.util.Supplier
import java.util.concurrent.ConcurrentHashMap

class ChunkItemRegistry {

    var defaultItemId: Long = NO_ID
    var selectedItemId: Long = defaultItemId

    private val itemFactoriesById: MutableMap<Long, Supplier<Chunk>> = ConcurrentHashMap()
    private val itemsById: MutableMap<Long, Chunk> = ConcurrentHashMap()

    val selectedItem: Chunk?
        get() = itemsById[selectedItemId]

    val nonSelectedItems: Collection<Chunk>
        get() = itemsById.filterNot(::isSelectedItem).values

    private fun isSelectedItem(
        itemById: Map.Entry<Long, Chunk>,
    ): Boolean = selectedItemId == itemById.key

    fun getSelectedItemIfFactoryExists(): Chunk? {
        val factory: Supplier<Chunk> = itemFactoriesById[selectedItemId] ?: return null
        return itemsById.getOrPut(key = selectedItemId, defaultValue = factory::get)
    }

    fun addItemFactory(id: Long, factory: Supplier<Chunk>) {
        itemFactoriesById[id] = factory
    }

    fun removeAllItems() {
        itemFactoriesById.clear()
        itemsById.clear()
        defaultItemId = NO_ID
        selectedItemId = NO_ID
    }

    companion object {

        private val NO_ID: Long
            get() = -1
    }
}
