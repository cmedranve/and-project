package pe.com.scotiabank.blpm.android.ui.list.items.buttons.navigationbutton

import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import com.scotiabank.canvascore.buttons.NavigationButton

internal object UiBinderOfNavigationButton {

    @JvmStatic
    fun <D: Any> bindContent(
        entity: UiEntityOfNavigationButton<D>,
        nButtonBack: NavigationButton,
        gravity: Int,
    ) {
        nButtonBack.isEnabled = entity.isEnabled
        nButtonBack.setText(entity.text, entity.bufferTypeForText)
        attemptBindGravity(nButtonBack, gravity)

        if (ResourcesCompat.ID_NULL == entity.drawableId) return
        nButtonBack.setDrawable(entity.drawableId)
    }

    private fun attemptBindGravity(nButtonBack: NavigationButton, gravity: Int) {
        if (nButtonBack.gravity == gravity) return
        nButtonBack.updateLayoutParams<LinearLayoutCompat.LayoutParams> {
            this.gravity = gravity
        }
    }
}
