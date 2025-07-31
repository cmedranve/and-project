package pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch

import android.content.res.ColorStateList
import android.view.View
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Supplier
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

object UiBinderOfMaterialSearch {

    @JvmStatic
    fun bind(entity: UiEntityOfMaterialSearch, searchBar: SearchBar, searchView: SearchView?) {
        bindSearchBar(entity, searchBar)
        bindSearchView(entity,searchView)
    }

    @JvmStatic
    private fun bindSearchBar(entity: UiEntityOfMaterialSearch, searchBar: SearchBar) {
        searchBar.hint = entity.hintTextForSearchBar

        if (ResourcesCompat.ID_NULL == entity.textAppearanceForSearchBar) return
        searchBar.setTitleTextAppearance(searchBar.context, entity.textAppearanceForSearchBar)
    }

    @JvmStatic
    private fun bindSearchView(entity: UiEntityOfMaterialSearch, searchView: SearchView?) {
        if (searchView == null) return

        searchView.hint = entity.hintTextForSearchView
        if (ResourcesCompat.ID_NULL != entity.textAppearanceForSearchView) {
            searchView.editText.setTextAppearance(entity.textAppearanceForSearchView)
        }

        bindIfDifferent(entity.isEnabledInputSearchView, searchView.editText::isEnabled, searchView.editText::setEnabled)
        if (entity.isEnabledInputSearchView) {
            toggleSearchViewDecoration(searchView, true)
            bindCallbacks(entity, searchView.editText)
            return
        }
        toggleSearchViewDecoration(searchView, false)
    }

    @JvmStatic
    fun toggleSearchViewDecoration(searchView: SearchView, decorate: Boolean) {
        val colorResId = if (decorate) R.color.canvascore_brand_black else R.color.canvascore_brand_white
        @ColorInt val colorInt = ContextCompat.getColor(searchView.context, colorResId)
        val dividerView = searchView.findViewById<View>(com.google.android.material.R.id.open_search_view_divider)
        dividerView.backgroundTintList = ColorStateList.valueOf(colorInt)
    }

    @JvmStatic
    private fun bindCallbacks(entity: UiEntityOfMaterialSearch, et: EditText) {
        if (entity.inputHandlingAdapter == null) {
            val textSupplying: Supplier<CharSequence> = Supplier(et::getText)
            entity.inputHandlingAdapter = InputHandlingAdapter(entity, textSupplying)
        }
        et.removeTextChangedListener(entity.inputHandlingAdapter)
        et.addTextChangedListener(entity.inputHandlingAdapter)
    }
}
