package pe.com.scotiabank.blpm.android.ui.list.items

import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding

object UiBinderOfSideLinearLayout {

    @JvmStatic
    fun bind(
        paddingEntity: UiEntityOfPadding,
        bias: Float,
        llSide: LinearLayoutCompat,
    ) {
        UiBinderOfPadding.bind(paddingEntity, llSide)
        llSide.updateLayoutParams<ConstraintLayout.LayoutParams> {
            verticalBias = bias
        }
    }
}
