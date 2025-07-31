package pe.com.scotiabank.blpm.android.ui.list.items.navigation

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView.LabelVisibility
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.items.isHoldingTheSameContentAs
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfNavigation(
    internal val _itemEntitiesById: LinkedHashMap<Long, UiEntityOfNavigationItem>,
    selectedItemId: Long,
    @LabelVisibility labelVisibilityMode: Int = BottomNavigationView.LABEL_VISIBILITY_LABELED,
    @ColorRes itemActiveIndicatorColor: Int = com.scotiabank.canvascore.R.color.canvascore_card_pressed_background,
    @StyleRes itemTextAppearanceInactive: Int = R.style.canvascore_style_legal_alternate,
    @StyleRes itemTextAppearanceActive: Int = R.style.canvascore_style_legal_alternate_bold,
    @ColorRes itemTextColor: Int = R.color.selector_navigation_bar_item,
    @ColorRes itemIconTint: Int = R.color.selector_navigation_bar_item,
    val receiver: InstanceReceiver? = null,
    val data: Any? = null,
    override val id: Long = randomLong(),
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfNavigation>,
    ChangingState by changingState,
    Recycling by recycling
{

    var selectedItemId: Long by Delegates.observable(
        selectedItemId,
        ::onChangeOfNonEntityProperty
    )

    val selectedResId: Int
        @IdRes
        get() = _itemEntitiesById[selectedItemId]?.idRes ?: View.NO_ID

    var labelVisibilityMode: Int by Delegates.observable(
        labelVisibilityMode,
        ::onChangeOfNonEntityProperty
    )

    var itemActiveIndicatorColor: Int by Delegates.observable(
        itemActiveIndicatorColor,
        ::onChangeOfNonEntityProperty
    )

    var itemTextAppearanceInactive: Int by Delegates.observable(
        itemTextAppearanceInactive,
        ::onChangeOfNonEntityProperty
    )

    var itemTextAppearanceActive: Int by Delegates.observable(
        itemTextAppearanceActive,
        ::onChangeOfNonEntityProperty
    )

    var itemTextColor: Int by Delegates.observable(
        itemTextColor,
        ::onChangeOfNonEntityProperty
    )

    var itemIconTint: Int by Delegates.observable(
        itemIconTint,
        ::onChangeOfNonEntityProperty
    )

    val itemEntitiesById: Map<Long, UiEntityOfNavigationItem>
        get() = _itemEntitiesById

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfNavigation,
    ): Boolean = isUnmodified
            && selectedItemId == other.selectedItemId
            && labelVisibilityMode == other.labelVisibilityMode
            && itemActiveIndicatorColor == other.itemActiveIndicatorColor
            && itemTextAppearanceInactive == other.itemTextAppearanceInactive
            && itemTextAppearanceActive == other.itemTextAppearanceActive
            && itemTextColor == other.itemTextColor
            && itemIconTint == other.itemIconTint
            && _itemEntitiesById.isHoldingTheSameContentAs(other._itemEntitiesById)
}
