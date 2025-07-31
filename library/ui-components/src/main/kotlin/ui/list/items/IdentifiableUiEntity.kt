package pe.com.scotiabank.blpm.android.ui.list.items

import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling

interface IdentifiableUiEntity<E>: Identifiable, Resettable, ChangingState, Recycling {

    fun isHoldingTheSameContentAs(other: E): Boolean
}
