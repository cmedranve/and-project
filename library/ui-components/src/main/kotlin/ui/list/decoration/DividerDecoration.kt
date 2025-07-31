package pe.com.scotiabank.blpm.android.ui.list.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.function.BiFunction

class DividerDecoration(
    private val mDivider: Drawable?,
    private val dividerPositions: List<Int>,
    private val rectMeasurement: BiFunction<View, ViewGroup.MarginLayoutParams, Rect>,
) {

    fun draw(canvas: Canvas, parent: RecyclerView) {
        if (mDivider == null) return

        val childCount: Int = parent.childCount
        if (childCount == 0) return

        val left: Int = parent.paddingLeft
        val right: Int = parent.width - parent.paddingRight

        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val absolutePosition = parent.getChildAdapterPosition(child)

            if (!dividerPositions.contains(absolutePosition)) continue

            val params: ViewGroup.MarginLayoutParams = child.layoutParams as? ViewGroup.MarginLayoutParams
                ?: continue

            val rect: Rect = rectMeasurement.apply(child, params)

            mDivider.setBounds(left, rect.top, right, rect.bottom)
            mDivider.draw(canvas)
        }

    }
}
