package pe.com.scotiabank.blpm.android.ui.list.compound

import androidx.viewbinding.ViewBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.PortableAdapter
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity

object Submitter {

    @JvmStatic
    fun <E: IdentifiableUiEntity<E>> submit(
        compound: UiCompound<E>,
        adapter: PortableAdapter<E, out ViewBinding>,
    ) {
        val isGoingToBeVisible: Boolean = compound.visibilitySupplier.get()
        val uiEntities: List<E> = if (isGoingToBeVisible) compound.uiEntities.toList() else emptyList()
        adapter.submitList(uiEntities)
    }
}
