package pe.com.scotiabank.blpm.android.client.base.onecolumn

import android.content.Context
import android.content.res.Resources
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.ProxyOfClickableLink
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setClickableDynamicDrawableSpan
import java.lang.ref.WeakReference

class TextBuilderForInfo(
    val weakAppContext: WeakReference<Context?>,
    private val callback: Runnable,
) : TextBuilder {

    private val empty: SpannableStringBuilder
        get() = SpannableStringBuilder.valueOf(Constant.EMPTY_STRING)

    override fun build(text: String): SpannableStringBuilder {
        val clickableSpan: ClickableSpan = ProxyOfClickableLink(callback)
        val resources: Resources = weakAppContext.get()?.resources ?: return empty
        val threeSpaceBlank =  Constant.SPACE_WHITE + Constant.SPACE_WHITE + Constant.SPACE_WHITE
        val fullText: String = text + threeSpaceBlank
        val drawableStartIndex: Int = fullText.lastIndex - Constant.ONE
        val drawableEndIndex: Int = fullText.lastIndex

        return SpannableStringBuilder
            .valueOf(fullText)
            .setClickableDynamicDrawableSpan(
                clickableSpan,
                resources,
                com.scotiabank.canvascore.R.drawable.canvascore_icon_tooltip,
                drawableStartIndex,
                drawableEndIndex,
            )
    }
}
