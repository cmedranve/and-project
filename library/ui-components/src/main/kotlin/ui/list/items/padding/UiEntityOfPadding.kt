package pe.com.scotiabank.blpm.android.ui.list.items.padding

import androidx.annotation.DimenRes
import com.scotiabank.canvascore.R
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.ui.list.changingstates.ChangingState
import pe.com.scotiabank.blpm.android.ui.list.changingstates.MutableState
import pe.com.scotiabank.blpm.android.ui.list.items.IdentifiableUiEntity
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.Recycling
import pe.com.scotiabank.blpm.android.ui.list.layoutmanager.StatelessRecycling
import kotlin.properties.Delegates

class UiEntityOfPadding(
    @DimenRes top: Int = R.dimen.canvascore_margin_0,
    @DimenRes bottom: Int = R.dimen.canvascore_margin_0,
    @DimenRes left: Int = R.dimen.canvascore_margin_0,
    @DimenRes right: Int = R.dimen.canvascore_margin_0,
    override val id: Long = randomLong(),
    private val changingState: ChangingState = MutableState(),
    recycling: Recycling = StatelessRecycling,
): IdentifiableUiEntity<UiEntityOfPadding>,
    ChangingState by changingState,
    Recycling by recycling
{

    constructor(
        @DimenRes horizontal: Int = R.dimen.canvascore_margin_0,
        @DimenRes vertical: Int = R.dimen.canvascore_margin_0,
        id: Long = randomLong(),
        changingState: ChangingState = MutableState(),
        recycling: Recycling = StatelessRecycling,
    ) : this(
        left = horizontal,
        right = horizontal,
        top = vertical,
        bottom = vertical,
        id = id,
        changingState = changingState,
        recycling = recycling
    )

    constructor(
        @DimenRes padding: Int = R.dimen.canvascore_margin_0,
        id: Long = randomLong(),
        changingState: ChangingState = MutableState(),
        recycling: Recycling = StatelessRecycling,
    ) : this(
        left = padding,
        right = padding,
        top = padding,
        bottom = padding,
        id = id,
        changingState = changingState,
        recycling = recycling
    )

    var top: Int by Delegates.observable(
        top,
        ::onChangeOfNonEntityProperty
    )

    var bottom: Int by Delegates.observable(
        bottom,
        ::onChangeOfNonEntityProperty
    )

    var left: Int by Delegates.observable(
        left,
        ::onChangeOfNonEntityProperty
    )

    var right: Int by Delegates.observable(
        right,
        ::onChangeOfNonEntityProperty
    )

    override fun isHoldingTheSameContentAs(
        other: UiEntityOfPadding
    ): Boolean = isUnmodified
            && left == other.left
            && top == other.top
            && right == other.right
            && bottom == other.bottom

    override fun resetChangingState() {
        changingState.resetChangingState()
    }
}
