package pe.com.scotiabank.blpm.android.client.base.bottomsheet.list

import android.content.res.Resources
import android.view.View
import androidx.annotation.DimenRes
import androidx.core.view.doOnLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.list.items.appbar.FinderOfAppBarLayout

object BottomSheetPaddingUtil {

    @JvmStatic
    fun setUpPaddingOnLayout(
        recyclerView: RecyclerView,
        rootView: View,
        @DimenRes horizontalPaddingRes: Int
    ) {
        val resources: Resources = recyclerView.resources ?: return
        val horizontalPadding: Int = resources.getDimensionPixelOffset(horizontalPaddingRes)
        setUpPaddingOnLayout(recyclerView, horizontalPadding, rootView)
    }

    /**
     * Hacking code
     * Since the AppBarLayout is over the NestedScrollView of the CanvasCore's BottomSheetListDialog,
     * we need to set RecyclerView's paddingTop at the same size of the AppBarLayout's height.
     * */
    @JvmStatic
    private fun setUpPaddingOnLayout(
        recyclerView: RecyclerView,
        horizontalPadding: Int,
        rootView: View
    ) = rootView.doOnLayout {
        setUpPadding(recyclerView, horizontalPadding, it)
    }

    @JvmStatic
    private fun setUpPadding(recyclerView: RecyclerView, horizontalPadding: Int, rootView: View) {
        val res: Resources = rootView.resources
        val zeroPadding: Int = res.getDimensionPixelOffset(R.dimen.canvascore_margin_0)
        val marginTopOfParent: Int = findMarginTopOfRecyclerParent(recyclerView, zeroPadding)
        val heightOfAppBarLayout: Int = rootView
            .let(FinderOfAppBarLayout::attemptFind)
            ?.height
            ?: zeroPadding
        var topPadding: Int = heightOfAppBarLayout - marginTopOfParent
        topPadding = if (topPadding > 0) zeroPadding else topPadding
        recyclerView.setPadding(horizontalPadding, topPadding, horizontalPadding, zeroPadding)
    }

    @JvmStatic
    private fun findMarginTopOfRecyclerParent(recyclerView: RecyclerView, defaultPadding: Int): Int {
        val parent: View = recyclerView.parent as? View ?: return defaultPadding
        return parent.marginTop
    }
}
