package pe.com.scotiabank.blpm.android.ui.list.items.text

import androidx.appcompat.widget.AppCompatTextView
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback

object UiBinderOfText {

    @JvmStatic
    fun bind(entity: UiEntityOfText, tvText: AppCompatTextView) {
        bindTextProperties(entity, tvText)
        UiBinderOfClickCallback.bindNonClickableOrClickableBackground(entity, entity.receiver, tvText)
    }

    @JvmStatic
    fun bindTextProperties(entity: UiEntityOfText, tvText: AppCompatTextView) {
        tvText.setTextAppearance(entity.appearance)
        tvText.gravity = entity.gravity
        tvText.movementMethod = entity.movementMethod
        tvText.text = entity.text
        tvText.maxLines = entity.maxLines
        tvText.ellipsize = entity.whereToEllipsize
    }
}
