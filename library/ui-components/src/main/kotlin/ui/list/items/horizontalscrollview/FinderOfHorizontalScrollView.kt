package pe.com.scotiabank.blpm.android.ui.list.items.horizontalscrollview

import android.view.View
import android.widget.HorizontalScrollView
import androidx.core.view.allViews

internal object FinderOfHorizontalScrollView {

    @JvmStatic
    fun attemptFind(parentView: View): HorizontalScrollView? {
        val children: Sequence<View> = parentView.allViews
        for (anyChild: View in children) {
            if (anyChild is HorizontalScrollView) return anyChild
        }
        return null
    }
}
