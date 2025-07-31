package pe.com.scotiabank.blpm.android.ui.list.compound

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.items.Identifiable
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity

class UiCompoundOfSingle<E: IdentifiableUiEntity<E>>(
    val entity: E,
    val visibilitySupplier: Supplier<Boolean>,
) : Identifiable by entity
