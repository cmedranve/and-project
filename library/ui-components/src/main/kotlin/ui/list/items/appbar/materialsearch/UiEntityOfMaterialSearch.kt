package pe.com.scotiabank.blpm.android.ui.list.items.appbar.materialsearch

import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import com.scotiabank.enhancements.handling.InstanceReceiver
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import pe.com.scotiabank.blpm.android.ui.util.Constant
import kotlin.properties.Delegates

class UiEntityOfMaterialSearch(
    isEnabledInputSearchView: Boolean = false,
    hintTextForSearchBar: CharSequence = Constant.EMPTY,
    hintTextForSearchView: CharSequence = Constant.EMPTY,
    @StyleRes textAppearanceForSearchBar: Int = ResourcesCompat.ID_NULL,
    @StyleRes textAppearanceForSearchView: Int = ResourcesCompat.ID_NULL,
    override val id: Long = randomLong(),
    val receiver: InstanceReceiver? = null,
    changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
) : IdentifiableUiEntity<UiEntityOfMaterialSearch>,
    ChangingState by changingState,
    Recycling by recycling
{

    var inputHandlingAdapter: InputHandlingAdapter? = null

    private var observableHintTextForSearchBar: String by Delegates.observable(
        hintTextForSearchBar.toString(),
        ::onChangeOfNonEntityProperty
    )

    var hintTextForSearchBar: CharSequence = hintTextForSearchBar
        set(value) {
            observableHintTextForSearchBar = value.toString()
            field = value
        }

    private var observableHintTextForSearchView: String by Delegates.observable(
        hintTextForSearchView.toString(),
        ::onChangeOfNonEntityProperty
    )

    var hintTextForSearchView: CharSequence = hintTextForSearchView
        set(value) {
            observableHintTextForSearchView = value.toString()
            field = value
        }

    var textAppearanceForSearchBar: Int by Delegates.observable(
        textAppearanceForSearchBar,
        ::onChangeOfNonEntityProperty
    )

    var textAppearanceForSearchView: Int by Delegates.observable(
        textAppearanceForSearchView,
        ::onChangeOfNonEntityProperty
    )

    var isEnabledInputSearchView: Boolean by Delegates.observable(
        isEnabledInputSearchView,
        ::onChangeOfNonEntityProperty
    )

    private var observableText: String by Delegates.observable(
        Constant.EMPTY,
        ::onChangeOfNonEntityProperty
    )

    var text: CharSequence = Constant.EMPTY
        set(value) {
            observableText = value.toString()
            field = value
        }

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfMaterialSearch,
    ): Boolean = isUnmodified
            && hintTextForSearchBar.contentEquals(other.hintTextForSearchBar)
            && hintTextForSearchView.contentEquals(other.hintTextForSearchView)
            && isEnabledInputSearchView == other.isEnabledInputSearchView
            && textAppearanceForSearchBar == other.textAppearanceForSearchBar
            && textAppearanceForSearchView == other.textAppearanceForSearchView
}
