package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.DateUtil
import pe.com.scotiabank.blpm.android.client.util.color.ColorUtil
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setColorfulSpan
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.BuddyTipType
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import java.lang.ref.WeakReference
import java.util.Date
import kotlin.time.Duration

enum class TimerInfo(
    val type: BuddyTipType = BuddyTipType.NON_EXPANDABLE,
) {
    HIDDEN_DATA(
        type = BuddyTipType.EXPANDABLE
    ) {
        override fun createDescriptionBuilder(
            weakAppContext: WeakReference<Context?>,
            typefaceProvider: TypefaceProvider,
            duration: Duration?,
        ): SpannableStringBuilder {

            val cvvIsSingleUse: String = weakAppContext.get()
                ?.getString(R.string.cvv_is_single_use_for_each_purchase).orEmpty()

            @ColorInt val darkBlueColor: Int = weakAppContext.get()?.let(ColorUtil::getDarkBlueColor)
                ?: return empty
            val howDoesItWorkRaw: CharSequence = weakAppContext.get()
                ?.getString(R.string.how_does_it_work).orEmpty()
            val howDoesItWorkColoured: CharSequence = SpannableStringBuilder
                .valueOf(howDoesItWorkRaw)
                .setColorfulSpan(darkBlueColor, typefaceProvider.boldTypeface, howDoesItWorkRaw)

            return SpannableStringBuilder
                .valueOf(cvvIsSingleUse)
                .append(Constant.LINE_BREAK)
                .append(howDoesItWorkColoured)
        }
    },

    DECRYPTED_DATA(
        type = BuddyTipType.EXPANDABLE
    ) {
        override fun createDescriptionBuilder(
            weakAppContext: WeakReference<Context?>,
            typefaceProvider: TypefaceProvider,
            duration: Duration?,
        ): SpannableStringBuilder {
            if (duration == null) return empty

            val cvvIsSingleUse: String = weakAppContext.get()
                ?.getString(R.string.cvv_is_single_use_for_each_purchase_and_expires_in)
                .orEmpty()

            val date = Date(duration.inWholeMilliseconds)
            val timer: String = DateUtil.convertDateToString(Constant.MM_SS, date)
            val timeBold: CharSequence = SpannableStringBuilder
                .valueOf(timer)
                .setTypefaceSpan(typeface = typefaceProvider.boldTypeface, shortText = timer)

            val minutesRaw: CharSequence = weakAppContext.get()
                ?.getString(R.string.debit_card_timer_minutes).orEmpty()
            val minutesBold: CharSequence = SpannableStringBuilder
                .valueOf(minutesRaw)
                .setTypefaceSpan(typeface = typefaceProvider.boldTypeface, shortText = minutesRaw)

            @ColorInt val darkBlueColor: Int = weakAppContext.get()?.let(ColorUtil::getDarkBlueColor)
                ?: return SpannableStringBuilder.valueOf(String.EMPTY)
            val howDoesItWorkRaw: CharSequence = weakAppContext.get()
                ?.getString(R.string.how_does_it_work).orEmpty()
            val howDoesItWorkColoured: CharSequence = SpannableStringBuilder
                .valueOf(howDoesItWorkRaw)
                .setColorfulSpan(darkBlueColor, typefaceProvider.boldTypeface, howDoesItWorkRaw)

            return SpannableStringBuilder
                .valueOf(cvvIsSingleUse)
                .append(Constant.SPACE_WHITE)
                .append(timeBold)
                .append(Constant.SPACE_WHITE)
                .append(minutesBold)
                .append(Constant.LINE_BREAK)
                .append(howDoesItWorkColoured)
        }
    },

    DECRYPTED_DATA_WITHOUT_CVV {
        override fun createDescriptionBuilder(
            weakAppContext: WeakReference<Context?>,
            typefaceProvider: TypefaceProvider,
            duration: Duration?,
        ): SpannableStringBuilder {
            if (duration == null) return empty

            val inRaw: CharSequence = weakAppContext.get()
                ?.getString(R.string.debit_card_timer_in).orEmpty()
            val inBold: CharSequence = SpannableStringBuilder
                .valueOf(inRaw)
                .setTypefaceSpan(typeface = typefaceProvider.boldTypeface, shortText = inRaw)

            val date = Date(duration.inWholeMilliseconds)
            val timer: String = DateUtil.convertDateToString(Constant.MM_SS, date)
            val timeBold: CharSequence = SpannableStringBuilder
                .valueOf(timer)
                .setTypefaceSpan(typeface = typefaceProvider.boldTypeface, shortText = timer)

            val minutesRaw: CharSequence = weakAppContext.get()
                ?.getString(R.string.debit_card_timer_minutes).orEmpty()
            val minutesBold: CharSequence = SpannableStringBuilder
                .valueOf(minutesRaw)
                .setTypefaceSpan(typeface = typefaceProvider.boldTypeface, shortText = minutesRaw)

            val willBeHidden: String = weakAppContext.get()
                ?.getString(R.string.card_data_will_be_hidden)
                .orEmpty()

            return SpannableStringBuilder
                .valueOf(inBold)
                .append(Constant.SPACE_WHITE)
                .append(timeBold)
                .append(Constant.SPACE_WHITE)
                .append(minutesBold)
                .append(Constant.SPACE_WHITE)
                .append(willBeHidden)
        }
    };

    val empty: SpannableStringBuilder
        get() = SpannableStringBuilder.valueOf(String.EMPTY)

    abstract fun createDescriptionBuilder(
        weakAppContext: WeakReference<Context?>,
        typefaceProvider: TypefaceProvider,
        duration: Duration?,
    ): SpannableStringBuilder
}
