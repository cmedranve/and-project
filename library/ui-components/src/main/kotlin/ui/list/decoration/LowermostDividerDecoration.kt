package pe.com.scotiabank.blpm.android.ui.list.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import java.util.function.BiFunction

class LowermostDividerDecoration(
    context: Context,
    @DrawableRes resId: Int,
    dividerPositions: List<Int>
) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable? = AppCompatResources.getDrawable(context, resId)
    private val intrinsicHeightOfDivider: Int
        get() = mDivider?.intrinsicHeight ?: 0

    private val dividerDecoration = DividerDecoration(
        mDivider = mDivider,
        dividerPositions = dividerPositions,
        rectMeasurement = BiFunction(::measureRect),
    )

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        dividerDecoration.draw(canvas, parent)
    }

    private fun measureRect(child: View, params: ViewGroup.MarginLayoutParams): Rect {
        val bottom: Int = child.bottom + params.bottomMargin
        val top: Int = bottom - intrinsicHeightOfDivider
        return Rect(0, top, 0, bottom)
    }

}
