package pe.com.scotiabank.blpm.android.ui.list.items.richtext.style

import android.text.TextPaint
import android.text.style.UnderlineSpan

class UnderlineOrNotSpan(private val isUnderline: Boolean = false) : UnderlineSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
    }

}
