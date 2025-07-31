package pe.com.scotiabank.blpm.android.ui.list.items.skeleton

import androidx.annotation.DrawableRes
import pe.com.scotiabank.blpm.android.ui.R

enum class SkeletonShape(@DrawableRes val drawableId: Int) {
    CIRCLE(drawableId = R.drawable.shape_circle_card),
    QUADRILATERAL(drawableId = com.scotiabank.canvascore.R.drawable.canvascore_background_card_skeleton),
}
