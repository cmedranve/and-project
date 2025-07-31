package pe.com.scotiabank.blpm.android.ui.list.compound

import androidx.core.util.Supplier
import androidx.viewbinding.ViewBinding
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.FactoryOfPortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.items.Identifiable
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity

class UiCompound<E: IdentifiableUiEntity<E>>(
    val uiEntities: List<E>,
    val factoryOfPortableAdapter: FactoryOfPortableAdapter<E, out ViewBinding>,
    val visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
    override val id: Long = randomLong(),
) : Identifiable
