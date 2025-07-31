package pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar

import androidx.core.content.res.ResourcesCompat
import pe.com.scotiabank.blpm.android.ui.util.Constant

interface ToolbarController {

    fun editHome(
        isEnabled: Boolean,
        iconRes: Int = ResourcesCompat.ID_NULL,
        titleText: CharSequence = Constant.EMPTY,
        titleAppearanceRes: Int = ResourcesCompat.ID_NULL,
    )

    fun editHomeEnabling(isEnabled: Boolean)

    fun editHomeIcon(iconRes: Int)

    fun editHomeTitleText(text: CharSequence)

    fun editHomeTitleAppearance(appearanceRes: Int)
}
