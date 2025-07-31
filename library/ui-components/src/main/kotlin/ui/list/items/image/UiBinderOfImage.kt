package pe.com.scotiabank.blpm.android.ui.list.items.image

import androidx.appcompat.widget.AppCompatImageView
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback

object UiBinderOfImage {

    @JvmStatic
    fun bind(entity: UiEntityOfImage, ivImage: AppCompatImageView) {
        ivImage.scaleType = entity.scaleType
        ivImage.importantForAccessibility = entity.importantForAccessibility
        UiBinderOfClickCallback.bindNonClickableOrClickable(entity, entity.receiver, ivImage)
    }
}
