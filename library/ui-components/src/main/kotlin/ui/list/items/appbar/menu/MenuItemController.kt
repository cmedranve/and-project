package pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu

import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat

interface MenuItemController {

    fun addMenuItem(
        idRes: Int,
        title: CharSequence,
        iconRes: Int = ResourcesCompat.ID_NULL,
        data: Any? = null,
        showAsActionFlags: Int = MenuItem.SHOW_AS_ACTION_ALWAYS,
        isVisible: Boolean = true,
    )

    fun editMenuItemVisibility(idRes: Int, isVisible: Boolean)

    fun removeMenuItem(idRes: Int)
}
