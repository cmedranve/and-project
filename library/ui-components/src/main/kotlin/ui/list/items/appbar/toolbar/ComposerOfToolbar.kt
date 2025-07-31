package pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu.MenuItemController
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu.UiEntityOfMenuItem

class ComposerOfToolbar(
    private val receiver: InstanceReceiver,
) : ToolbarController, MenuItemController {

    private val entity: UiEntityOfToolbar by lazy {
        UiEntityOfToolbar(
            _itemEntitiesByIdRes = LinkedHashMap(),
            isNavigationIconEnabled = false,
            receiver = receiver,
        )
    }
    private val itemEntitiesByIdRes: LinkedHashMap<Int, UiEntityOfMenuItem> by entity::_itemEntitiesByIdRes

    fun composeUiData(
        visibilitySupplier: Supplier<Boolean>,
    ): UiCompoundOfSingle<UiEntityOfToolbar> = UiCompoundOfSingle(entity, visibilitySupplier)

    override fun editHome(
        isEnabled: Boolean,
        @DrawableRes iconRes: Int,
        titleText: CharSequence,
        @StyleRes titleAppearanceRes: Int,
    ) {
        entity.isNavigationIconEnabled = isEnabled
        entity.navigationIconRes = iconRes
        entity.titleText = titleText
        entity.titleAppearanceRes = titleAppearanceRes
    }

    override fun editHomeEnabling(isEnabled: Boolean) {
        entity.isNavigationIconEnabled = isEnabled
    }

    override fun editHomeIcon(@DrawableRes iconRes: Int) {
        entity.navigationIconRes = iconRes
    }

    override fun editHomeTitleText(text: CharSequence) {
        entity.titleText = text
    }

    override fun editHomeTitleAppearance(@StyleRes appearanceRes: Int) {
        entity.titleAppearanceRes = appearanceRes
    }

    override fun addMenuItem(
        idRes: Int,
        title: CharSequence,
        iconRes: Int,
        data: Any?,
        showAsActionFlags: Int,
        isVisible: Boolean
    ) {
        val itemEntity = UiEntityOfMenuItem(
            idRes = idRes,
            title = title,
            iconRes = iconRes,
            data = data,
            showAsActionFlags = showAsActionFlags,
            isVisible = isVisible,
        )
        itemEntitiesByIdRes[idRes] = itemEntity
    }

    override fun editMenuItemVisibility(idRes: Int, isVisible: Boolean) {
        val itemEntity: UiEntityOfMenuItem = itemEntitiesByIdRes[idRes] ?: return
        itemEntity.isVisible = isVisible
    }

    override fun removeMenuItem(@IdRes idRes: Int) {
        itemEntitiesByIdRes.remove(idRes)
    }
}
