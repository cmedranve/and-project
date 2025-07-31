package pe.com.scotiabank.blpm.android.ui.list.items.appbar

import android.view.View
import androidx.core.view.allViews
import com.google.android.material.appbar.AppBarLayout

object FinderOfAppBarLayout {

    @JvmStatic
    fun attemptFind(rootView: View): AppBarLayout? {
        val children: Sequence<View> = rootView.allViews
        for (anyChild: View in children) {
            if (anyChild is AppBarLayout) return anyChild
        }
        return null
    }
}
