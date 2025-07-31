package pe.com.scotiabank.blpm.android.ui.list.items.footer

import android.view.View
import android.widget.Space
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import pe.com.scotiabank.blpm.android.ui.R

internal object SpaceMeasurement {

    private const val MINIMUM_VALID_Y: Int = 0

    @JvmStatic
    fun safelyComputeThenUpdateSpaceHeight(preComputed: Int, rvItems: RecyclerView) {
        val clFooter: ConstraintLayout = rvItems
            .children
            .lastOrNull(::isFooterItemView) as? ConstraintLayout
            ?: return
        val space: Space = clFooter
            .children
            .firstOrNull() as? Space
            ?: return
        computeThenUpdateSpaceHeightOnPreDraw(preComputed, clFooter, space)
    }

    @JvmStatic
    private fun isFooterItemView(
        child: View,
    ): Boolean = child is ConstraintLayout && child.id == R.id.cl_footer

    @JvmStatic
    private fun computeThenUpdateSpaceHeightOnPreDraw(
        preComputed: Int,
        clFooter: ConstraintLayout,
        space: Space,
    ) = clFooter.doOnPreDraw {
        val height: Int = computeSpaceHeight(preComputed, clFooter)
        updateSpaceHeight(space, height)
    }

    @JvmStatic
    private fun computeSpaceHeight(preComputed: Int, clFooter: View): Int {
        val top: Int = clFooter.top
        val heightDiff: Int = preComputed - top
        return if (heightDiff > MINIMUM_VALID_Y) heightDiff else MINIMUM_VALID_Y
    }

    @JvmStatic
    private fun updateSpaceHeight(sFooter: Space, heightForSpace: Int) {
        if (heightForSpace == sFooter.height) return
        sFooter.updateLayoutParams<LayoutParams> {
            width = LayoutParams.MATCH_CONSTRAINT
            height = heightForSpace
        }
    }
}
