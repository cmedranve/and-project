package pe.com.scotiabank.blpm.android.client.base.onecolumn

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setColorfulSpan
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan

class TextBuilderForDefault(
    private val typeface: Typeface? = null,
    @ColorInt private val color: Int? = null,
    private val shortText: CharSequence? = null,
) : TextBuilder {

    override fun build(text: String): SpannableStringBuilder {
        val shortText = shortText ?: text

        if (typeface != null && color != null) {
            return SpannableStringBuilder.valueOf(text).setColorfulSpan(
                color = color,
                typeface = typeface,
                shortText = shortText
            )
        }

        if (typeface != null) {
            return SpannableStringBuilder.valueOf(text).setTypefaceSpan(
                typeface = typeface,
                shortText = shortText
            )
        }

        return SpannableStringBuilder.valueOf(text)
    }
}
