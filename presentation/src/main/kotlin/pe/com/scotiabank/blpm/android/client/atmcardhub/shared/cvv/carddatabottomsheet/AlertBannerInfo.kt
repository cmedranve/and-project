package pe.com.scotiabank.blpm.android.client.atmcardhub.shared.cvv.carddatabottomsheet

import android.content.res.Resources
import android.text.SpannableStringBuilder
import com.scotiabank.canvascore.views.AlertBannerType
import com.scotiabank.enhancements.uuid.randomLong
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.atmcardhub.shared.hub.Action
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.instancereceiver.ProxyOfClickableLink
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setClickableSpan
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setUnderlineSpan
import pe.com.scotiabank.blpm.android.client.util.string.EMPTY
import java.lang.ref.WeakReference

enum class AlertBannerInfo(
    val id: Long,
    val type: AlertBannerType,
    val supportLink: Boolean,
    val emphasisLink: Boolean,
    val analyticsValue: String,
    val action: Action = Action.CARD_SETTINGS,
) {
    CARD_LOCKED(
        id = randomLong(),
        type = AlertBannerType.Warning,
        supportLink = true,
        emphasisLink = true,
        analyticsValue = "desbloquear-ahora"
    ) {
        override fun createTextContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
        ): CharSequence = weakResources.get()?.getString(R.string.need_unlock_card).orEmpty()

        override fun createEmphasisContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
            clickableSpan: ProxyOfClickableLink,
        ): CharSequence {
            val emphasisText: String = weakResources.get()?.getString(R.string.unlock_now).orEmpty()
            return SpannableStringBuilder
                .valueOf(emphasisText)
                .setClickableSpan(clickableSpan, typefaceProvider.boldTypeface, emphasisText)
                .setUnderlineSpan()
        }
    },

    ADDITIONAL_CARD_LOCKED(
        id = randomLong(),
        type = AlertBannerType.Warning,
        supportLink = false,
        emphasisLink = false,
        analyticsValue = String.EMPTY,
    ) {
        override fun createTextContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
        ): CharSequence = weakResources.get()?.getString(R.string.need_unlock_additional_card).orEmpty()

        override fun createEmphasisContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
            clickableSpan: ProxyOfClickableLink,
        ): CharSequence = String.EMPTY
    },

    CARD_PURCHASES_DISABLED(
        id = randomLong(),
        type = AlertBannerType.Warning,
        supportLink = true,
        emphasisLink = true,
        analyticsValue = "activar-ahora"
    ) {
        override fun createTextContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
        ): CharSequence = weakResources.get()?.getString(R.string.need_enable_online_shopping).orEmpty()

        override fun createEmphasisContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
            clickableSpan: ProxyOfClickableLink,
        ): CharSequence {
            val emphasisText: String = weakResources.get()?.getString(R.string.activate_now).orEmpty()
            return SpannableStringBuilder
                .valueOf(emphasisText)
                .setClickableSpan(clickableSpan, typefaceProvider.boldTypeface, emphasisText)
                .setUnderlineSpan()
        }
    },

    ADDITIONAL_CARD_PURCHASES_DISABLED(
        id = randomLong(),
        type = AlertBannerType.Warning,
        supportLink = false,
        emphasisLink = false,
        analyticsValue = String.EMPTY,
    ) {
        override fun createTextContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
        ): CharSequence = weakResources.get()
            ?.getString(R.string.need_enable_online_shopping_additional_card).orEmpty()

        override fun createEmphasisContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
            clickableSpan: ProxyOfClickableLink,
        ): CharSequence = String.EMPTY
    },

    CVV_ERROR(
        id = randomLong(),
        type = AlertBannerType.Error,
        supportLink = false,
        emphasisLink = false,
        analyticsValue = String.EMPTY,
    ) {
        override fun createTextContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
        ): CharSequence = weakResources.get()?.getString(R.string.card_data_cvv_error).orEmpty()

        override fun createEmphasisContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
            clickableSpan: ProxyOfClickableLink,
        ): CharSequence = String.EMPTY
    },

    FULL_ERROR(
        id = randomLong(),
        type = AlertBannerType.Error,
        supportLink = false,
        emphasisLink = false,
        analyticsValue = String.EMPTY,
    ) {
        override fun createTextContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
        ): CharSequence = weakResources.get()?.getString(R.string.card_data_full_error).orEmpty()

        override fun createEmphasisContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
            clickableSpan: ProxyOfClickableLink,
        ): CharSequence = String.EMPTY
    },

    UNAVAILABLE_CARD(
        id = randomLong(),
        type = AlertBannerType.Error,
        supportLink = true,
        emphasisLink = true,
        analyticsValue = "llamar-ahora",
        action = Action.CALL_NOW,
    ) {
        override fun createTextContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
        ): CharSequence {

            val textOne = weakResources.get()?.getString(R.string.card_data_unavailable_one).orEmpty()
            val textTwo = weakResources.get()?.getString(R.string.card_data_unavailable_two).orEmpty()

            val boldTextOne: CharSequence = SpannableStringBuilder
                .valueOf(textOne)
                .setTypefaceSpan(typeface = typefaceProvider.boldTypeface, shortText = textOne)

            return SpannableStringBuilder
                .valueOf(boldTextOne)
                .append(Constant.SPACE_WHITE)
                .append(textTwo)
        }

        override fun createEmphasisContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
            clickableSpan: ProxyOfClickableLink,
        ): CharSequence {
            val emphasisText: String = weakResources.get()?.getString(R.string.cards_settings_call_now).orEmpty()
            return SpannableStringBuilder
                .valueOf(emphasisText)
                .setClickableSpan(clickableSpan, typefaceProvider.boldTypeface, emphasisText)
                .setUnderlineSpan()
        }
    },

    NONE(
        id = randomLong(),
        type = AlertBannerType.Warning,
        supportLink = false,
        emphasisLink = false,
        analyticsValue = String.EMPTY,
    ) {
        override fun createTextContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
        ): CharSequence = String.EMPTY

        override fun createEmphasisContent(
            weakResources: WeakReference<Resources?>,
            typefaceProvider: TypefaceProvider,
            clickableSpan: ProxyOfClickableLink,
        ): CharSequence = String.EMPTY
    };

    abstract fun createTextContent(
        weakResources: WeakReference<Resources?>,
        typefaceProvider: TypefaceProvider,
    ): CharSequence

    abstract fun createEmphasisContent(
        weakResources: WeakReference<Resources?>,
        typefaceProvider: TypefaceProvider,
        clickableSpan: ProxyOfClickableLink,
    ): CharSequence
}
