package pe.com.scotiabank.blpm.android.ui.list.decoration

import androidx.annotation.DrawableRes
import com.scotiabank.canvascore.R

class DecorationCompound(
    val positions: List<Int>,
    val rendering: DecorationRendering,
    @DrawableRes val resId: Int = R.drawable.canvascore_divider,
)
