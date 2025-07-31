package pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu.UiBinderOfMenuItem
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu.UiEntityOfMenuItem

object UiBinderOfToolbar {

    @JvmStatic
    fun bind(entity: UiEntityOfToolbar, toolbar: Toolbar) {
        bindNavigationIcon(entity, toolbar)
        bindTitle(entity, toolbar)
        bindMenu(entity, toolbar)
    }

    @JvmStatic
    private fun bindNavigationIcon(entity: UiEntityOfToolbar, toolbar: Toolbar) {

        if (ResourcesCompat.ID_NULL == entity.navigationIconRes) {
            clearNavigationIcon(toolbar)
            return
        }

        if (entity.isNavigationIconEnabled) {
            toolbar.logo = null
            toolbar.setNavigationIcon(entity.navigationIconRes)
        } else {
            toolbar.navigationIcon = null
            toolbar.setLogo(entity.navigationIconRes)
        }

        toolbar.setNavigationOnClickListener { handleNavigationClick(entity) }
    }

    @JvmStatic
    internal fun clearNavigationIcon(toolbar: Toolbar) {
        toolbar.logo = null
        toolbar.navigationIcon = null
    }

    @JvmStatic
    private fun handleNavigationClick(entity: UiEntityOfToolbar) {
        val nonNullReceiver: InstanceReceiver = entity.receiver ?: return
        nonNullReceiver.receive(entity)
    }

    @JvmStatic
    private fun bindTitle(entity: UiEntityOfToolbar, toolbar: Toolbar) {
        toolbar.title = entity.titleText

        if (ResourcesCompat.ID_NULL == entity.titleAppearanceRes) return

        toolbar.setTitleTextAppearance(toolbar.context, entity.titleAppearanceRes)
    }

    @JvmStatic
    private fun bindMenu(entity: UiEntityOfToolbar, toolbar: Toolbar) {

        val menu: Menu = toolbar.menu
        menu.clear()

        val itemEntitiesByIdRes: Map<Int, UiEntityOfMenuItem> = entity.itemEntitiesByIdRes
        UiBinderOfMenuItem.bind(itemEntitiesByIdRes, menu)

        toolbar.setOnMenuItemClickListener { menuItem ->
            handleMenuItemClick(entity, menuItem)
        }
    }

    @JvmStatic
    private fun handleMenuItemClick(entity: UiEntityOfToolbar, menuItem: MenuItem): Boolean {

        val nonNullReceiver: InstanceReceiver = entity.receiver ?: return false

        val itemEntitiesByIdRes: Map<Int, UiEntityOfMenuItem> = entity.itemEntitiesByIdRes
        val itemEntity: UiEntityOfMenuItem = itemEntitiesByIdRes[menuItem.itemId] ?: return false

        nonNullReceiver.receive(itemEntity)
        return true
    }
}
