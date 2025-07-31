package pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch

import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.util.Constant

object SearchBarRendering {

    @JvmStatic
    fun renderSingleFrom(
        compounds: List<UiCompoundOfSingle<UiEntityOfMaterialSearch>>,
        appBarLayout: AppBarLayout,
        searchBar: SearchBar,
        searchView: SearchView?,
    ) {
        if (appBarLayout.visibility != View.VISIBLE) {
            appBarLayout.visibility = View.VISIBLE
        }

        val compound: UiCompoundOfSingle<UiEntityOfMaterialSearch> = compounds
            .firstOrNull() ?: return clearMaterialSearch(searchBar, searchView)

        bindSearch(searchBar, searchView, compound)
    }

    @JvmStatic
    private fun bindSearch(
        searchBar: SearchBar,
        searchView: SearchView?,
        compound: UiCompoundOfSingle<UiEntityOfMaterialSearch>,
    ) {
        val isGoingToBeVisible: Boolean = compound.visibilitySupplier.get()
        searchBar.visibility = if (isGoingToBeVisible) View.VISIBLE else View.GONE
        UiBinderOfMaterialSearch.bind(compound.entity, searchBar, searchView)
    }

    @JvmStatic
    private fun clearMaterialSearch(searchBar: SearchBar, searchView: SearchView?) {
        searchBar.visibility = View.GONE
        searchBar.hint = Constant.EMPTY

        if (searchView == null) return

        searchView.hint = Constant.EMPTY
        UiBinderOfMaterialSearch.toggleSearchViewDecoration(searchView, false)
    }
}
