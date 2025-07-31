package pe.com.scotiabank.blpm.android.client.base

import android.os.Parcelable
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling
import java.util.Deque
import java.util.concurrent.ConcurrentLinkedDeque

class ChildRegistry(
    private val parent: Coordinator,
    private val navigationItemRegistry: ItemRegistry = ItemRegistry(),
) : CoordinatorRegistry, Recycling {

    private val recycling: Recycling by lazy {
        StatefulRecycling()
    }

    private val _children: Deque<Coordinator> = ConcurrentLinkedDeque()
    override val children: Collection<Coordinator>
        get() = _children

    val childrenCount: Int
        get() = _children.size

    override val currentChild: Coordinator
        get() {
            val child: Coordinator? = _children.peekFirst()
            if (child != null) return child

            val navigationItem: Coordinator? = navigationItemRegistry.getSelectedItemIfFactoryExists()
            if (navigationItem != null) {
                return navigationItem
            }

            return parent
        }

    override val currentDeepChild: Coordinator
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

    val isParentWithTwoOrMoreChildren: Boolean
        get() = isParentWithTwoOrMoreChildren(parent)

    val isSingleChildDescendants: Boolean
        get() = isSingleChildDescendants(parent)

    private fun findCurrentDeepChild(child: Coordinator): Coordinator {
        if (child.currentChild == child) return child
        return findCurrentDeepChild(child.currentChild)
    }

    private fun isParentWithTwoOrMoreChildren(
        coordinator: Coordinator,
    ): Boolean = coordinator.children.size > 1 && coordinator.currentChild != coordinator

    private fun isSingleChildDescendants(coordinator: Coordinator): Boolean {
        if (isParentWithTwoOrMoreChildren(coordinator)) return false
        if (coordinator.currentChild == coordinator) return true
        return isSingleChildDescendants(coordinator.currentChild)
    }

    fun addChild(child: Coordinator) {
        _children.push(child)
    }

    suspend fun removeChildren() {
        _children.forEach { child -> removeChild(child) }
    }

    suspend fun removeChild(child: Coordinator) {
        if (child == parent) return
        val retrievedChild: Coordinator = _children.peekFirst() ?: return
        val isFound: Boolean = retrievedChild.id == child.id
        if (isFound.not()) return

        child.clearCoordinator()
        _children.pop()
    }
}
