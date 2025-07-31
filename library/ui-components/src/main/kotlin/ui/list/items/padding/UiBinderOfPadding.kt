package pe.com.scotiabank.blpm.android.ui.list.items.padding

import android.content.res.Resources
import android.view.View

object UiBinderOfPadding {

    @JvmStatic
    fun bind(entity: UiEntityOfPadding, view: View) {
        val res: Resources = view.resources
        val leftInPixels: Int = res.getDimensionPixelOffset(entity.left)
        val topInPixels: Int = res.getDimensionPixelOffset(entity.top)
        val rightInPixels: Int = res.getDimensionPixelOffset(entity.right)
        val bottomInPixels: Int = res.getDimensionPixelOffset(entity.bottom)
        view.setPadding(leftInPixels, topInPixels, rightInPixels, bottomInPixels)
    }
}
