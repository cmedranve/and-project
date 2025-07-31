package pe.com.scotiabank.blpm.android.ui.list.decoration

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView

fun interface DecorationRendering {

    fun render(recyclerView: RecyclerView, @DrawableRes resId: Int, positions: List<Int>)
}
