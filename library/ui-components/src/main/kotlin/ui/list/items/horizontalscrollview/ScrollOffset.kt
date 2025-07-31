package pe.com.scotiabank.blpm.android.ui.list.items.horizontalscrollview

import android.view.View
import android.widget.HorizontalScrollView

internal object ScrollOffset {

    @JvmStatic
    fun saveOffsetFromStart(entity: HostUiEntityOfScrollOffset, parentView: View) {
        if (entity.isReset) return
        val hsv: HorizontalScrollView = FinderOfHorizontalScrollView.attemptFind(parentView) ?: return
        entity.scrollOffsetFromStart = estimateOffsetFromStart(hsv)
    }

    @JvmStatic
    private fun estimateOffsetFromStart(hsv: HorizontalScrollView): Int {
        return if (View.LAYOUT_DIRECTION_RTL == hsv.layoutDirection) -hsv.scrollX else hsv.scrollX
    }

    @JvmStatic
    fun restoreOffsetFromStart(entity: HostUiEntityOfScrollOffset, parentView: View) {
        val scrollOffsetFromStart: Int = entity.scrollOffsetFromStart ?: return
        val hsv: HorizontalScrollView = FinderOfHorizontalScrollView.attemptFind(parentView) ?: return
        hsv.scrollX = scrollOffsetFromStart
    }
}
