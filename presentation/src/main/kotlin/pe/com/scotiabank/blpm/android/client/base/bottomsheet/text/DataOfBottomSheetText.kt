package pe.com.scotiabank.blpm.android.client.base.bottomsheet.text

import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyTextType

class DataOfBottomSheetText(
    val attributes: AttrsBodyTextType,
    val callbackOfPrimaryButton: Runnable? = null,
    val callbackOfSecondaryButton: Runnable? = null,
    val buttonDirectionColumn: Boolean = false,
)
