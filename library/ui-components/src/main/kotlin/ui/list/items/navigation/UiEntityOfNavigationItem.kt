package pe.com.scotiabank.blpm.android.ui.list.items.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.badge.UiEntityOfBadge
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfNavigationItem(
    @IdRes val idRes: Int,
    val badgeEntity: UiEntityOfBadge,
    val title: CharSequence = Constant.EMPTY,
    @DrawableRes val iconRes: Int = ResourcesCompat.ID_NULL,
    val data: Any? = null,
    isVisible: Boolean = true,
    override val id: Long = randomLong(),
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfNavigationItem>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isVisible: Boolean by Delegates.observable(
        isVisible,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfNavigationItem,
    ): Boolean = isUnmodified
            && idRes == other.idRes
            && title.contentEquals(other.title)
            && iconRes == other.iconRes
            && badgeEntity.isHoldingTheSameContentAs(other.badgeEntity)
            && isVisible == other.isVisible
}
