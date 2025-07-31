package pe.com.scotiabank.blpm.android.client.base.onecolumn

import android.text.SpannableStringBuilder
import pe.com.scotiabank.blpm.android.client.util.Constant

interface TextBuilder {

    fun build(text: String = Constant.EMPTY_STRING): SpannableStringBuilder
}
