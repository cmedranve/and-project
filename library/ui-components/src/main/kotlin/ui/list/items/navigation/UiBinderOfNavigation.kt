package pe.com.scotiabank.blpm.android.ui.list.items.navigation

import android.content.res.ColorStateList
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.items.badge.UiBinderOfBadge
import pe.com.scotiabank.blpm.android.ui.util.bindIfDifferent

object UiBinderOfNavigation {

    @JvmStatic
    fun bind(entity: UiEntityOfNavigation, bnv: BottomNavigationView) {
        bindTheme(entity, bnv)
        bindMenu(entity, bnv)
    }

    @JvmStatic
    private fun bindTheme(entity: UiEntityOfNavigation, bnv: BottomNavigationView) {

        bindIfDifferent(entity.labelVisibilityMode, bnv::getLabelVisibilityMode, bnv::setLabelVisibilityMode)

        val itemActiveIndicatorColor: Int = ContextCompat.getColor(bnv.context, entity.itemActiveIndicatorColor)
        bnv.itemActiveIndicatorColor = ColorStateList.valueOf(itemActiveIndicatorColor)

        bindIfDifferent(entity.itemTextAppearanceInactive, bnv::getItemTextAppearanceInactive, bnv::setItemTextAppearanceInactive)
        bindIfDifferent(entity.itemTextAppearanceActive, bnv::getItemTextAppearanceActive, bnv::setItemTextAppearanceActive)

        bnv.itemTextColor = ContextCompat.getColorStateList(bnv.context, entity.itemTextColor)
        bnv.itemIconTintList = ContextCompat.getColorStateList(bnv.context, entity.itemIconTint)
    }

    @JvmStatic
    private fun bindMenu(entity: UiEntityOfNavigation, bnv: BottomNavigationView) {
        bnv.setOnItemSelectedListener(null)

        val itemEntitiesById: Map<Long, UiEntityOfNavigationItem> = entity.itemEntitiesById
        val itemEntities: Collection<UiEntityOfNavigationItem> = itemEntitiesById.values

        clearIfDifferent(itemEntities, bnv)
        itemEntities.forEach { itemEntity -> bindItem(itemEntity, bnv) }

        bindIfDifferent(entity.selectedResId, bnv::getSelectedItemId, bnv::setSelectedItemId)

        bindItemSelectedCallback(entity, bnv)
    }

    @JvmStatic
    private fun clearIfDifferent(entities: Collection<UiEntityOfNavigationItem>, bnv: BottomNavigationView) {
        val menu: Menu = bnv.menu

        val entitiesFound: List<UiEntityOfNavigationItem> = entities
            .filter { entity -> menu.findItem(entity.idRes) != null }

        if (entities.size == menu.size() && entitiesFound.size == menu.size()) return

        menu.clear()
    }

    @JvmStatic
    private fun bindItem(entity: UiEntityOfNavigationItem, bnv: BottomNavigationView) {
        val menuItem: MenuItem = fetchMenuItem(entity.idRes, entity.title, bnv.menu)
        bindBadge(entity, bnv)

        if (ResourcesCompat.ID_NULL == entity.iconRes) {
            menuItem.icon = null
            menuItem.isVisible = entity.isVisible
            return
        }
        menuItem.setIcon(entity.iconRes)
        menuItem.isVisible = entity.isVisible
    }

    @JvmStatic
    private fun fetchMenuItem(
        @IdRes idRes: Int,
        title: CharSequence,
        menu: Menu,
    ): MenuItem = menu.findItem(idRes) ?: menu.add(Menu.NONE, idRes, Menu.NONE, title)

    @JvmStatic
    private fun bindBadge(entity: UiEntityOfNavigationItem, bnv: BottomNavigationView) {
        val badgeDrawable: BadgeDrawable = bnv.getOrCreateBadge(entity.idRes)
        UiBinderOfBadge.bindOrClear(entity.badgeEntity, badgeDrawable)
    }

    @JvmStatic
    private fun bindItemSelectedCallback(entity: UiEntityOfNavigation, bnv: BottomNavigationView) {
        bnv.setOnItemSelectedListener { menuItem -> handleItemClick(entity, menuItem, bnv) }
    }

    @JvmStatic
    private fun handleItemClick(
        entity: UiEntityOfNavigation,
        menuItem: MenuItem,
        bnv: BottomNavigationView,
    ): Boolean {

        if (menuItem.itemId == bnv.selectedItemId) return false
        val nonNullReceiver: InstanceReceiver = entity.receiver ?: return false

        val itemEntitiesById: Map<Long, UiEntityOfNavigationItem> = entity.itemEntitiesById
        val itemEntity: UiEntityOfNavigationItem = itemEntitiesById
            .firstNotNullOfOrNull { itemEntityById -> toSelectedItem(itemEntityById.value, menuItem) }
            ?: return false

        entity.selectedItemId = itemEntity.id
        nonNullReceiver.receive(itemEntity)
        return true
    }

    @JvmStatic
    private fun toSelectedItem(
        itemEntity: UiEntityOfNavigationItem,
        menuItem: MenuItem,
    ): UiEntityOfNavigationItem? = if (itemEntity.idRes == menuItem.itemId) itemEntity else null
}
