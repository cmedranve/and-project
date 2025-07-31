package pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar

import android.view.View
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.util.Constant

object ToolbarRendering {

    @JvmStatic
    fun renderSingleFrom(
        compounds: List<UiCompoundOfSingle<UiEntityOfToolbar>>,
        appBarLayout: AppBarLayout,
        toolbar: Toolbar,
    ) {
        if (appBarLayout.visibility != View.VISIBLE) {
            appBarLayout.visibility = View.VISIBLE
        }

        val compound: UiCompoundOfSingle<UiEntityOfToolbar> = compounds
            .firstOrNull() ?: return clearToolbar(toolbar)

        bindToolbar(toolbar, compound)
    }

    @JvmStatic
    private fun bindToolbar(toolbar: Toolbar, compound: UiCompoundOfSingle<UiEntityOfToolbar>) {
        val isGoingToBeVisible: Boolean = compound.visibilitySupplier.get()
        toolbar.visibility = if (isGoingToBeVisible) View.VISIBLE else View.GONE
        UiBinderOfToolbar.bind(compound.entity, toolbar)
    }

    @JvmStatic
    private fun clearToolbar(toolbar: Toolbar) {
        toolbar.visibility = View.GONE
        UiBinderOfToolbar.clearNavigationIcon(toolbar)
        toolbar.title = Constant.EMPTY
        toolbar.menu.clear()
    }
}
