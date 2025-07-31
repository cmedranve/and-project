package pe.com.scotiabank.blpm.android.ui.list.items

import kotlin.math.min

internal fun <E: IdentifiableUiEntity<E>> E?.isNullableEntityTheSameAs(
    other: E?
): Boolean = when {
    this == null && other == null -> true
    this != null && other != null -> this.isHoldingTheSameContentAs(other)
    else -> false
}

fun <E: IdentifiableUiEntity<E>> Collection<E>.isHoldingTheSameContentAs(
    otherCollection: Collection<E>
): Boolean {

    if (this.size != otherCollection.size) return false

    // just to avoid an iteration overflow if any collection is modified just prior the looping
    val minSize: Int = min(this.size, otherCollection.size)
    for (i in 0 until minSize) {
        val oldItem: E = this.elementAt(i)
        val newItem: E = otherCollection.elementAt(i)
        if (!oldItem.isHoldingTheSameContentAs(newItem)) return false
    }

    return true
}

fun <K: Any, E: IdentifiableUiEntity<E>> LinkedHashMap<K, E>.isHoldingTheSameContentAs(
    otherMap: LinkedHashMap<K, E>
): Boolean {

    if (this.size != otherMap.size) return false

    val oldEntries: Set<Map.Entry<K, E>> = this.entries
    val newEntries: Set<Map.Entry<K, E>> = otherMap.entries

    // just to avoid an iteration overflow if any map is modified just prior the looping
    val minSize: Int = min(oldEntries.size, newEntries.size)

    for (i in 0 until minSize) {
        val keyOfOldItem: K = oldEntries.elementAt(i).key
        val keyOfNewItem: K = newEntries.elementAt(i).key
        if (keyOfOldItem != keyOfNewItem) return false

        val valueOfOldItem: E = oldEntries.elementAt(i).value
        val valueOfNewItem: E = newEntries.elementAt(i).value
        if (!valueOfOldItem.isHoldingTheSameContentAs(valueOfNewItem)) return false
    }

    return true
}
