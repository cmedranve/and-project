package pe.com.scotiabank.blpm.android.ui.list.compound

import pe.com.scotiabank.blpm.android.ui.list.items.Identifiable

fun toUiEntities(compound: UiCompound<*>): List<*> = compound.uiEntities

fun toUiEntities(compoundsById: Map.Entry<Long, UiCompound<*>>): List<*> {
    val compound: UiCompound<*> = compoundsById.value
    return compound.uiEntities
}

fun byId(identifiable: Identifiable): Long = identifiable.id
