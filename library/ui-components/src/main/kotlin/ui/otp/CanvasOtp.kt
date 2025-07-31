package pe.com.scotiabank.blpm.android.ui.otp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.scotiabank.canvascore.inputs.EditTextHelper
import pe.com.scotiabank.blpm.android.ui.databinding.CanvasOtpBinding

class CanvasOtp @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: CanvasOtpBinding = CanvasOtpBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    val eth1: EditTextHelper
        get() = binding.ethText1

    val eth2: EditTextHelper
        get() = binding.ethText2

    val eth3: EditTextHelper
        get() = binding.ethText3

    val eth4: EditTextHelper
        get() = binding.ethText4

    val eth5: EditTextHelper
        get() = binding.ethText5

    val eth6: EditTextHelper
        get() = binding.ethText6

    override fun setEnabled(enabled: Boolean) {
        eth1.isEnabled = enabled
        eth2.isEnabled = enabled
        eth3.isEnabled = enabled
        eth4.isEnabled = enabled
        eth5.isEnabled = enabled
        eth6.isEnabled = enabled
        super.setEnabled(enabled)
    }
}
