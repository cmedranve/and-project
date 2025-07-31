package pe.com.scotiabank.blpm.android.ui.list.windowinsets

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomappbar.BottomAppBar
import pe.com.scotiabank.blpm.android.ui.R

object OverlappingResolver {

    @JvmStatic
    fun resolve(rootView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(
            rootView,
            OnApplyWindowInsetsListener(::resolveOnApplyWindowInsets),
        )
    }

    /**
     * Bottom insets is already computed and included in <a href="https://github.com/material-components/material-components-android/blob/master/lib/java/com/google/android/material/bottomappbar/BottomAppBar.java#L397">BottomAppBar</a>.
     * That's why we only need setting {@code RootView.paddingBottom} it for non-portable screens.
     */
    @JvmStatic
    private fun resolveOnApplyWindowInsets(
        rootView: View,
        windowInsets: WindowInsetsCompat,
    ): WindowInsetsCompat {

        val insets: Insets = ViewCompat.getRootWindowInsets(rootView)
            ?.let(InsetsFinder::findInsets)
            ?: Insets.NONE
        val bottomAppBar: BottomAppBar? = rootView.findViewById(R.id.bab_anchored_bottom)

        if (bottomAppBar == null) {
            rootView.updatePadding(top = insets.top, bottom = insets.bottom)
            return windowInsets
        }

        rootView.updatePadding(top = insets.top)
        return windowInsets
    }
}
