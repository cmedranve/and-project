package pe.com.scotiabank.blpm.android.client.base.operation.token.validation.personalbanking

import android.content.res.Resources
import android.text.SpannableStringBuilder
import pe.com.scotiabank.blpm.android.client.R
import pe.com.scotiabank.blpm.android.client.base.font.TypefaceProvider
import pe.com.scotiabank.blpm.android.client.util.Constant
import pe.com.scotiabank.blpm.android.client.util.spannablestringbuilder.setTypefaceSpan
import pe.com.scotiabank.blpm.android.ui.list.items.buddytip.UiEntityOfBuddyTip
import pe.com.scotiabank.blpm.android.ui.list.items.padding.UiEntityOfPadding
import java.lang.ref.WeakReference

class FactoryOfBiometricBuddyTip(
    private val typefaceProvider: TypefaceProvider,
    private val weakResources: WeakReference<Resources?>,
) {

    private val empty: SpannableStringBuilder
        get() = SpannableStringBuilder.valueOf(Constant.EMPTY_STRING)

    fun create(): UiEntityOfBuddyTip {
        return UiEntityOfBuddyTip(
            paddingEntity = UiEntityOfPadding(),
            iconRes = R.drawable.ic_fingerprint_black_24dp,
            descriptionBuilder = createText(),
        )
    }

    private fun createText(): SpannableStringBuilder {
        val boldText: String = weakResources.get()
            ?.getString(R.string.biometric_transfer_other_account_banner_bold_message)
            ?: return empty
        val fullText: String = weakResources.get()
            ?.getString(R.string.biometric_transfer_other_account_banner)
            ?: return empty
        return SpannableStringBuilder
            .valueOf(fullText)
            .setTypefaceSpan(typefaceProvider.boldTypeface, boldText)
    }
}
