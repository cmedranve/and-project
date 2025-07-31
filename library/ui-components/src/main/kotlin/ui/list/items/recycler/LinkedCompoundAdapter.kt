package pe.com.scotiabank.blpm.android.ui.list.items.recycler

import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.PortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity

class LinkedCompoundAdapter<E: IdentifiableUiEntity<E>>(
    val compound: UiCompound<E>,
    val adapter: PortableAdapter<E, *>,
)
