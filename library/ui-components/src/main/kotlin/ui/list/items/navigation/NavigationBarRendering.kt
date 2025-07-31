package pe.com.scotiabank.blpm.android.ui.list.items.navigation

import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle

object NavigationBarRendering {

    @JvmStatic
    fun renderSingleFrom(
        compoundsById: Map<Long, UiCompoundOfSingle<UiEntityOfNavigation>>,
        bnv: BottomNavigationView,
    ) {
        val compound: UiCompoundOfSingle<UiEntityOfNavigation> = compoundsById
            .values
            .lastOrNull() ?: return hide(bnv)
        bind(bnv, compound)
    }

    @JvmStatic
    private fun bind(bnv: BottomNavigationView, compound: UiCompoundOfSingle<UiEntityOfNavigation>) {
        val isGoingToBeVisible: Boolean = compound.visibilitySupplier.get()
        bnv.visibility = if (isGoingToBeVisible) View.VISIBLE else View.GONE
        UiBinderOfNavigation.bind(compound.entity, bnv)
    }

    @JvmStatic
    private fun hide(bnv: BottomNavigationView) {
        bnv.visibility = View.GONE
    }
}
