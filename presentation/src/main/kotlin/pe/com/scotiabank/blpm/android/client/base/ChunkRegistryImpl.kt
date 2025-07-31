package pe.com.scotiabank.blpm.android.client.base

import android.os.Parcelable
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling
import java.util.Deque
import java.util.concurrent.ConcurrentLinkedDeque

class ChunkRegistryImpl(
    private val parent: Chunk,
    private val chunkItemRegistry: ChunkItemRegistry = ChunkItemRegistry(),
) : ChunkRegistry, Recycling {

    private val recycling: Recycling by lazy {
        StatefulRecycling()
    }

    private val _children: Deque<Chunk> = ConcurrentLinkedDeque()
    override val children: Collection<Chunk>
        get() = _children

    val childrenCount: Int
        get() = _children.size

    override val currentChild: Chunk
        get() {
            val child: Chunk? = _children.peekFirst()
            if (child != null) return child

            val chunkItem: Chunk? = chunkItemRegistry.getSelectedItemIfFactoryExists()
            if (chunkItem != null) {
                return chunkItem
            }

            return parent
        }

    override val currentDeepChild: Chunk
        get() = findCurrentDeepChild(currentChild)

    override var recyclingState: Parcelable?
        get() = if (currentChild == parent) recycling.recyclingState else currentChild.recyclingState
        set(value) {
            if (currentChild == parent) {
                recycling.recyclingState = value
            } else {
                currentChild.recyclingState = value
            }
        }

    private fun findCurrentDeepChild(child: Chunk): Chunk {
        if (child.currentChild == child) return child
        return findCurrentDeepChild(child.currentChild)
    }

    fun addChild(child: Chunk) {
        _children.push(child)
    }
}
