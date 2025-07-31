package pe.com.scotiabank.blpm.android.ui.list.items.appbar.toolbar

import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.isHoldingTheSameContentAs
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.menu.UiEntityOfMenuItem
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfToolbar(
    internal val _itemEntitiesByIdRes: LinkedHashMap<Int, UiEntityOfMenuItem>,
    isNavigationIconEnabled: Boolean,
    @DrawableRes navigationIconRes: Int = ResourcesCompat.ID_NULL,
    titleText: CharSequence = Constant.EMPTY,
    @StyleRes titleAppearanceRes: Int = ResourcesCompat.ID_NULL,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    override val id: Long = randomLong(),
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfToolbar>,
    ChangingState by changingState,
    Recycling by recycling
{

    var isNavigationIconEnabled: Boolean by Delegates.observable(
        isNavigationIconEnabled,
        ::onChangeOfNonEntityProperty
    )

    var navigationIconRes: Int by Delegates.observable(
        navigationIconRes,
        ::onChangeOfNonEntityProperty
    )

    private var observableTitleText: String by Delegates.observable(
        titleText.toString(),
        ::onChangeOfNonEntityProperty
    )

    var titleText: CharSequence = titleText
        set(value) {
            observableTitleText = value.toString()
            field = value
        }

    var titleAppearanceRes: Int by Delegates.observable(
        titleAppearanceRes,
        ::onChangeOfNonEntityProperty
    )

    val itemEntitiesByIdRes: Map<Int, UiEntityOfMenuItem>
        get() = _itemEntitiesByIdRes

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfToolbar,
    ): Boolean = isUnmodified
            && isNavigationIconEnabled == other.isNavigationIconEnabled
            && navigationIconRes == other.navigationIconRes
            && titleText.contentEquals(other.titleText)
            && titleAppearanceRes == other.titleAppearanceRes
            && _itemEntitiesByIdRes.isHoldingTheSameContentAs(other._itemEntitiesByIdRes)
}
