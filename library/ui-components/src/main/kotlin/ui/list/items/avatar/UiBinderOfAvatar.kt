package pe.com.scotiabank.blpm.android.ui.list.items.avatar

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.card.MaterialCardView
import com.scotiabank.canvascore.R
import com.scotiabank.canvascore.views.Avatar
import pe.com.scotiabank.blpm.android.ui.databinding.ViewAvatarItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfAvatar {

    @JvmStatic
    fun delegateBinding(carrier: UiEntityCarrier<UiEntityOfAvatar, ViewAvatarItemBinding>) {
        val entity: UiEntityOfAvatar = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(entity, binding) }
    }

    @JvmStatic
    private fun bind(
        entity: UiEntityOfAvatar,
        binding: ViewAvatarItemBinding,
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindClickableAvatar(entity, binding.avatarImage)
        bindDrawableOrName(entity, binding.avatarImage)
    }

    internal fun bindClickableAvatar(entity: UiEntityOfAvatar, avatar: Avatar) {
        UiBinderOfClickCallback.bindNonClickableOrClickable(entity, entity.receiver, avatar)
    }

    internal fun bindDrawableOrName(entity: UiEntityOfAvatar, avatar: Avatar) {
        val isNameBinding: Boolean = entity.drawableRes == ResourcesCompat.ID_NULL
        if (isNameBinding) bindName(entity, avatar) else bindDrawable(entity, avatar)
    }

    private fun bindDrawable(entity: UiEntityOfAvatar, avatar: Avatar) {
        val drawable: Drawable = AppCompatResources.getDrawable(avatar.context, entity.drawableRes) ?: return
        avatar.setupAvatar(drawable, entity.size)
    }

    private fun bindName(entity: UiEntityOfAvatar, avatar: Avatar) {
        val cardView: MaterialCardView = avatar.findViewById(R.id.card_view_avatar)
        cardView.visibility = View.GONE

        if (entity.color == null) {
            avatar.setupAvatar(entity.name, entity.size)
            return
        }
        avatar.setupAvatar(entity.name, entity.color, entity.size)
    }

}
