package pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu

import android.view.Menu
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat

object UiBinderOfMenuItem {

    @JvmStatic
    fun bind(itemEntitiesByIdRes: Map<Int, UiEntityOfMenuItem>, menu: Menu) {
        val itemEntities: Collection<UiEntityOfMenuItem> = itemEntitiesByIdRes.values
        itemEntities.forEach { itemEntity -> bindItem(itemEntity, menu) }
    }

    @JvmStatic
    private fun bindItem(itemEntity: UiEntityOfMenuItem, menu: Menu) {
        val menuItem: MenuItem = menu.add(Menu.NONE, itemEntity.idRes, Menu.NONE, itemEntity.title)
        if (ResourcesCompat.ID_NULL == itemEntity.iconRes) {
            menuItem.icon = null
            menuItem.setShowAsActionFlags(itemEntity.showAsActionFlags)
            menuItem.isVisible = itemEntity.isVisible
            return
        }
        menuItem.setIcon(itemEntity.iconRes)
        menuItem.setShowAsActionFlags(itemEntity.showAsActionFlags)
        menuItem.isVisible = itemEntity.isVisible
    }
}
