package pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch

import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import pe.com.scotiabank.blpm.android.ui.util.Constant

interface MaterialSearchController {

    fun editSearch(
        hintTextForSearchBar: CharSequence = Constant.EMPTY,
        hintTextForSearchView: CharSequence = Constant.EMPTY,
        @StyleRes textAppearanceRes: Int = ResourcesCompat.ID_NULL,
    )

    fun editSearchView(
        isEnabled: Boolean = false,
        hintText: CharSequence = Constant.EMPTY,
        @StyleRes textAppearanceRes: Int = ResourcesCompat.ID_NULL,
    )
}
