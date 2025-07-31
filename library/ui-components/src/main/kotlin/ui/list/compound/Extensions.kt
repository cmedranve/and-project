package pe.com.scotiabank.blpm.android.ui.list.compound

import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import kotlin.math.min

@Suppress("UNCHECKED_CAST")
private fun Collection<IdentifiableUiEntity<*>>.isHoldingTheSameContentAs(
    otherCollection: Collection<IdentifiableUiEntity<*>>
): Boolean {

    if (this.size != otherCollection.size) return false

    // just to avoid an iteration overflow if any collection is modified just prior the looping
    val minSize: Int = min(this.size, otherCollection.size)
    for (i in 0 until minSize) {

        val oldItem: IdentifiableUiEntity<Any?> = this.elementAt(i)
            as? IdentifiableUiEntity<Any?>
            ?: return false

        val newItem: Any = otherCollection.elementAt(i)
        if (!oldItem.isHoldingTheSameContentAs(newItem)) return false
    }

    return true
}

fun LinkedHashMap<Long, UiCompound<*>>.isHoldingTheSameContentAs(
    otherMap: LinkedHashMap<Long, UiCompound<*>>
): Boolean {

    if (this.size != otherMap.size) return false

    for ((idOfOldCompound: Long, oldCompound: UiCompound<*>) in this.entries) {

        val newCompound: UiCompound<*> = otherMap[idOfOldCompound] ?: return false

        val oldEntities: List<IdentifiableUiEntity<*>> = oldCompound.uiEntities
        val newEntities: List<IdentifiableUiEntity<*>> = newCompound.uiEntities
        if (!oldEntities.isHoldingTheSameContentAs(newEntities)) return false
    }

    return true
}
