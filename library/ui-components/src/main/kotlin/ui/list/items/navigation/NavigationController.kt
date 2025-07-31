package pe.com.scotiabank.blpm.android.ui.list.items.navigation

import androidx.core.content.res.ResourcesCompat
import pe.com.scotiabank.blpm.android.ui.util.Constant

interface NavigationController {

    fun addItem(
        id: Long,
        idRes: Int,
        badgeLabel: String? = null,
        badgeNumber: Int? = null,
        badgeMaxNumber: Int = 999,
        isBadgeVisible: Boolean = false,
        title: CharSequence = Constant.EMPTY,
        iconRes: Int = ResourcesCompat.ID_NULL,
        data: Any? = null,
        isItemVisible: Boolean = true,
    )

    fun setSelectedItem(id: Long)

    fun showItem(id: Long)

    fun hideItem(id: Long)

    fun editBadgeLabel(id: Long, badgeLabel: String?)

    fun editBadgeNumber(id: Long, badgeNumber: Int?)

    fun editBadgeVisibility(id: Long, isVisible: Boolean)
}
