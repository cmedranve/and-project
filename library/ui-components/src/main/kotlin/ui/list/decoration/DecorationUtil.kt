package pe.com.scotiabank.blpm.android.ui.list.decoration

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView

object DecorationUtil {

    @JvmStatic
    fun addDividerAboveEachItem(
        recyclerView: RecyclerView,
        @DrawableRes resId: Int,
        positions: List<Int>
    ) {
        val decoration = UppermostDividerDecoration(recyclerView.context.applicationContext, resId, positions)
        recyclerView.addItemDecoration(decoration)
    }

    @JvmStatic
    fun addDividerBelowEachItem(
        recyclerView: RecyclerView,
        @DrawableRes resId: Int,
        positions: List<Int>
    ) {
        val decoration = LowermostDividerDecoration(recyclerView.context.applicationContext, resId, positions)
        recyclerView.addItemDecoration(decoration)
    }
}
