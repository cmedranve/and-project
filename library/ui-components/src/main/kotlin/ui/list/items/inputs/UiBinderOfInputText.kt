package pe.com.scotiabank.blpm.android.ui.list.items.inputs

import android.text.InputFilter
import android.view.View
import android.widget.EditText
import com.scotiabank.canvascore.inputs.FormInputView
import com.scotiabank.canvascore.inputs.InputErrorView
import com.scotiabank.canvascore.views.CanvasTextView

object UiBinderOfInputText {

    /**
     * We assess that the CanvasEditText's text is different than the UiEntityOfEditText's text
     * to prevent setting the very same text.
     *
     * When we need to clear CanvasEditText's text, we don't call to FormInputView.reset()
     * because it would trigger a watcher event.
     *
     * Since the text is set by the the logic below instead of the user-typing,
     * we call to setSelection(int) to prevent leaving out the cursor at the wrong position.
     * */
    @JvmStatic
    internal fun setTextWithoutTextWatcher(text: CharSequence, fiv: FormInputView) {
        if (text.contentEquals(fiv.text)) return

        fiv.setTextWithoutTextWatcher(text)
        fiv.setSelection(fiv.text.length)
    }

    @JvmStatic
    internal fun setFiltersIfDifferent(filters: Array<InputFilter>, inputField: EditText) {
        val isEquals: Boolean = filters.contentEquals(inputField.filters)
        if (isEquals) return
        inputField.filters = filters
    }

    @JvmStatic
    fun bindErrorView(errorText: CharSequence, fiv: FormInputView, iev: InputErrorView, ctvError: CanvasTextView) {
        fiv.bindErrorView(iev)
        if (errorText.isBlank()) {
            fiv.clearError()
            ctvError.visibility = View.GONE
            return
        }
        fiv.setError(errorText)
        ctvError.text = errorText
        ctvError.visibility = View.VISIBLE
    }

    @JvmStatic
    fun showSupplementaryIfNotBlank(
        supplementaryText: CharSequence,
        ctvSupplementary: CanvasTextView,
    ) {
        ctvSupplementary.text = supplementaryText
        ctvSupplementary.visibility = if (supplementaryText.isBlank()) View.GONE else View.VISIBLE
    }
}
