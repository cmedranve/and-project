package pe.com.scotiabank.blpm.android.client.base.toolbar

import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Supplier
import com.scotiabank.enhancements.handling.InstanceReceiver
import kotlinx.coroutines.withContext
import pe.com.scotiabank.blpm.android.client.base.coroutine.DispatcherProvider
import pe.com.scotiabank.blpm.android.ui.list.composite.CompositeOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.UiCompoundOfSingle
import pe.com.scotiabank.blpm.android.ui.list.compound.isGoingToBeVisible
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu.MenuItemController
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.ComposerOfToolbar
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.ToolbarController
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar.UiEntityOfToolbar
import pe.com.scotiabank.blpm.android.ui.util.Constant
import java.util.concurrent.ConcurrentHashMap

class AppBarComposite private constructor(
    dispatcherProvider: DispatcherProvider,
    private val composerOfToolbar: ComposerOfToolbar,
    private val visibilitySupplier: Supplier<Boolean>,
) : CompositeOfSingle<UiEntityOfToolbar>,
    DispatcherProvider by dispatcherProvider,
    ToolbarController by composerOfToolbar,
    MenuItemController by composerOfToolbar
{

    private val compoundsByKey: MutableMap<Int, List<UiCompoundOfSingle<UiEntityOfToolbar>>?> = ConcurrentHashMap()
    override val compounds: List<UiCompoundOfSingle<UiEntityOfToolbar>>
        get() = compoundsByKey[SINGLE_KEY].orEmpty()

    fun setHome(
        isEnabled: Boolean,
        @DrawableRes iconRes: Int = ResourcesCompat.ID_NULL,
        titleText: CharSequence = Constant.EMPTY,
        @StyleRes titleAppearanceRes: Int = ResourcesCompat.ID_NULL,
    ): AppBarComposite = apply {

        composerOfToolbar.editHome(isEnabled, iconRes, titleText, titleAppearanceRes)
    }

    fun setHomeEnabling(isEnabled: Boolean): AppBarComposite = apply {
        composerOfToolbar.editHomeEnabling(isEnabled)
    }

    fun setHomeIcon(@DrawableRes iconRes: Int): AppBarComposite = apply {
        composerOfToolbar.editHomeIcon(iconRes)
    }

    fun setHomeTitleText(text: CharSequence): AppBarComposite = apply {
        composerOfToolbar.editHomeTitleText(text)
    }

    fun setHomeTitleAppearance(@StyleRes appearanceRes: Int): AppBarComposite = apply {
        composerOfToolbar.editHomeTitleAppearance(appearanceRes)
    }

    fun addMenuItemWith(
        @IdRes idRes: Int,
        title: CharSequence,
        @DrawableRes iconRes: Int = ResourcesCompat.ID_NULL,
        data: Any? = null,
        showAsActionFlags: Int = MenuItem.SHOW_AS_ACTION_ALWAYS,
        isVisible: Boolean = true,
    ): AppBarComposite = apply {
        composerOfToolbar.addMenuItem(idRes, title, iconRes, data, showAsActionFlags, isVisible)
    }

    fun editMenuItemVisibilityWith(idRes: Int, isVisible: Boolean): AppBarComposite = apply {
        composerOfToolbar.editMenuItemVisibility(idRes, isVisible)
    }

    fun removeMenuItemWith(@IdRes idRes: Int): AppBarComposite = apply {
        composerOfToolbar.removeMenuItem(idRes)
    }

    override suspend fun recomposeItselfIfNeeded() = withContext(defaultDispatcher) {

        compoundsByKey.computeIfAbsent(SINGLE_KEY) { composeItself() }
    }

    private fun composeItself(): List<UiCompoundOfSingle<UiEntityOfToolbar>> {
        val toolbarCompound = composerOfToolbar.composeUiData(
            visibilitySupplier = visibilitySupplier,
        )
        return listOf(toolbarCompound)
    }

    class Factory(
        private val dispatcherProvider: DispatcherProvider,
    ) {

        fun create(
            receiver: InstanceReceiver,
            visibilitySupplier: Supplier<Boolean> = Supplier(::isGoingToBeVisible),
        ) = AppBarComposite(
            dispatcherProvider = dispatcherProvider,
            composerOfToolbar = ComposerOfToolbar(receiver),
            visibilitySupplier = visibilitySupplier,
        )
    }

        companion object {

            private val SINGLE_KEY: Int
                get() = 0
        }
}
