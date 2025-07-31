package pe.com.scotiabank.blpm.android.client.base.bottomsheet.list

import com.scotiabank.canvascore.bottomsheet.model.AttrsBodyListType

class StaticDataOfBottomSheetList(
    val attributes: AttrsBodyListType,
    val dividerPositions: List<Int> = emptyList(),
    val isCloseButtonVisible: Boolean = true,
    val isCancelable: Boolean = true,
    val callbackOfPrimaryButton: Runnable? = null,
    val callbackOfSecondaryButton: Runnable? = null,
    val id: Any? = null,
)
