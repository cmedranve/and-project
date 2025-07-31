package pe.com.scotiabank.blpm.android.ui.atmcard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.card.MaterialCardView
import com.scotiabank.canvascore.R
import pe.com.scotiabank.blpm.android.ui.databinding.CanvasAtmCardBinding

class AtmCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle,
): MaterialCardView(context, attrs, defStyleAttr) {

    private var binding: CanvasAtmCardBinding = CanvasAtmCardBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    val rightActionButton: AppCompatButton
        get() = binding.btnRightAction

    init {
        setUpCardElevation()
        setUpCardRadius()
        setUpCardStroke()
    }

    private fun setUpCardElevation() {
        val elevationInPixels: Int = resources.getDimensionPixelOffset(R.dimen.canvascore_card_flat_elevation)
        val elevationInPixelsAsFloat: Float = elevationInPixels.toFloat()
        cardElevation = elevationInPixelsAsFloat
    }

    private fun setUpCardRadius() {
        radius = resources.getDimension(R.dimen.canvascore_border_radius_300)
    }

    private fun setUpCardStroke() {
        strokeWidth = resources
            .getDimension(R.dimen.canvascore_margin_0)
            .toInt()
        strokeColor = ContextCompat.getColor(context, R.color.canvascore_gray_0)
    }

    fun setBankLogo(@DrawableRes bankLogoRes: Int) {
        attemptBindDrawable(bankLogoRes, binding.ivBankLogo)
    }

    private fun attemptBindDrawable(@DrawableRes drawableRes: Int, ivColumn: ImageView) {

        if (ResourcesCompat.ID_NULL == drawableRes) {
            ivColumn.setImageDrawable(null)
            return
        }

        ivColumn.setImageResource(drawableRes)
    }

    fun setCardName(name: CharSequence) {
        binding.tvCardName.text = name
    }

    fun setCardNumber(number: CharSequence) {
        binding.tvCardNumber.text = number
    }

    fun setRightActionButton(@DrawableRes iconRes: Int, label: CharSequence) {
        binding.btnRightAction.text = label
        binding.btnRightAction.setCompoundDrawablesWithIntrinsicBounds(
            iconRes,
            ResourcesCompat.ID_NULL,
            ResourcesCompat.ID_NULL,
            ResourcesCompat.ID_NULL,
        )
    }

    fun showRightActionButton(isGoingToBeVisible: Boolean) {
        binding.btnRightAction.visibility = if (isGoingToBeVisible) VISIBLE else GONE
    }

    fun setExpiryDate(label: CharSequence, value: CharSequence) {
        binding.tvExpiryDateLabel.text = label
        binding.tvExpiryDateValue.text = value
    }

    fun setCode(label: CharSequence, value: CharSequence) {
        binding.tvCodeLabel.text = label
        binding.tvCodeValue.text = value
    }

    fun setBrandLogo(@DrawableRes brandLogoRes: Int) {
        attemptBindDrawable(brandLogoRes, binding.ivBrandLogo)
    }
}
