package pe.com.scotiabank.blpm.android.ui.list.items.card

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.card.MaterialCardView
import pe.com.scotiabank.blpm.android.ui.databinding.ViewCardItemBinding
import pe.com.scotiabank.blpm.android.ui.list.adapterfactories.UiEntityCarrier
import pe.com.scotiabank.blpm.android.ui.list.items.UiBinderOfClickCallback
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiBinderOfPadding
import pe.com.scotiabank.blpm.android.ui.list.items.recycler.UiBinderOfRecyclerForOtherType
import pe.com.scotiabank.blpm.android.ui.list.items.widthparam.UiBinderOfWidthParam

object UiBinderOfCard {

    @JvmStatic
    fun <D: Any> delegateBinding(
        carrier: UiEntityCarrier<UiEntityOfCard<D>, ViewCardItemBinding>
    ) {
        val entity: UiEntityOfCard<D> = carrier.uiEntity ?: return
        carrier.weakBinding.get()?.let { binding -> bind(carrier, entity, binding) }
    }

    @JvmStatic
    private fun <D: Any> bind(
        carrier: UiEntityCarrier<UiEntityOfCard<D>, ViewCardItemBinding>,
        entity: UiEntityOfCard<D>,
        binding: ViewCardItemBinding
    ) {
        UiBinderOfPadding.bind(entity.paddingEntity, binding.root)
        UiBinderOfWidthParam.bind(child = binding.root, expectedFlexGrow = entity.expectedFlexGrow)
        bindCardView(entity, binding.mcv)
        UiBinderOfClickCallback.bindNonClickableOrClickable(entity, entity.receiver, binding.mcv)

        UiBinderOfRecyclerForOtherType.bind(
            carrier = carrier,
            entity = entity.recyclerEntity,
            recyclerView = binding.rvItems,
        )
    }

    @JvmStatic
    private fun <D: Any> bindCardView(
        entity: UiEntityOfCard<D>,
        cardView: MaterialCardView
    ) {
        bindUseCompatPadding(entity.useCompatPadding, cardView)
        bindRadius(entity.cornerRadiusRes, cardView)
        bindElevation(entity.elevationRes, cardView)
        bindBackgroundColor(entity.backgroundColorRes, cardView)
        bindStrokeColor(entity.strokeColorRes, cardView)
        bindStrokeWidth(entity.strokeWidthRes, cardView)
    }

    @JvmStatic
    private fun bindUseCompatPadding(useCompatPadding: Boolean, cardView: MaterialCardView) {

        if (useCompatPadding != cardView.useCompatPadding) {
            cardView.useCompatPadding = useCompatPadding
        }
    }

    @JvmStatic
    internal fun bindRadius(@DimenRes cornerRadiusRes: Int, cardView: MaterialCardView) {

        val res: Resources = cardView.resources
        val cornerRadiusInPixels: Int = res.getDimensionPixelOffset(cornerRadiusRes)
        val cornerRadiusInPixelsAsFloat: Float = cornerRadiusInPixels.toFloat()

        if (cornerRadiusInPixelsAsFloat != cardView.radius) {
            cardView.radius = cornerRadiusInPixelsAsFloat
        }
    }

    @JvmStatic
    internal fun bindElevation(@DimenRes elevationRes: Int, cardView: MaterialCardView) {

        val res: Resources = cardView.resources
        val elevationInPixels: Int = res.getDimensionPixelOffset(elevationRes)
        val elevationInPixelsAsFloat: Float = elevationInPixels.toFloat()

        if (elevationInPixelsAsFloat != cardView.cardElevation) {
            cardView.cardElevation = elevationInPixelsAsFloat
        }
    }

    @JvmStatic
    internal fun bindBackgroundColor(@ColorRes colorRes: Int, cardView: MaterialCardView) {
        @ColorInt val backgroundColor: Int = ContextCompat.getColor(cardView.context, colorRes)
        cardView.setCardBackgroundColor(backgroundColor)
    }

    @JvmStatic
    internal fun bindBackgroundDrawable(@DrawableRes drawableRes: Int, cardView: MaterialCardView) {
        if (ResourcesCompat.ID_NULL == drawableRes) {
            cardView.setBackgroundDrawable(null)
            return
        }
        val drawable: Drawable? = ContextCompat.getDrawable(cardView.context, drawableRes)
        cardView.setBackgroundDrawable(drawable)
    }

    @JvmStatic
    private fun bindStrokeColor(@ColorRes colorRes: Int, cardView: MaterialCardView) {
        @ColorInt val strokeColor: Int = ContextCompat.getColor(cardView.context, colorRes)
        cardView.strokeColor = strokeColor
    }

    @JvmStatic
    internal fun bindStrokeWidth(@DimenRes strokeWidthRes: Int, cardView: MaterialCardView) {
        val res: Resources = cardView.resources
        val strokeWidth: Int = res.getDimensionPixelOffset(strokeWidthRes)
        cardView.strokeWidth = strokeWidth
    }
}
