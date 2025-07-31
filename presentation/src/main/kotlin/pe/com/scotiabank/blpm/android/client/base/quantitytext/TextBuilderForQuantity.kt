package pe.com.scotiabank.blpm.android.client.base.quantitytext

import android.content.res.Resources
import android.text.SpannableStringBuilder
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.app.AppModel
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import java.lang.ref.WeakReference

class TextBuilderForQuantity(
    private val appModel: AppModel,
    private val weakResources: WeakReference<Resources?>,
    private val boldText: String,
) {

    private val empty: SpannableStringBuilder
        get() = SpannableStringBuilder.valueOf(Constant.EMPTY_STRING)

    fun build(total: Int): CharSequence {
        val fullText: String = weakResources.get()
            ?.getString(R.string.parenthesis_dual_placeholder, boldText, total)
            ?: return empty
        return SpannableStringBuilder
            .valueOf(fullText)
            .setTypefaceSpan(appModel.boldTypeface, boldText)
    }
}
