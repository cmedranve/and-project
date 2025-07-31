package pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu

import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfMenuItem(
    @IdRes val idRes: Int,
    val title: CharSequence,
    @DrawableRes val iconRes: Int = ResourcesCompat.ID_NULL,
    val data: Any? = null,
    val showAsActionFlags: Int = MenuItem.SHOW_AS_ACTION_ALWAYS,
    isVisible: Boolean = true,
    override val id: Long = randomLong(),
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfMenuItem>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isVisible: Boolean by Delegates.observable(
        isVisible,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfMenuItem,
    ): Boolean = isUnmodified
            && idRes == other.idRes
            && title.contentEquals(other.title)
            && iconRes == other.iconRes
            && showAsActionFlags == other.showAsActionFlags
            && isVisible == other.isVisible
}
