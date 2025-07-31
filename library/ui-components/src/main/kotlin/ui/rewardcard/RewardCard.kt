package pe.com.scotiabank.blpm.android.ui.rewardcard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.card.MaterialCardView
import pe.com.scotiabank.blpm.android.ui.R
import pe.com.scotiabank.blpm.android.ui.databinding.CanvasRewardCardBinding

class RewardCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle,
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var binding: CanvasRewardCardBinding = CanvasRewardCardBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    private var mainStyle: Boolean = true

    private val strokeWidthCard: Int
        @DimenRes get() {
            if (mainStyle) return com.scotiabank.canvascore.R.dimen.canvascore_margin_0
            return com.scotiabank.canvascore.R.dimen.canvascore_margin_1
        }

    private val strokeColorCard: Int
        @ColorRes get() {
            if (mainStyle) return com.scotiabank.canvascore.R.color.canvascore_gray_0
            return com.scotiabank.canvascore.R.color.canvascore_gray_400
        }

    private val appearanceForBackgroundCard: Int
        @ColorRes get() {
            if (mainStyle) return com.scotiabank.canvascore.R.color.canvascore_gray_700
            return com.scotiabank.canvascore.R.color.canvascore_gray_0
        }

    private val appearanceForLabelPoints: Int
        @StyleRes get() {
            if (mainStyle) return com.scotiabank.canvascore.R.style.canvascore_style_caption_white
            return com.scotiabank.canvascore.R.style.canvascore_style_caption
        }

    private val appearanceForTotalPoints: Int
        @StyleRes get() {
            if (mainStyle) return com.scotiabank.canvascore.R.style.canvascore_style_headline_medium_white
            return com.scotiabank.canvascore.R.style.canvascore_style_headline_medium_black
        }

    private val appearanceForEquivalent: Int
        @StyleRes get() {
            if (mainStyle) return com.scotiabank.canvascore.R.style.canvascore_style_introduction_white
            return com.scotiabank.canvascore.R.style.canvascore_style_introduction_black
        }

    private val appearanceForPointsRate: Int
        @StyleRes get() {
            if (mainStyle) return com.scotiabank.canvascore.R.style.canvascore_style_body2_white
            return com.scotiabank.canvascore.R.style.canvascore_style_body2
        }

    init {
        setUpCardElevation()
        setUpCardRadius()
    }

    private fun setUpCardElevation() {
        val elevationInPixels: Int = resources.getDimensionPixelOffset(
            com.scotiabank.canvascore.R.dimen.canvascore_card_flat_elevation,
        )
        val elevationInPixelsAsFloat: Float = elevationInPixels.toFloat()
        cardElevation = elevationInPixelsAsFloat
    }

    private fun setUpCardRadius() {
        radius = resources.getDimension(
            com.scotiabank.canvascore.R.dimen.canvascore_border_radius_300,
        )
    }

    private fun setUpCardStroke() {
        strokeWidth = resources.getDimension(strokeWidthCard).toInt()
        strokeColor = ContextCompat.getColor(context, strokeColorCard)
    }

    fun setIsMainStyle(mainStyle: Boolean) {
        this.mainStyle = mainStyle
        updateBackground()
        setUpCardStroke()
    }

    private fun updateBackground() {
        val colorBackground = ContextCompat.getColor(context, appearanceForBackgroundCard)
        setCardBackgroundColor(colorBackground)
    }

    fun setLogo(@DrawableRes bankLogoRes: Int) {
        attemptBindDrawable(bankLogoRes, binding.ivLogo)
    }

    private fun attemptBindDrawable(@DrawableRes drawableRes: Int, ivColumn: ImageView) {

        if (ResourcesCompat.ID_NULL == drawableRes) {
            ivColumn.setImageDrawable(null)
            return
        }

        ivColumn.setImageResource(drawableRes)
    }

    fun setLabelPoints(label: CharSequence) {
        binding.tvLabelPoints.text = label
        binding.tvLabelPoints.setTextAppearance(appearanceForLabelPoints)
    }

    fun setPoints(points: CharSequence) {
        binding.tvPoints.text = points
        binding.tvPoints.setTextAppearance(appearanceForTotalPoints)
    }

    fun setPointsEquivalence(value: CharSequence) {
        binding.tvPointsEquivalence.text = value
        binding.tvPointsEquivalence.setTextAppearance(appearanceForEquivalent)
    }

    fun setPointsRate(value: CharSequence) {
        binding.tvPointsToValue.text = value
        binding.tvPointsToValue.setTextAppearance(appearanceForPointsRate)
    }
}
