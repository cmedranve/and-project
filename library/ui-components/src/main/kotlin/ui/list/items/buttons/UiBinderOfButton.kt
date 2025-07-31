package pe.com.scotiabank.blpm.android.ui.list.items.buttons

import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback

object UiBinderOfButton {

    @JvmStatic
    fun <D: Any> bind(entity: UiEntityOfButton<D>, btnButton: AppCompatButton) {
        btnButton.isEnabled = entity.isEnabled
        btnButton.text = entity.text
        btnButton.setCompoundDrawablesWithIntrinsicBounds(
            entity.drawableStartId,
            ResourcesCompat.ID_NULL,
            ResourcesCompat.ID_NULL,
            ResourcesCompat.ID_NULL,
        )
        UiBinderOfClickCallback.attemptBindEnabledClickable(entity, entity.receiver, btnButton)
    }
}
