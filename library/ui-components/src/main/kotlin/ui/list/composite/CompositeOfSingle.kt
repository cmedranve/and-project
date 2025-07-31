package pe.com.scotiabank.blpm.android.ui.list.composite

import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity

interface CompositeOfSingle<E: IdentifiableUiEntity<E>> {

    val compounds: List<UiCompoundOfSingle<E>>

    suspend fun recomposeItselfIfNeeded(): List<UiCompoundOfSingle<E>>?
}
