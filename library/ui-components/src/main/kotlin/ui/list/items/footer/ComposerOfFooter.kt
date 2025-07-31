package pe.com.scotiabank.blpm.android.ui.list.items.footer

import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompound
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatefulRecycling
import java.util.LinkedHashMap

class ComposerOfFooter(
    private val id: Long,
    private val recycling: Recycling = StatefulRecycling(),
    private val adapterFactory: AdapterFactoryOfFooter = AdapterFactoryOfFooter(),
) {

    fun composeUiData(
        compoundsById: LinkedHashMap<Long, UiCompound<*>>,
    ): UiCompound<UiEntityOfFooter> {
        val entity = UiEntityOfFooter(compoundsById = compoundsById, id = id, recycling = recycling)
        val entities: List<UiEntityOfFooter> = listOf(entity)
        val visibilitySupplier: Supplier<Boolean> = Supplier(entity::isAnyItemVisible)
        return UiCompound(entities, adapterFactory, visibilitySupplier, id)
    }
}
