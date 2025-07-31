package pe.com.scotiabank.blpm.android.client.cardsettings.settings.shared

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import androidx.core.util.Supplier
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.color.ColorUtil
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setColorfulSpan
import java.lang.ref.WeakReference

class TextSupplierForBuddyTip(
    private val typefaceProvider: TypefaceProvider,
    private val weakAppContext: WeakReference<Context?>,
    private val buddyTipInfo: BuddyTipInfo,
) : Supplier<CharSequence> {

    private val empty: SpannableStringBuilder
        get() = SpannableStringBuilder.valueOf(Constant.EMPTY_STRING)

    override fun get(): CharSequence {

        @ColorInt val color: Int = weakAppContext.get()?.let(ColorUtil::getDarkBlueColor)
            ?: return empty

        val lightText: CharSequence = weakAppContext.get()
            ?.getString(buddyTipInfo.descriptionRes)
            .orEmpty()

        val textToBeBoldColoured: CharSequence = weakAppContext.get()
            ?.getString(buddyTipInfo.textToBeClickable)
            .orEmpty()

        val boldText: CharSequence = SpannableStringBuilder
            .valueOf(textToBeBoldColoured)
            .setColorfulSpan(color, typefaceProvider.boldTypeface, textToBeBoldColoured)

        return SpannableStringBuilder
            .valueOf(lightText)
            .append(Constant.SPACE_WHITE)
            .append(boldText)
    }
}