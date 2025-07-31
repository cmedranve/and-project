package pe.com.scotiabank.blpm.android.ui.slider

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.Function
import com.scotiabank.canvascore.views.CanvasTextView
import pe.com.scotiabank.blpm.android.ui.databinding.CanvasSliderBinding

class CanvasSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: CanvasSliderBinding = CanvasSliderBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    val tvTitle: CanvasTextView
        get() = binding.tvTitle

    fun setAttributes(
        formattingCallback: Function<Float, CharSequence>,
        maxValueLabel: CharSequence,
        maxValue: Float,
        stepSize: Float = 0f,
        minValue: Float = 0f,
        initialValue: Float = minValue,
        currentValue: Float? = null,
        changeCallback: CallbackOfValueChange? = null
    ) {
        binding.slider.clearOnChangeListeners()
        val value: Float = currentValue ?: initialValue
        initBottomLabelValues(value, formattingCallback, maxValueLabel)
        initNumericalValues(maxValue, stepSize, minValue, value)
        setCallbackOfValueChange(formattingCallback, changeCallback)
        setOnClickListeners()
    }

    private fun initBottomLabelValues(
        value: Float,
        formattingCallback: Function<Float, CharSequence>,
        maxValueLabel: CharSequence
    ) {
        binding.tvValueLabel.text = formattingCallback.apply(value)
        binding.tvMaxValueLabel.text = maxValueLabel
    }

    private fun initNumericalValues(
        maxValue: Float,
        stepSize: Float,
        minValue: Float,
        value: Float,
    ) {
        binding.slider.valueFrom = minValue
        binding.slider.valueTo = maxValue
        binding.slider.stepSize = stepSize
        binding.slider.value = value
    }

    private fun setCallbackOfValueChange(
        formattingCallback: Function<Float, CharSequence>,
        changeCallback: CallbackOfValueChange?,
    ) {
        binding.slider.addOnChangeListener { _, value, fromUser ->
            binding.tvValueLabel.text = formattingCallback.apply(value)
            changeCallback?.onValueChange(value, fromUser)
        }
    }

    private fun setOnClickListeners() {
        binding.cvDown.setOnClickListener { onDownCardClicked() }
        binding.cvUp.setOnClickListener { onUpCardClicked() }
    }

    private fun onDownCardClicked() {
        enableClickListeners(false)
        if (binding.slider.value > binding.slider.valueFrom) {
            binding.slider.value--
        }
        enableClickListeners(true)
    }

    private fun enableClickListeners(isGoingToBeEnabled: Boolean) {
        enableClickListenerOn(binding.cvDown, isGoingToBeEnabled)
        enableClickListenerOn(binding.cvUp, isGoingToBeEnabled)
    }

    private fun enableClickListenerOn(view: View, isGoingToBeEnabled: Boolean) {
        view.isEnabled = isGoingToBeEnabled
        view.isClickable = isGoingToBeEnabled
    }

    private fun onUpCardClicked() {
        enableClickListeners(false)
        if (binding.slider.value < binding.slider.valueTo) {
            binding.slider.value++
        }
        enableClickListeners(true)
    }
}
